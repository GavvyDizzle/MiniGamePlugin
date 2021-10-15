package me.gavvydizzle.minigameplugin.managers;

import me.gavvydizzle.minigameplugin.MiniGamePlugin;
import me.gavvydizzle.minigameplugin.boards.GameBoard;
import me.gavvydizzle.minigameplugin.boards._2048Board;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class _2048Manager implements GameManager{

    // FIELDS //

    // Instance of this class
    private static _2048Manager _2048Manager;

    // Distance all boards are from one another
    private static final int BLOCKS_APART = MiniGamePlugin.getInstance().getConfig().getInt("2048.distance-apart");

    // The location of the origin of the first GameBoard of this type
    private static final Location ORIGIN_LOCATION = MiniGamePlugin.getInstance().getConfig().getLocation("2048.first-board-location");

    // List of all current boards
    private final GameBoard[] gameBoards = new _2048Board[MiniGamePlugin.getInstance().getConfig().getInt("2048.max-games")];

    // List of all players currently playing this game type
    private final ArrayList<UUID> players = new ArrayList<>();


    // METHODS //

    private _2048Manager() {} // Prevent instantiation

    /**
     * @return An instance of this class.
     */
    public static _2048Manager getManager() {
        if (_2048Manager == null) {
            _2048Manager = new _2048Manager();
        }
        return _2048Manager;
    }

    /**
     * Creates a new 2048 game for player p at the location specified by the game's ID.
     *
     * @param p the player wishing to play.
     * @return the GameBoard created.
     */
    public GameBoard createGameBoard(Player p) {
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
            p.sendMessage(ChatColor.RED + "No open 2048 slots");
            p.sendMessage(ChatColor.RED + "Request an administrator to open more or wait for one to open");
            return null;
        }

        GameManager.removePlayerFromGame(p);

        Location boardOriginLocation = new Location(ORIGIN_LOCATION.getWorld(), -BLOCKS_APART * lowestOpenIndex + ORIGIN_LOCATION.getBlockX(), ORIGIN_LOCATION.getBlockY(), ORIGIN_LOCATION.getBlockZ());
        _2048Board b = new _2048Board(p, boardOriginLocation, lowestOpenIndex + 1);

        // Teleports the player by mounting them to the armor stand associated with this GameBoard
        b.addPlayerToArmorStand(p);
        this.gameBoards[lowestOpenIndex] = b;

        addPlayer(p);
        b.addPlayerToArmorStand(p);

        return b;
    }

    /**
     * Creates a new 2048 game  for player p at the location of the previous one.
     * To only be called when the player is assigned to a game (because the ID is known).
     *
     * @param p The player wishing to play.
     * @param index The index of gameBoards[] to place the new _2048Board in.
     * @return The GameBoard created.
     */
    public GameBoard createGameBoard(Player p, int index) {

        this.gameBoards[index].removeGameBoardFromWorld();
        _2048Board b = new _2048Board(p, gameBoards[index].getOriginLocation(), index + 1);
        b.addPlayerToArmorStand(p);
        this.gameBoards[index] = b;

        return b;
    }

    @Override
    public void removeGameBoard(Player p) {
        GameBoard b = null;

        // Searches each picross instance for the player
        for (GameBoard board : gameBoards) {
            if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                b = board;
                break;
            }
        }

        // Check arena validity
        if (b == null) {
            return;
        }

        // Remove board from board list
        gameBoards[b.getID() - 1].removeGameBoardFromWorld();
        gameBoards[b.getID() - 1] = null;
    }

    @Override
    public void addPlayer(Player p) {
        players.add(p.getUniqueId());
        p.getPersistentDataContainer().set(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING, "2048");
    }

    @Override
    public void removePlayer(Player p) {
        players.remove(p.getUniqueId());
        p.getPersistentDataContainer().set(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING, "lobby");
        p.teleport((Location) Objects.requireNonNull(MiniGamePlugin.getInstance().getConfig().get("lobby.spawn-location")));
    }

    @Override
    public boolean isInGame(Player p) {
        for (UUID player : players) {
            if (player.equals(p.getUniqueId()))
                return true;
        }
        return false;
    }

    @Override
    public _2048Board[] getGameBoards() {
        return (_2048Board[]) gameBoards;
    }
}
