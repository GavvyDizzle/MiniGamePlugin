package me.gavindimpter.minigameplugin.managers;

import me.gavindimpter.minigameplugin.MiniGamePlugin;
import me.gavindimpter.minigameplugin.boards.GameBoard;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public interface GameManager {

    static void removePlayerFromGame(Player p) {
        // Gets where the player was before moving them to the new game
        switch(Objects.requireNonNull(p.getPersistentDataContainer().get(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING))) {
            case "picross":
                PicrossManager.getManager().removeGameBoard(p);
                PicrossManager.getManager().removePlayer(p);
                break;
            case "minesweeper":
                MinesweeperManager.getManager().removeGameBoard(p);
                MinesweeperManager.getManager().removePlayer(p);
                break;
        }
    }

    void removeGameBoard(Player p);

    void addPlayer(Player p);

    void removePlayer(Player p);

    boolean isInGame(Player p);

    GameBoard[] getGameBoards();
}
