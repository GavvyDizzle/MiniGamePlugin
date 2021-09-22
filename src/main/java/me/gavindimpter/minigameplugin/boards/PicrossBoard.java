package me.gavindimpter.minigameplugin.boards;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.gavindimpter.minigameplugin.MiniGamePlugin;
import me.gavindimpter.minigameplugin.blocks.picross.PicrossEmptyBlock;
import me.gavindimpter.minigameplugin.blocks.picross.PicrossFilledBlock;
import me.gavindimpter.minigameplugin.blocks.picross.PicrossBlock;
import me.gavindimpter.minigameplugin.blocks.Block;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.UUID;

public class PicrossBoard implements GameBoard{

    private final int id;
    private final int cols;
    private final int rows;
    private final UUID playerUUID;
    private final Location originLocation;
    private final PicrossBlock[][] gameBoard;

    // Picross specific info
    private int numFilledTiles;
    private int filledTilesClicked;
    private int numMistakes;
    private boolean isPerfect;
    private boolean isGameOver;
    private BukkitTask bukkitTask;
    private final Hologram[] columnInfo;
    private final Hologram[] rowInfo;

    public PicrossBoard(int cols, int rows, Player player, Location originLocation, int id) {

        this.id = id;
        this.cols = cols;
        this.rows = rows;
        this.playerUUID = player.getUniqueId();
        this.originLocation = originLocation;

        this.numFilledTiles = 0;
        this.filledTilesClicked = 0;
        this.numMistakes = 0;
        this.isPerfect = true;
        this.isGameOver = false;
        this.bukkitTask = null;

        gameBoard = new PicrossBlock[cols][rows];
        initializeGameBoard();
        setGameBoardInWorld();

        columnInfo = new Hologram[cols];
        createColumnInfo();
        rowInfo = new Hologram[rows];
        createRowInfo();
    }

