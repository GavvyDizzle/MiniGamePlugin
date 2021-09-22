package me.gavindimpter.minigameplugin.boards;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.gavindimpter.minigameplugin.MiniGamePlugin;
import me.gavindimpter.minigameplugin.blocks.minesweeper.MinesweeperBlock;
import me.gavindimpter.minigameplugin.blocks.minesweeper.MinesweeperEmptyBlock;
import me.gavindimpter.minigameplugin.blocks.minesweeper.MinesweeperFilledBlock;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class MinesweeperBoard implements GameBoard{

    private final int id;
    private final int cols;
    private final int rows;
    private final UUID playerUUID;
    private final Location originLocation;
    private final MinesweeperBlock[][] gameBoard;

    // Minesweeper specific info
    private boolean isGameOver;
    private final int numMines;
    private int numFlags;
    private final int numSafeSquares;
    private int numSafeSquaresRevealed;
    private final Hologram flagsRemaining;
    private final Hologram timeDisplay;
    private BukkitTask bukkitTask;

    public MinesweeperBoard(int cols, int rows, int numMines, Player player, Location originLocation, int id) {
        this.id = id;
        this.cols = cols;
        this.rows = rows;
        this.playerUUID = player.getUniqueId();
        this.originLocation = originLocation;

        this.isGameOver = false;
        this.numMines = numMines;
        this.numSafeSquares = cols * rows - numMines;
        this.numSafeSquaresRevealed = 0;

        Location flagRemainingDisplayLocation = new Location(originLocation.getWorld(), originLocation.getBlockX() + 0.5, originLocation.getBlockY() + 0.5 + rows, originLocation.getBlockZ() - 0.25);
        flagsRemaining = HologramsAPI.createHologram(MiniGamePlugin.getInstance(), flagRemainingDisplayLocation);
        flagsRemaining.appendTextLine("Flags: " + numMines);
        Location timeRemainingLocation = new Location(originLocation.getWorld(), originLocation.getBlockX() + 1.5 - cols, originLocation.getBlockY() + 0.5 + rows, originLocation.getBlockZ() - 0.25);
        timeDisplay = HologramsAPI.createHologram(MiniGamePlugin.getInstance(), timeRemainingLocation);
        timeDisplay.appendTextLine("Time : 0");

        bukkitTask = null;
        startGameTimer();

        gameBoard = new MinesweeperBlock[cols][rows];
        initializeGameBoard();
        setGameBoardInWorld();
    }

    /**
     * Sets all values of the GameBoard
     */
    private void initializeGameBoard() {
        int x = originLocation.getBlockX();
        int y = originLocation.getBlockY();
        int z = originLocation.getBlockZ();
        World world = originLocation.getWorld();

        // Sets all tiles to empty
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                gameBoard[col][row] = new MinesweeperEmptyBlock(new Location(world, x - col, y + row, z));
            }
        }

        // Randomly adds numMines mines to the gameBoard
        int numMinesAdded = 0;
        while (numMinesAdded < numMines) {
            int col = (int) (Math.random() * (cols));
            int row = (int) (Math.random() * (rows));
            if (gameBoard[col][row] instanceof MinesweeperEmptyBlock) {
                gameBoard[col][row] = new MinesweeperFilledBlock(gameBoard[col][row].getLocation());
                numMinesAdded++;
            }
        }

        // Outer 2 for loops determine which tiles are empty
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                if (gameBoard[col][row] instanceof MinesweeperEmptyBlock) {

                    // Calculates the number of mines adjacent to this tile
                    // For loops handle out of bounds checks
                    int numAdjacentMines = 0;
                    for (int c = Math.max(0, col - 1); c < Math.min(cols, col + 2); c++) {
                        for (int r = Math.max(0, row - 1); r < Math.min(rows, row + 2); r++){
                            if (gameBoard[c][r] instanceof MinesweeperFilledBlock) {
                                numAdjacentMines++;
                            }
                        }
                    }
                    if (numAdjacentMines > 0) {
                        MinesweeperEmptyBlock t = new MinesweeperEmptyBlock(gameBoard[col][row].getLocation());
                        t.setNumAdjacentMines(numAdjacentMines);
                        gameBoard[col][row] = t;
                    }
                }
            }
        }
    }

    /**
     * Sets all blocks in the world for the Board
     */
    private void setGameBoardInWorld() {
        // Place blocks into the world and removes the layer of pots (sets to air)
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                gameBoard[col][row].getLocation().getBlock().setType( gameBoard[col][row].getMaterial() );
                Location buttonLayer = new Location(originLocation.getWorld(), gameBoard[col][row].getLocation().getBlockX(), gameBoard[col][row].getLocation().getBlockY(), gameBoard[col][row].getLocation().getBlockZ());
                buttonLayer.setZ( buttonLayer.getBlockZ() - 1 );
                buttonLayer.getBlock().setType( Material.AIR );
            }
        }
        //sets black outline (1 thick)
        for (int col = -1; col <= cols; col++) {
            for (int row = -1; row <= rows; row++) {
                if (row <= -1 || row >= rows || col <= -1 || col >= cols) {
                    Location newLoc = new Location(originLocation.getWorld(), originLocation.getBlockX() - col, originLocation.getBlockY() + row, originLocation.getBlockZ());
                    newLoc.getBlock().setType(Material.BLACK_CONCRETE);
                }
            }
        }
    }

    /**
     * Removes all the blocks and Holograms created by the GameBoard
     * Sets the box bounded by the GameBoard to air
     */
    @Override
    public void removeGameBoardFromWorld() {
        // Remove blocks
        for (int z = originLocation.getBlockZ() - 1; z <= originLocation.getBlockZ(); z++) {
            for (int y = originLocation.getBlockY() - 3; y < originLocation.getBlockY() + rows + 3; y++) {
                for (int x = originLocation.getBlockX() + 3; x > originLocation.getBlockX() - cols - 3; x--) {
                    Location loc = new Location(originLocation.getWorld(), x, y, z);
                    loc.getBlock().setType(Material.AIR);
                }
            }
        }

        // Remove Holograms
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                if (gameBoard[col][row].getNumAdjacentMinesHologram() != null) {
                    gameBoard[col][row].getNumAdjacentMinesHologram().delete();
                }
            }
        }
        flagsRemaining.delete();
        timeDisplay.delete();
        bukkitTask.cancel();
    }

    /**
     * Starts the timer that counts the seconds the game has been played for
     */
    private void startGameTimer() {
        bukkitTask = new BukkitRunnable() {
            int secondsCount = 0;

            @Override
            // Count seconds and updates the Time display until the game is over
            public void run() {

                if (isGameOver) {
                    this.cancel();
                    return;
                }
                timeDisplay.removeLine(0);
                timeDisplay.appendTextLine("Time: " + secondsCount);
                secondsCount++;
            }
        }.runTaskTimer(MiniGamePlugin.getInstance(), 0L, 20L);
    }

    /**
     * Updated the flags display at the top left of the board in the world
     */
    public void updateFlagsRemaining() {
        flagsRemaining.removeLine(0);
        flagsRemaining.appendTextLine("Flags: " + (numMines - numFlags));
    }

    /**
     * WIN - If all non-mine blocks are revealed
     * LOSS - If a mine was clicked
     *
     * @param p The player to message if they won or lost
     */
    @Override
    public void checkForGameOver(Player p) {
        if (isGameOver) {
            p.sendMessage(ChatColor.RED + "You lost Minesweeper");

            // Reveals the rest of the game board, setting incorrectly flagged blocks to red concrete
            for (int col = 0; col < cols; col++) {
                for (int row = 0; row < rows; row++) {
                    gameBoard[col][row].setRevealed(true);
                    if (gameBoard[col][row] instanceof MinesweeperEmptyBlock) {
                        if (!gameBoard[col][row].isRevealed()) {
                            (gameBoard[col][row]).placeNumAdjacentMines();
                        }
                        if (gameBoard[col][row].isFlagPlaced()) {
                            gameBoard[col][row].getLocation().getBlock().setType(Material.RED_CONCRETE);
                        }
                        else {
                            gameBoard[col][row].getLocation().getBlock().setType(Material.IRON_BLOCK);
                        }
                    }
                    else {
                        gameBoard[col][row].getLocation().getBlock().setType(Material.TNT);
                    }
                }
            }
        }
        else if (numSafeSquaresRevealed == numSafeSquares) {
            p.sendMessage(ChatColor.GREEN + "You won Minesweeper");
            isGameOver = true;

            for (int col = 0; col < cols; col++) {
                for (int row = 0; row < rows; row++) {

                    if (gameBoard[col][row] instanceof MinesweeperFilledBlock && !gameBoard[col][row].isFlagPlaced()) {
                        gameBoard[col][row].toggleFlagPlacement();
                        addFlag();
                        updateFlagsRemaining();
                    }
                }
            }
        }
    }


    /**
     * Recursive method that reveals all adjacent empty blocks
     * and the blocks directly adjacent to them
     *
     * @param col The column of the index of the block in the GameBoard
     * @param row The row of the index of the block in the GameBoard
     */
    public void revealEmptyAdjacentTiles(int col, int row) {
        for (int i = Math.max(0, col - 1); i < Math.min(cols, col + 2); i++) {
            for (int j = Math.max(0, row - 1); j < Math.min(rows, row + 2); j++){

                // If the block isn't revealed, and it's not flagged
                if ( !gameBoard[i][j].isRevealed() && !gameBoard[i][j].isFlagPlaced()) {

                    // The following code imitates a left click without directly calling the method

                    gameBoard[i][j].setRevealed(true);
                    gameBoard[i][j].getLocation().getBlock().setType(gameBoard[i][j].getRevealedMaterial());
                    safeSquareRevealed();
                    gameBoard[i][j].placeNumAdjacentMines();

                    if ( gameBoard[i][j].getNumAdjacentMines() == 0 ) {
                        revealEmptyAdjacentTiles(i, j);
                    }
                }
            }
        }
    }

    /**
     * Reveals only the 8 adjacent blocks to the clicked block
     * Calls revealEmptyAdjacentTiles() if a revealed block has no adjacent mines
     *
     * @param col The column of the index of the block in the GameBoard
     * @param row The row of the index of the block in the GameBoard
     */
    public void revealAdjacentTiles(int col, int row) {

        int numFlags = 0;
        int numMines = 0;

        // Continue if the 8 adjacent tiles have the number of flags equal to "numAdjacentMines"
        for (int i = Math.max(0, col - 1); i < Math.min(cols, col + 2); i++) {
            for (int j = Math.max(0, row - 1); j < Math.min(rows, row + 2); j++) {

                if (gameBoard[i][j] instanceof MinesweeperFilledBlock) {
                    numMines++;
                }
                if (gameBoard[i][j].isFlagPlaced()) {
                    numFlags++;
                }
            }
        }

        // If the flags differ from the number of mines
        if (numFlags != numMines) {
            return;
        }

        // Checks the 8 surrounding blocks
        for (int i = Math.max(0, col - 1); i < Math.min(cols, col + 2); i++) {
            for (int j = Math.max(0, row - 1); j < Math.min(rows, row + 2); j++){

                // If the block isn't revealed, and it's not flagged
                if ( !gameBoard[i][j].isRevealed() && !gameBoard[i][j].isFlagPlaced()) {

                    // The following code imitates a left click without directly calling the method

                    gameBoard[i][j].setRevealed(true);
                    gameBoard[i][j].getLocation().getBlock().setType(gameBoard[i][j].getRevealedMaterial());
                    safeSquareRevealed();
                    gameBoard[i][j].placeNumAdjacentMines();

                    if ( gameBoard[i][j].getNumAdjacentMines() == 0 ) {
                        revealEmptyAdjacentTiles(i, j);
                    }
                }
            }
        }
    }

    @Override
    public Location getOriginLocation() {
        return originLocation;
    }

    // Called when a flagged block is right-clicked
    public void addFlag() {
        numFlags++;
    }

    // Called when a non-flagged block is right-clicked
    public void removeFlag() {
        numFlags--;
    }

    // Called when an empty block is left-clicked
    public void safeSquareRevealed() {
        numSafeSquaresRevealed++;
    }


    /* GETTERS & SETTERS */

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public int getID() {
        return id;
    }

    public MinesweeperBlock[][] getGameBoard() {
        return gameBoard;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    // Called when a mine is left-clicked
    public void setGameOver(boolean b){
        isGameOver = b;
    }
}
