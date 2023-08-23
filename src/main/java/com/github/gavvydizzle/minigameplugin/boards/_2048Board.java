package com.github.gavvydizzle.minigameplugin.boards;

import com.github.gavvydizzle.minigameplugin.MiniGamePlugin;
import com.github.gavvydizzle.minigameplugin.blocks.Block;
import com.github.gavvydizzle.minigameplugin.blocks._2048._2048Block;
import com.github.gavvydizzle.minigameplugin.blocks._2048._2048EmptyBlock;
import com.github.gavvydizzle.minigameplugin.blocks._2048._2048FilledBlock;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class _2048Board implements GameBoard{

    private final int id;
    private static final int cols = MiniGamePlugin.getInstance().getConfig().getInt("2048.size");
    private static final int rows = MiniGamePlugin.getInstance().getConfig().getInt("2048.size");
    private final UUID playerUUID;
    private final Location originLocation;
    private final _2048Block[][] gameBoard;

    // Snake specific fields
    private int score;
    private boolean isMovingUp;
    private boolean isMovingDown;
    private boolean isMovingLeft;
    private boolean isMovingRight;
    private boolean isHoldingDirection;
    private boolean isGameOver;

    private ArmorStand armorStand;

    private final Hologram scoreDisplay;


    public _2048Board(Player player, Location originLocation, int id) {
        this.playerUUID = player.getUniqueId();
        this.originLocation = originLocation;
        this.id = id;

        this.score = 0;
        this.isMovingUp = false;
        this.isMovingDown = false;
        this.isMovingLeft = false;
        this.isMovingRight = false;
        this.isHoldingDirection = false;
        this.isGameOver = false;

        this.armorStand = null;
        createArmorStand();

        gameBoard = new _2048Block[cols][rows];
        initializeGameBoard();
        setGameBoardInWorld();

        Location scoreDisplayLocation = new Location(originLocation.getWorld(), originLocation.getBlockX() + 0.5, originLocation.getBlockY() + 0.5 + rows, originLocation.getBlockZ() - 0.25);
        scoreDisplay = DHAPI.createHologram("2048_score", scoreDisplayLocation, false, Collections.singletonList("Score: 0"));
    }

    /**
     * Sets all values of the GameBoard to empty blocks
     * Places in the snake head, and food block
     */
    private void initializeGameBoard() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                gameBoard[col][row] = new _2048EmptyBlock();
            }
        }

        // Places initial 2 blocks
        placeNewBlock();
        placeNewBlock();
    }

    @Override
    public void setGameBoardInWorld() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                Location blockLoc = new Location(originLocation.getWorld(), originLocation.getBlockX() - col, originLocation.getBlockY() + row, originLocation.getBlockZ());
                blockLoc.getBlock().setType( gameBoard[col][row].getMaterial() );
            }
        }
    }

    @Override
    public void removeGameBoardFromWorld() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                Location blockLoc = new Location(originLocation.getWorld(), originLocation.getBlockX() - col, originLocation.getBlockY() + row, originLocation.getBlockZ());
                blockLoc.getBlock().setType( Material.AIR );
            }
        }
        armorStand.remove();
        scoreDisplay.delete();
    }

    @Override
    public void checkForGameOver(Player p) {

        // Checks if all blocks are fill
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (gameBoard[i][j] instanceof _2048EmptyBlock) {
                    return;
                }
            }
        }


        // Checks all four directions to see if a move is possible
        for (int col = 0; col < cols; col++) {
            for (int n = 1; n <= rows - 1; n++) {
                for (int row = rows - 1; row >= n; row--) {

                    // If both blocks are filled
                    if (gameBoard[col][row - 1] instanceof _2048FilledBlock && gameBoard[col][row] instanceof _2048FilledBlock) {
                        // If both have the same value
                        if (((_2048FilledBlock) gameBoard[col][row - 1]).getValue() == ((_2048FilledBlock) gameBoard[col][row]).getValue()) {
                            return;
                        }
                    }
                }
            }
        }
        for (int col = 0; col < cols; col++) {
            for (int n = rows - 2; n >= 0; n--) {
                for (int row = 0; row <= n; row++) {

                    // If both blocks are filled
                    if (gameBoard[col][row] instanceof _2048FilledBlock && gameBoard[col][row + 1] instanceof _2048FilledBlock) {
                        // If both have the same value and neither have merged this move
                        if (((_2048FilledBlock) gameBoard[col][row]).getValue() == ((_2048FilledBlock) gameBoard[col][row + 1]).getValue()) {
                            return;
                        }
                    }
                }
            }
        }
        for (int row = 0; row < rows; row++) {
            for (int n = cols - 2; n >= 0; n--) {
                for (int col = 0; col <= n; col++) {

                    // If both blocks are filled
                    if (gameBoard[col][row] instanceof _2048FilledBlock && gameBoard[col + 1][row] instanceof _2048FilledBlock) {
                        // If both have the same value
                        if (((_2048FilledBlock) gameBoard[col][row]).getValue() == ((_2048FilledBlock) gameBoard[col + 1][row]).getValue()) {
                            return;
                        }
                    }
                }
            }
        }
        for (int row = 0; row < rows; row++) {
            for (int n = 1; n <= cols - 1; n++) {
                for (int col = cols - 1; col >= n; col--) {

                    // If both blocks are filled
                    if (gameBoard[col - 1][row] instanceof _2048FilledBlock && gameBoard[col][row] instanceof _2048FilledBlock) {
                        // If both have the same value
                        if (((_2048FilledBlock) gameBoard[col - 1][row]).getValue() == ((_2048FilledBlock) gameBoard[col][row]).getValue()) {
                            return;
                        }
                    }
                }
            }
        }

        // Loss
        isGameOver = true;
        p.sendTitle(ChatColor.GREEN + "Score: " + score, "Press space to play again", 10, 20, 10);
    }

    /**
     * Creates the armor stand at the play location
     * Changes some of the armor stand's properties
     */
    private void createArmorStand() {
        Location playerLocation = new Location(originLocation.getWorld(), originLocation.getBlockX() - cols/2.0 + 1, originLocation.getBlockY() - 0.2, originLocation.getBlockZ() - 6.3);
        armorStand = Objects.requireNonNull(playerLocation.getWorld()).spawn(playerLocation, ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setInvisible(true);
    }

    /**
     * Mounts the player on the armor stand
     *
     * @param p The player to mount
     */
    public void addPlayerToArmorStand(Player p) {
        armorStand.addPassenger(p);
    }

    public void placeNewBlock() {
        int randCol = (int) (Math.random() * cols);
        int randRow = (int) (Math.random() * rows);

        if (gameBoard[randCol][randRow] instanceof _2048EmptyBlock) {
            if (Math.random() <= 0.05) {
                gameBoard[randCol][randRow] = new _2048FilledBlock(4);
            }
            else {
                gameBoard[randCol][randRow] = new _2048FilledBlock(2);
            }
        }
        else {
            placeNewBlock();
        }
    }

    public void handleMove() {
        _2048Block[][] oldGB = Arrays.stream(gameBoard).map(_2048Block[]::clone).toArray(_2048Block[][]::new);

        //Bukkit.getPlayer(playerUUID).sendMessage("IHD?: " + isHoldingDirection);

        if (isMovingUp && !isHoldingDirection) {
            for (int col = 0; col < cols; col++) {
                for (int n = 1; n <= rows - 1; n++) {
                    for (int row = rows - 1; row >= n; row--) {

                        // If both blocks are filled
                        if (gameBoard[col][row - 1] instanceof _2048FilledBlock && gameBoard[col][row] instanceof _2048FilledBlock) {
                            // If both have the same value
                            if (((_2048FilledBlock) gameBoard[col][row - 1]).getValue() == ((_2048FilledBlock) gameBoard[col][row]).getValue() && (!((_2048FilledBlock) gameBoard[col][row - 1]).isHasMergedThisMove() && !((_2048FilledBlock) gameBoard[col][row]).isHasMergedThisMove())) {
                                gameBoard[col][row] = new _2048FilledBlock(((_2048FilledBlock) gameBoard[col][row]).getValue() * 2);
                                gameBoard[col][row - 1] = new _2048EmptyBlock();
                                gameBoard[col][row].hasMergedThisMove(true);

                                score += ((_2048FilledBlock) gameBoard[col][row]).getValue();
                                DHAPI.setHologramLine(scoreDisplay, 0, "Score: " + score);
                            }
                            // If both have different values, do nothing
                        }
                        // If the original is filled and the new location is empty
                        else if (gameBoard[col][row - 1] instanceof _2048FilledBlock && gameBoard[col][row] instanceof _2048EmptyBlock){
                            gameBoard[col][row] = gameBoard[col][row - 1];
                            gameBoard[col][row - 1] = new _2048EmptyBlock();
                        }
                    }
                }
            }
        }
        else if (isMovingDown && !isHoldingDirection) {
            for (int col = 0; col < cols; col++) {
                for (int n = rows - 2; n >= 0; n--) {
                    for (int row = 0; row <= n; row++) {

                        // If both blocks are filled
                        if (gameBoard[col][row] instanceof _2048FilledBlock && gameBoard[col][row + 1] instanceof _2048FilledBlock) {
                            // If both have the same value and neither have merged this move
                            if (((_2048FilledBlock) gameBoard[col][row]).getValue() == ((_2048FilledBlock) gameBoard[col][row + 1]).getValue() && (!((_2048FilledBlock) gameBoard[col][row]).isHasMergedThisMove() && !((_2048FilledBlock) gameBoard[col][row + 1]).isHasMergedThisMove())) {
                                gameBoard[col][row] = new _2048FilledBlock(((_2048FilledBlock) gameBoard[col][row]).getValue() * 2);
                                gameBoard[col][row + 1] = new _2048EmptyBlock();
                                gameBoard[col][row].hasMergedThisMove(true);

                                score += ((_2048FilledBlock) gameBoard[col][row]).getValue();
                                DHAPI.setHologramLine(scoreDisplay, 0, "Score: " + score);
                            }
                            // If both have different values, do nothing
                        }
                        // If the original is filled and the new location is empty
                        else if (gameBoard[col][row + 1] instanceof _2048FilledBlock && gameBoard[col][row] instanceof _2048EmptyBlock){
                            gameBoard[col][row] = gameBoard[col][row + 1];
                            gameBoard[col][row + 1] = new _2048EmptyBlock();
                        }
                    }
                }
            }
        }
        else if (isMovingLeft && !isHoldingDirection) {
            for (int row = 0; row < rows; row++) {
                for (int n = cols - 2; n >= 0; n--) {
                    for (int col = 0; col <= n; col++) {

                        // If both blocks are filled
                        if (gameBoard[col][row] instanceof _2048FilledBlock && gameBoard[col + 1][row] instanceof _2048FilledBlock) {
                            // If both have the same value
                            if (((_2048FilledBlock) gameBoard[col][row]).getValue() == ((_2048FilledBlock) gameBoard[col + 1][row]).getValue() && (!((_2048FilledBlock) gameBoard[col][row]).isHasMergedThisMove() && !((_2048FilledBlock) gameBoard[col + 1][row]).isHasMergedThisMove())) {
                                gameBoard[col][row] = new _2048FilledBlock(((_2048FilledBlock) gameBoard[col][row]).getValue() * 2);
                                gameBoard[col + 1][row] = new _2048EmptyBlock();
                                gameBoard[col][row].hasMergedThisMove(true);

                                score += ((_2048FilledBlock) gameBoard[col][row]).getValue();
                                DHAPI.setHologramLine(scoreDisplay, 0, "Score: " + score);
                            }
                            // If both have different values, do nothing
                        }
                        // If the original is filled and the new location is empty
                        else if (gameBoard[col + 1][row] instanceof _2048FilledBlock && gameBoard[col][row] instanceof _2048EmptyBlock){
                            gameBoard[col][row] = gameBoard[col + 1][row];
                            gameBoard[col + 1][row] = new _2048EmptyBlock();
                        }
                    }
                }
            }
        }
        else if (isMovingRight && !isHoldingDirection) {
            for (int row = 0; row < rows; row++) {
                for (int n = 1; n <= cols - 1; n++) {
                    for (int col = cols - 1; col >= n; col--) {

                        // If both blocks are filled
                        if (gameBoard[col - 1][row] instanceof _2048FilledBlock && gameBoard[col][row] instanceof _2048FilledBlock) {
                            // If both have the same value
                            if (((_2048FilledBlock) gameBoard[col - 1][row]).getValue() == ((_2048FilledBlock) gameBoard[col][row]).getValue() && (!((_2048FilledBlock) gameBoard[col - 1][row]).isHasMergedThisMove() && !((_2048FilledBlock) gameBoard[col][row]).isHasMergedThisMove())) {
                                gameBoard[col][row] = new _2048FilledBlock(((_2048FilledBlock) gameBoard[col][row]).getValue() * 2);
                                gameBoard[col - 1][row] = new _2048EmptyBlock();
                                gameBoard[col][row].hasMergedThisMove(true);

                                score += ((_2048FilledBlock) gameBoard[col][row]).getValue();
                                DHAPI.setHologramLine(scoreDisplay, 0, "Score: " + score);
                            }
                            // If both have different values, do nothing
                        }
                        // If the original is filled and the new location is empty
                        else if (gameBoard[col - 1][row] instanceof _2048FilledBlock && gameBoard[col][row] instanceof _2048EmptyBlock){
                            gameBoard[col][row] = gameBoard[col - 1][row];
                            gameBoard[col - 1][row] = new _2048EmptyBlock();
                        }
                    }
                }
            }
        }



        // If the GameBoard has changed due to the move, add a new tile
        if (!Arrays.deepEquals(oldGB, gameBoard)) {
            placeNewBlock();
            setGameBoardInWorld();
        }
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                gameBoard[i][j].hasMergedThisMove(false);
            }
        }

        checkForGameOver(Bukkit.getPlayer(playerUUID));
    }


    /* GETTERS & SETTERS */

    @Override
    public int getID() {
        return id;
    }

    @Override
    public boolean isGameOver() {
        return isGameOver;
    }

    @Override
    public Location getOriginLocation() {
        return originLocation;
    }

    @Override
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public Block[][] getGameBoard() {
        return gameBoard;
    }

    public void setMovingUp(boolean b) {
        isMovingUp = b;
    }

    public void setMovingDown(boolean b) {
        isMovingDown = b;
    }

    public void setMovingLeft(boolean b) {
        isMovingLeft = b;
    }

    public void setMovingRight(boolean b) {
        isMovingRight = b;
    }

    public void setHoldingDirection(boolean b) {
        isHoldingDirection = b;
    }
}
