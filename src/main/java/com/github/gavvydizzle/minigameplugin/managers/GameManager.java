package com.github.gavvydizzle.minigameplugin.managers;

import com.github.gavvydizzle.minigameplugin.MiniGamePlugin;
import com.github.gavvydizzle.minigameplugin.boards.GameBoard;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public interface GameManager {

    /**
     * Removes the player and the game they are in from the world if it exists.
     *
     * @param p The player to remove
     */
    static void removePlayerFromGame(Player p) {
        switch(Objects.requireNonNull(p.getPersistentDataContainer().get(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING))) {
            case "lobby":
                break;
            case "picross":
                PicrossManager.getManager().removeGameBoard(p);
                PicrossManager.getManager().removePlayer(p);
                break;
            case "minesweeper":
                MinesweeperManager.getManager().removeGameBoard(p);
                MinesweeperManager.getManager().removePlayer(p);
                break;
            case "snake":
                SnakeManager.getManager().removeGameBoard(p);
                SnakeManager.getManager().removePlayer(p);
                break;
            case "2048":
                _2048Manager.getManager().removeGameBoard(p);
                _2048Manager.getManager().removePlayer(p);
                break;
        }
    }

    /**
     * Removes the player from their current board and deletes that board if it exists.
     * Called when the player leaves/quits the game or the server.
     *
     * @param p The player to remove from the game
     */
    void removeGameBoard(Player p);

    /**
     * Adds the player to the requested game's player list.
     * Sets their currentGame to that game's identifier in config.yml.
     *
     * @param p the player to add.
     */
    void addPlayer(Player p);

    /**
     * Removes the player from the game.
     * Sets their currentGame identifier to "lobby" and teleports them there.
     *
     * @param p The player to remove.
     */
    void removePlayer(Player p);

    /**
     * Checks if the player is the game by checking the "players" ArrayList.
     *
     * @param p The player to check.
     * @return If the player is in a game.
     */
    boolean isInGame(Player p);

    /**
     * @return The manager's array GameBoards.
     */
    GameBoard[] getGameBoards();
}
