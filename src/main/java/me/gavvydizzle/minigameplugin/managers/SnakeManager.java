package me.gavvydizzle.minigameplugin.managers;

import me.gavvydizzle.minigameplugin.MiniGamePlugin;
import me.gavvydizzle.minigameplugin.boards.GameBoard;
import me.gavvydizzle.minigameplugin.boards.SnakeBoard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

//NOT THREAD SAFE
public class SnakeManager implements GameManager {

    // FIELDS //

    // Instance of this class
    private static SnakeManager snakeManager;

    // Distance all boards are from one another
    private static final int BLOCKS_APART = MiniGamePlugin.getInstance().getConfig().getInt("snake.distance-apart");

    // The location of the origin of the first GameBoard of this type
    private static final Location ORIGIN_LOCATION = MiniGamePlugin.getInstance().getConfig().getLocation("snake.first-board-location");

    // List of all current boards
    private final GameBoard[] gameBoards = new SnakeBoard[MiniGamePlugin.getInstance().getConfig().getInt("snake.max-games")];

    // List of all players currently playing this game type
    private final ArrayList<UUID> players = new ArrayList<>();


    // METHODS //

    private SnakeManager() {} // Prevent instantiation

    /**
     * @return An instance of this class.
     */
    public static SnakeManager getManager() {
        if (snakeManager == null) {
            snakeManager = new SnakeManager();
        }
        return snakeManager;
    }

    /**
     * Creates a new snake game (size set in SnakeBoard.java) for player p at the location specified by the game's ID.
     *
     * @param p The player wishing to play.
     * @return The GameBoard created.
     */
    public GameBoard createGameBoard(Player p) {
        if (isInGame(p)) {
            p.sendMessage("You're already playing!");
            return null;
        }

        // Calculates the lowest open game slot in the array snakeBoards
        int lowestOpenIndex = -1;
        for (int i = 0; i < gameBoards.length; i++) {
            if (gameBoards[i] == null) {
                lowestOpenIndex = i;
                break;
            }
        }
        // If all arenas are full
        if (lowestOpenIndex == -1) {
            p.sendMessage(ChatColor.RED + "No open Snake slots");
            p.sendMessage(ChatColor.RED + "Request an administrator to open more or wait for one to open");
            return null;
        }

        GameManager.removePlayerFromGame(p);

        Location boardOriginLocation = new Location(ORIGIN_LOCATION.getWorld(), -BLOCKS_APART * lowestOpenIndex + ORIGIN_LOCATION.getBlockX(), ORIGIN_LOCATION.getBlockY(), ORIGIN_LOCATION.getBlockZ());
        SnakeBoard b = new SnakeBoard(p, boardOriginLocation, lowestOpenIndex + 1);

        // Teleports the player by mounting them to the armor stand associated with this GameBoard
        b.addPlayerToArmorStand(p);
        this.gameBoards[lowestOpenIndex] = b;

        addPlayer(p);

        return b;
    }

    /**
     * Creates a new picross game (size set in SnakeBoard.java) for player p at the location of the previous one.
     * To only be called when the player is assigned to a game (because the ID is known).
     *
     * @param p The player wishing to play.
     * @param index The index of gameBoards[] to place the new SnakeBoard in.
     * @return The GameBoard created.
     */
    public GameBoard createGameBoard(Player p, int index) {

        this.gameBoards[index].removeGameBoardFromWorld();
        SnakeBoard b = new SnakeBoard(p, gameBoards[index].getOriginLocation(), index + 1);
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
        p.getPersistentDataContainer().set(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING, "snake");
    }

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
    public SnakeBoard[] getGameBoards() {
        return (SnakeBoard[]) gameBoards;
    }
}