    /**
     * Sets all values of the GameBoard to half filled and half empty tiles
     */
    private void initializeGameBoard(){
        int x = originLocation.getBlockX();
        int y = originLocation.getBlockY();
        int z = originLocation.getBlockZ();
        World world = originLocation.getWorld();


        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                double rand = Math.random();
                if (rand < 0.5) {
                    gameBoard[col][row] = new PicrossFilledBlock( new Location(world, x - col, y + row, z));
                    numFilledTiles++;
                }
                else {
                    gameBoard[col][row] = new PicrossEmptyBlock( new Location(world, x - col, y + row, z));
                }
            }
        }
    }

    /**
     * Sets all blocks in the world for the Board
     */
    private void setGameBoardInWorld() {
        // Place blocks into the world and removes the layer of buttons (sets to air)
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                gameBoard[col][row].getLocation().getBlock().setType( gameBoard[col][row].getMaterial() );
                Location buttonLayer = new Location(originLocation.getWorld(), gameBoard[col][row].getLocation().getBlockX(), gameBoard[col][row].getLocation().getBlockY(), gameBoard[col][row].getLocation().getBlockZ());
                buttonLayer.setZ( buttonLayer.getBlockZ() - 1 );
                buttonLayer.getBlock().setType( Material.AIR );
            }
        }
        // Sets black outline
        for (int col = -2; col <= cols + 1; col++) {
            for (int row = -2; row <= rows + 1; row++) {
                if (row <= -1 || row >= rows || col <= -1 || col >= cols) {
                    Location newLoc = new Location(originLocation.getWorld(), originLocation.getBlockX() - col, originLocation.getBlockY() + row, originLocation.getBlockZ());
                    newLoc.getBlock().setType(Material.BLACK_CONCRETE);
                }
            }
        }
        // Sets extruded black outline
        for (int col = -3; col <= cols + 2; col++) {
            for (int row = -3; row <= rows + 2; row++) {
                if (row == -3 || row == rows + 2 || col == -3 || col == cols + 2) {
                    Location newLoc = new Location(originLocation.getWorld(), originLocation.getBlockX() - col, originLocation.getBlockY() + row, originLocation.getBlockZ() - 1);
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
        // Stops the bukkitRunnable task if it is running
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }

        // Remove Holograms
        for (Hologram h : columnInfo) {
            h.delete();
        }
        for (Hologram h : rowInfo) {
            h.delete();
        }
    }

    /**
     * Creates each column's information Hologram and places it into the world,
     * saving it to the array columnInfo
     */
    private void createColumnInfo() {
        for (int col = 0; col < cols; col++) {
            ArrayList<Integer> filledNums = new ArrayList<>();
            int countFilled = 0;
            for (int row = rows - 1; row >= 0; row--) {
                //if this is filled
                if (gameBoard[col][row].isFilled()) {
                    countFilled++;
                }
                //if this isn't filled
                else if (countFilled > 0){
                    filledNums.add(countFilled);
                    countFilled = 0;
                }

                //add last number to the array if nonzero
                if (row == 0 && countFilled > 0) {
                    filledNums.add(countFilled);
                }
            }

            Location infoLocation = new Location(originLocation.getWorld(), originLocation.getBlockX() + 0.5 - col, originLocation.getBlockY() + rows + (0.25 * filledNums.size()), originLocation.getBlockZ() - 0.25);

            Hologram hologram = HologramsAPI.createHologram(MiniGamePlugin.getInstance(), infoLocation);

            for (int i = 0; i < filledNums.size(); i++) {
                hologram.insertTextLine(i, String.valueOf(filledNums.get(i)));
            }

            columnInfo[col] = hologram;
        }
    }

    /**
     * Creates each row's information Hologram and places it into the world,
     * saving it to the array rowInfo
     */
    private void createRowInfo() {
        for (int row = 0; row < rows; row++) {
            ArrayList<Integer> filledNums = new ArrayList<>();
            int countFilled = 0;
            for (int col = 0; col < cols; col++) {
                //if this is filled
                if (gameBoard[col][row].isFilled()) {
                    countFilled++;
                }
                //if this isn't filled
                else if (countFilled > 0){
                    filledNums.add(countFilled);
                    countFilled = 0;
                }

                //add last number to the array if nonzero
                if (col == cols - 1 && countFilled > 0) {
                    filledNums.add(countFilled);
                }
            }
            Location infoLocation = new Location(originLocation.getWorld(), originLocation.getBlockX() + 1 + (0.125 * filledNums.size()), originLocation.getBlockY() + row + 0.62, originLocation.getBlockZ() - 0.25);

            // Formats the row info
            StringBuilder infoString = new StringBuilder(" ");
            for (int n : filledNums) {
                infoString.append(n).append(" ");
            }

            Hologram hologram = HologramsAPI.createHologram(MiniGamePlugin.getInstance(), infoLocation);
            hologram.appendTextLine(infoString.toString().trim());
            rowInfo[row] = hologram;
        }
    }

    /**
     * WIN - If all blue blocks are revealed and no mistakes were made
     * LOSS - If all blue blocks are revealed and at least one mistake was made
     *
     * @param p The player to message if they won or lost
     */
    public void checkForGameOver(Player p) {
        if (!isGameOver && filledTilesClicked == numFilledTiles && isPerfect) {
            p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You win!");
            setEmptyTilesWhite();
            win();
            isGameOver = true;
        }
        else if (!isGameOver && filledTilesClicked == numFilledTiles){
            p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You won with " + numMistakes + " mistakes!");
            setEmptyTilesWhite();
            isGameOver = true;
        }
    }

    /**
     * Creates a repeating BukkitRunnable that makes all the filled blocks strobe in a rainbow pattern
     */
    // Called when the player wins the game
    private void win() {
        bukkitTask = new BukkitRunnable() {
            int count = 0;
            final Material red = Material.RED_CONCRETE;
            final Material orange = Material.ORANGE_CONCRETE;
            final Material yellow = Material.YELLOW_CONCRETE;
            final Material l_green = Material.LIME_CONCRETE;
            final Material green = Material.GREEN_CONCRETE;
            final Material cyan = Material.CYAN_CONCRETE;
            final Material l_blue = Material.LIGHT_BLUE_CONCRETE;
            final Material blue = Material.BLUE_CONCRETE;
            final Material purple = Material.PURPLE_CONCRETE;
            final Material magenta = Material.MAGENTA_CONCRETE;
            final Material pink = Material.PINK_CONCRETE;

            @Override
            //makes a rainbow of colors that changes every tick for the first 5 seconds after winning
            public void run() {

                count++;
                if (count == 50) {
                    this.cancel();
                    return;
                }

                for (int col = 0; col < cols; col++) {
                    for (int row = 0; row < rows; row++) {
                        if (gameBoard[col][row].isFilled()) {
                            Location loc = gameBoard[col][row].getLocation();
                            switch ((col + count) % 11) {
                                case 0:
                                    loc.getBlock().setType(red);
                                    break;
                                case 1:
                                    loc.getBlock().setType(orange);
                                    break;
                                case 2:
                                    loc.getBlock().setType(yellow);
                                    break;
                                case 3:
                                    loc.getBlock().setType(l_green);
                                    break;
                                case 4:
                                    loc.getBlock().setType(green);
                                    break;
                                case 5:
                                    loc.getBlock().setType(cyan);
                                    break;
                                case 6:
                                    loc.getBlock().setType(l_blue);
                                    break;
                                case 7:
                                    loc.getBlock().setType(blue);
                                    break;
                                case 8:
                                    loc.getBlock().setType(purple);
                                    break;
                                case 9:
                                    loc.getBlock().setType(magenta);
                                    break;
                                case 10:
                                    loc.getBlock().setType(pink);
                                    break;
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(MiniGamePlugin.getInstance(), 0L, 2L);
    }

    /**
     * Sets all the empty blocks to white concrete
     */
    // Called when the game ends
    private void setEmptyTilesWhite() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                if (!gameBoard[col][row].isFilled()) {
                    Location loc = gameBoard[col][row].getLocation();
                    loc.getBlock().setType(Material.WHITE_CONCRETE);
                }
            }
        }
    }

    /**
     * Sets isPerfect to false and adds one to the numMistakes
     */
    // Called when the player incorrectly clicks a block
    public void madeMistake() {
        isPerfect = false;
        numMistakes++;
    }

    public void filledTileClicked() {
        filledTilesClicked++;
    }


    /* GETTERS & SETTERS */

    public Block[][] getGameBoard() {
        return gameBoard;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public int getID() {
        return id;
    }

    public Location getOriginLocation() {
        return originLocation;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
