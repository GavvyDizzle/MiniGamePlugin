package com.github.gavvydizzle.minigameplugin.boards;

import com.github.gavvydizzle.minigameplugin.blocks.snake.SnakeBodyBlock;
import com.github.gavvydizzle.minigameplugin.blocks.snake.SnakeHeadBlock;
import com.github.gavvydizzle.minigameplugin.MiniGamePlugin;
import com.github.gavvydizzle.minigameplugin.blocks.Block;
import com.github.gavvydizzle.minigameplugin.blocks.snake.SnakeBlock;
import com.github.gavvydizzle.minigameplugin.blocks.snake.SnakeFoodBlock;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class SnakeBoard implements GameBoard{

    private final int id;
    private static final int cols = MiniGamePlugin.getInstance().getConfig().getInt("snake.size");
    private static final int rows = MiniGamePlugin.getInstance().getConfig().getInt("snake.size");
    private final UUID playerUUID;
    private final Location originLocation;
    private final SnakeBlock[][] gameBoard;

    // Snake specific fields
    private int score;
    private boolean isMovingUp;
    private boolean isMovingDown;
    private boolean isMovingLeft;
    private boolean isMovingRight;
    private boolean isGameOver;

    private SnakeFoodBlock foodBlock;
    private ArmorStand armorStand;
    private BukkitTask bukkitTask;
    private final ArrayList<SnakeBlock> snake;


    public SnakeBoard(Player player, Location originLocation, int id) {
        this.playerUUID = player.getUniqueId();
        this.originLocation = originLocation;
        this.id = id;

        this.score = 0;
        this.isMovingUp = false;
        this.isMovingDown = false;
        this.isMovingLeft = false;
        this.isMovingRight = false;
        this.isGameOver = false;

        this.bukkitTask = null;
        this.armorStand = null;
        createArmorStand();
        snake = new ArrayList<>();

        gameBoard = new SnakeBlock[cols][rows];
        initializeGameBoard();
        setGameBoardInWorld();

        startGameUpdater();
    }

    /**
     * Sets all values of the GameBoard to empty blocks
     * Places in the snake head, and food block
     */
    private void initializeGameBoard() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                gameBoard[col][row] = new SnakeBlock(col, row);
            }
        }

        int randCol = (int) (Math.random() * cols);
        int randRow = (int) (Math.random() * rows);
        snake.add(new SnakeHeadBlock(randCol, randRow));
        gameBoard[randCol][randRow] = snake.get(0);

        foodBlock = null;
        placeFoodBlock();
    }

    @Override
    public void setGameBoardInWorld() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                Location blockLoc = new Location(originLocation.getWorld(), originLocation.getBlockX() - col, originLocation.getBlockY() + row, originLocation.getBlockZ());
                blockLoc.getBlock().setType( SnakeBlock.getMaterial() );
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

        // Stops the bukkitRunnable task if it is running
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
    }

    @Override
    public void checkForGameOver(Player p) {
        // Loss
        if (isGameOver) {
            p.sendTitle(ChatColor.GREEN + "Score: " + score, "Press space to play again", 10, 20, 10);
        }
    }

    /**
     * Starts the loop that updates the game
     * Will execute the code in run() 10 times per second
     */
    private void startGameUpdater() {
        bukkitTask = new BukkitRunnable() {

            // Saves the direction the snake is moving to make sure the next move is valid
            String snakeDirection = "";

            @Override
            public void run() {

                if (!isGameOver) {

                    SnakeBlock oldHead = snake.get(0);
                    SnakeBlock oldTail = snake.get(snake.size() - 1);

                    // Moves the entire snake body one segment closer to the head
                    for (int i = snake.size() - 1; i > 0; i--) {
                        snake.set(i, new SnakeBodyBlock(snake.get(i-1).getCol(), snake.get(i-1).getRow()));
                        gameBoard[snake.get(i).getCol()][snake.get(i).getRow()] = snake.get(i);
                    }

                    // Sets the direction string of the snake
                    if(isMovingUp && !snakeDirection.equals("down")) {
                        snakeDirection = "up";
                    }
                    else if (isMovingDown && !snakeDirection.equals("up")) {
                        snakeDirection = "down";
                    }
                    else if (isMovingLeft && !snakeDirection.equals("right")) {
                        snakeDirection = "left";
                    }
                    else if (isMovingRight && !snakeDirection.equals("left")) {
                        snakeDirection = "right";
                    }

                    switch (snakeDirection) {
                        case "up":
                            snake.set(0, new SnakeHeadBlock(oldHead.getCol(), oldHead.getRow() + 1));
                            break;
                        case "down":
                            snake.set(0, new SnakeHeadBlock(oldHead.getCol(), oldHead.getRow() - 1));
                            break;
                        case "left":
                            snake.set(0, new SnakeHeadBlock(oldHead.getCol() - 1, oldHead.getRow()));
                            break;
                        case "right":
                            snake.set(0, new SnakeHeadBlock(oldHead.getCol() + 1, oldHead.getRow()));
                            break;
                    }

                    if (snake.get(0).getCol() >= 0 && snake.get(0).getCol() < cols && snake.get(0).getRow() >= 0 && snake.get(0).getRow() < rows) {
                        // Adds one segment to the snake and creates a new piece of food if the head and food collide
                        if (snake.get(0).getCol() == foodBlock.getCol() && snake.get(0).getRow() == foodBlock.getRow()) {
                            snake.add(new SnakeBodyBlock(oldTail.getCol(), oldTail.getRow()));
                            gameBoard[oldTail.getCol()][oldTail.getRow()] = snake.get(snake.size() - 1);
                            placeFoodBlock();
                            score++;
                        }
                        // Sets the old tail location back to a blank block
                        else {
                            gameBoard[oldTail.getCol()][oldTail.getRow()] = new SnakeBlock(oldTail.getCol(), oldTail.getRow());
                            Location oldTailLoc = new Location(originLocation.getWorld(), originLocation.getBlockX() - oldTail.getCol(), originLocation.getBlockY() + oldTail.getRow(), originLocation.getBlockZ());
                            oldTailLoc.getBlock().setType(SnakeBlock.getMaterial());
                        }

                        gameBoard[snake.get(0).getCol()][snake.get(0).getRow()] = snake.get(0);

                        // Check if the snake collided with itself
                        // No need to check if colliding with the head or the first 3 body segments
                        for (int i = snake.size() - 1; i >= 4; i--) {
                            if (snake.get(0).getCol() == snake.get(i).getCol() && snake.get(0).getRow() == snake.get(i).getRow()) {
                                isGameOver = true;
                                break;
                            }
                        }

                        if (snake.size() > 1) {
                            Location oneBehindHeadLoc = new Location(originLocation.getWorld(), originLocation.getBlockX() - snake.get(1).getCol(), originLocation.getBlockY() + snake.get(1).getRow(), originLocation.getBlockZ());
                            oneBehindHeadLoc.getBlock().setType(SnakeBodyBlock.getMaterial());
                        }

                        Location foodLoc = new Location(originLocation.getWorld(), originLocation.getBlockX() - foodBlock.getCol(), originLocation.getBlockY() + foodBlock.getRow(), originLocation.getBlockZ());
                        foodLoc.getBlock().setType(SnakeFoodBlock.getMaterial());

                        Location headLoc = new Location(originLocation.getWorld(), originLocation.getBlockX() - snake.get(0).getCol(), originLocation.getBlockY() + snake.get(0).getRow(), originLocation.getBlockZ());
                        headLoc.getBlock().setType(SnakeHeadBlock.getMaterial());
                    }
                    else {
                        isGameOver = true;
                    }
                }
                else {
                    checkForGameOver(Bukkit.getPlayer(playerUUID));
                    bukkitTask.cancel();
                }

            }
        }.runTaskTimer(MiniGamePlugin.getInstance(), 0L, 2L);
    }

    /**
     * Places the food block on the board in an empty location
     */
    public void placeFoodBlock() {

        int randCol = (int) (Math.random() * cols);
        int randRow = (int) (Math.random() * rows);

        boolean hasSnake = false;
        // Make sure the new food location isn't a space where the snake is
        for (int i = 0; i < snake.size() - 1; i++) {
            if (snake.get(i).getCol() == randCol && snake.get(i).getRow() == randRow) {
                hasSnake = true;
                break;
            }
        }

        if (hasSnake) {
            placeFoodBlock();
        }
        else {
            foodBlock = new SnakeFoodBlock(randCol, randRow);
            gameBoard[randCol][randRow] = foodBlock;
        }
    }

    /**
     * Creates the armor stand at the play location
     * Changes some of the armor stand's properties
     */
    private void createArmorStand() {
        Location playerLocation = new Location(originLocation.getWorld(), originLocation.getBlockX() - cols/2.0 + 0.5, originLocation.getBlockY() + 3.8, originLocation.getBlockZ() - 8.3);
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
}
