package me.gavvydizzle.minigameplugin.managers;

import me.gavvydizzle.minigameplugin.MiniGamePlugin;
import me.gavvydizzle.minigameplugin.boards.GameBoard;
import me.gavvydizzle.minigameplugin.boards.MinesweeperBoard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class MinesweeperManager implements GameManager{

    // FIELDS //

    // Instance of this class
    private static MinesweeperManager minesweeperManager;

    // Distance all boards are from one another
    private static int BLOCKS_APART = MiniGamePlugin.getInstance().getConfig().getInt("minesweeper.distance-apart");

    // The location of the origin of the first GameBoard of this type
    private static Location ORIGIN_LOCATION = MiniGamePlugin.getInstance().getConfig().getLocation("minesweeper.first-board-location");

    // List of all current boards
    private GameBoard[] gameBoards = new MinesweeperBoard[MiniGamePlugin.getInstance().getConfig().getInt("minesweeper.max-games")];

    // List of all players currently playing this game type
    private ArrayList<UUID> players = new ArrayList<>();


    // METHODS //

    private MinesweeperManager() {} // Prevent instantiation

    /**
     * @return an instance of this class
     */
    public static MinesweeperManager getManager() {
        if (minesweeperManager == null) {
            minesweeperManager = new MinesweeperManager();
        }
        return minesweeperManager;
    }

    /**
     * Creates a new minesweeper game of size (cols x rows) with numMines mines for player p at the location specified by the game's ID
     *
     * @param cols the number of columns in the board
     * @param rows the number of columns in the board
     * @param numMines the number of mines in the game
     * @param p the player wishing to play
     * @return the arena created
     */
    public GameBoard createGameBoard(int cols, int rows, int numMines, Player p) {
        if (isInGame(p)) {
            p.sendMessage("You're already playing!");
            return null;
        }

        // Calculates the lowest open game slot in the array gameBoards
        int lowestOpenIndex = -1;
        for (int i = 0; i < gameBoards.length; i++) {
            if (gameBoards[i] == null) {
                lowestOpenIndex = i;
                break;
            }
        }
        // If all arenas are full
        if (lowestOpenIndex == -1) {
            p.sendMessage(ChatColor.RED + "No open Minesweeper slots");
            p.sendMessage(ChatColor.RED + "Request an administrator to open more or wait for one to open");
            return null;
        }

        GameManager.removePlayerFromGame(p);

        Location boardOriginLocation = new Location(ORIGIN_LOCATION.getWorld(), -BLOCKS_APART * lowestOpenIndex + ORIGIN_LOCATION.getBlockX(), ORIGIN_LOCATION.getBlockY(), ORIGIN_LOCATION.getBlockZ());
        MinesweeperBoard b = new MinesweeperBoard(cols, rows, numMines, p, boardOriginLocation, lowestOpenIndex + 1);
        this.gameBoards[lowestOpenIndex] = b;

        // teleports player to the Minesweeper board spawn
        Location playerSpawnLocation = new Location(b.getOriginLocation().getWorld(), b.getOriginLocation().getBlockX(), b.getOriginLocation().getBlockY(), b.getOriginLocation().getBlockZ() - 6);
        p.teleport(playerSpawnLocation);

        addPlayer(p);

        return b;
    }

    /**
     * Creates a new minesweeper game of size (cols x rows) with numMines mines for player p at the location of the previous one
     * To only be called when the player is assigned to a game (because the ID is known)
     *
     * @param cols the number of columns in the board
     * @param rows the number of columns in the board
     * @param numMines the number of mines in the game
     * @param p the player wishing to play
     * @param index the index of GameBoards to place the new PicrossBoard in
     * @return the arena created
     */
    public GameBoard createGameBoard(int cols, int rows, int numMines, Player p, int index) {

        this.gameBoards[index].removeGameBoardFromWorld();
        MinesweeperBoard b = new MinesweeperBoard(cols, rows, numMines, p, gameBoards[index].getOriginLocation(), index + 1);
        this.gameBoards[index] = b;

        return b;
    }

    /**
     * Removes the player from their current board and deletes that board.
     * Called when the player leaves/quits the game or the server
     *
     * The player is allowed to not be in game, a check
     * will be performed to ensure the validity of the arena
     *
     * @param p the player and player's board to remove
     */
    @Override
    public void removeGameBoard(Player p) {
        MinesweeperBoard b = null;

        // Searches each picross instance for the player
        for (MinesweeperBoard board : (MinesweeperBoard[]) gameBoards) {
            if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                b = board;
                break;
            }
        }

        // Check arena validity
        if (b == null) {
            p.sendMessage("You can't leave a game that doesn't exist");
            return;
        }

        // Remove board from board list
        gameBoards[b.getID() - 1].removeGameBoardFromWorld();
        gameBoards[b.getID() - 1] = null;
        p.sendMessage(ChatColor.GRAY + "Removed you from the Minesweeper game");

    }

    /**
     * Adds the player to this game's player list
     * Sets their currentGame to this game's identifier in config.yml
     *
     * @param p the player to add
     */
    @Override
    public void addPlayer(Player p) {
        players.add(p.getUniqueId());
        p.getPersistentDataContainer().set(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING, "minesweeper");
    }

    /**
     * Removes the player from this game's player list
     * Sets their currentGame to "lobby"
     * Teleports the player to the lobby
     *
     * @param p the player to remove
     */
    @Override
    public void removePlayer(Player p) {
        players.remove(p.getUniqueId());
        p.getPersistentDataContainer().set(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING, "lobby");
        p.teleport((Location) Objects.requireNonNull(MiniGamePlugin.getInstance().getConfig().get("lobby.spawn-location")));
    }

    /**
     * Checks if the player is the game by checking the "players" ArrayList
     *
     * @param p the player to check
     * @return true if the player is in a game
     */
    @Override
    public boolean isInGame(Player p) {
        for (UUID player : players) {
            if (player.equals(p.getUniqueId()))
                return true;
        }
        return false;
    }

    /**
     * @return the gameBoards array
     */
    @Override
    public MinesweeperBoard[] getGameBoards() {
        return (MinesweeperBoard[]) gameBoards;
    }
}
