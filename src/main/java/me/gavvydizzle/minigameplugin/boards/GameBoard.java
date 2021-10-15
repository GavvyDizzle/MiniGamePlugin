package me.gavvydizzle.minigameplugin.boards;

import me.gavvydizzle.minigameplugin.blocks.Block;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface GameBoard {

    /**
     * Places all parts of the GameBoard in the world.
     */
    void setGameBoardInWorld();

    /**
     * Removes everything created by the GameBoard.
     */
    void removeGameBoardFromWorld();

    /**
     * Determines if the current game is over.
     *
     * @param p The player who won/lost.
     */
    void checkForGameOver(Player p);

    /**
     * @return The ID of the current GameBoard.
     */
    int getID();

    /**
     * @return If the game is over.
     */
    boolean isGameOver();

    /**
     * @return The origin location of the current GameBoard.
     */
    Location getOriginLocation();

    /**
     * @return The player's UUID associated with the current GameBoard
     */
    UUID getPlayerUUID();

    /**
     * @return The current gameBoard.
     */
    Block[][] getGameBoard();

}
