package me.gavvydizzle.minigameplugin.boards;

import me.gavvydizzle.minigameplugin.blocks.Block;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface GameBoard {

    void removeGameBoardFromWorld();

    void checkForGameOver(Player p);

    boolean isGameOver();

    Location getOriginLocation();

    UUID getPlayerUUID();

    Block[][] getGameBoard();

}
