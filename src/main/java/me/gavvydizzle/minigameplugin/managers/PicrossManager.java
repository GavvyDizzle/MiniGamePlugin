package me.gavvydizzle.minigameplugin.managers;

import me.gavvydizzle.minigameplugin.MiniGamePlugin;
import me.gavvydizzle.minigameplugin.boards.GameBoard;
import me.gavvydizzle.minigameplugin.boards.PicrossBoard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

// NOT THREAD SAFE!
public class PicrossManager implements GameManager{

    // FIELDS //

    // Instance of this class
    private static PicrossManager picrossManager;

    // Distance all boards are from one another
    private static int BLOCKS_APART = MiniGamePlugin.getInstance().getConfig().getInt("picross.distance-apart");

    // The location of the origin of the first GameBoard of this type
    private static Location ORIGIN_LOCATION = MiniGamePlugin.getInstance().getConfig().getLocation("picross.first-board-location");

    // List of all current boards
    private GameBoard[] gameBoards = new PicrossBoard[MiniGamePlugin.getInstance().getConfig().getInt("picross.max-games")];

    // List of all players currently playing this game type
    private ArrayList<UUID> players = new ArrayList<>();


    // METHODS //

    private PicrossManager() {} // Prevent instantiation

    /**
     * @return an instance of this class
     */
    public static PicrossManager getManager() {
        if (picrossManager == null) {
            picrossManager = new PicrossManager();
        }
        return picrossManager;
    }

    /**
     * Creates a new picross game of size (cols x rows) for player p at the location specified by the game's ID
     *
     * @param cols the number of columns in the board
     * @param rows the number of columns in the board
     * @param p the player wishing to play
     * @return the arena created
     */
    public GameBoard createGameBoard(int cols, int rows, Player p) {
        if (isInGame(p)) {
            p.sendMessage("You're already playing!");
            return null;
        }

        // Calculates the lowest open game slot in the array picrossBoards
        int lowestOpenIndex = -1;
        for (int i = 0; i < gameBoards.length; i++) {
            if (gameBoards[i] == null) {
                lowestOpenIndex = i;
                break;
            }
        }
        // If all arenas are full
        if (lowestOpenIndex == -1) {
            p.sendMessage(ChatColor.RED + "No open Picross slots");
            p.sendMessage(ChatColor.RED + "Request an administrator to open more or wait for one to open");
            return null;
        }

        GameManager.removePlayerFromGame(p);

        Location boardOriginLocation = new Location(ORIGIN_LOCATION.getWorld(), -BLOCKS_APART * lowestOpenIndex + ORIGIN_LOCATION.getBlockX(), ORIGIN_LOCATION.getBlockY(), ORIGIN_LOCATION.getBlockZ());
        PicrossBoard b = new PicrossBoard(cols, rows, p, boardOriginLocation, lowestOpenIndex + 1);
        this.gameBoards[lowestOpenIndex] = b;

        // teleports player to the picross board spawn
        Location playerSpawnLocation = new Location(b.getOriginLocation().getWorld(), b.getOriginLocation().getBlockX(), b.getOriginLocation().getBlockY(), b.getOriginLocation().getBlockZ() - 6);
        p.teleport(playerSpawnLocation);

        addPlayer(p);

        return b;
    }

    /**
     * Creates a new picross game of size (cols x rows) for player p at the location of the previous one
     * To only be called when the player is assigned to a game (because the ID is known)
     *
     * @param cols the number of columns in the board
     * @param rows the number of columns in the board
     * @param p the player wishing to play
     * @param index the index of GameBoards to place the new PicrossBoard in
     * @return the arena created
     */
    public PicrossBoard createGameBoard(int cols, int rows, Player p, int index) {

        this.gameBoards[index].removeGameBoardFromWorld();
        PicrossBoard b = new PicrossBoard(cols, rows, p, gameBoards[index].getOriginLocation(), index + 1);
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
        PicrossBoard b = null;

        // Searches each picross instance for the player
        for (PicrossBoard board : (PicrossBoard[]) gameBoards) {
            if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                b = board;
                break;
            }
        }

        // Check arena validity
        if (b == null) {
            p.sendMessage("You can't leave a game that doesn't exist - picross removeGameBoard");
            return;
        }

        // Remove board from board list
        gameBoards[b.getID() - 1].removeGameBoardFromWorld();
        gameBoards[b.getID() - 1] = null;
        p.sendMessage(ChatColor.GRAY + "Removed you from the Picross game");

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
        p.getPersistentDataContainer().set(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING, "picross");
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
    public PicrossBoard[] getGameBoards() {
        return (PicrossBoard[]) gameBoards;
    }
}
