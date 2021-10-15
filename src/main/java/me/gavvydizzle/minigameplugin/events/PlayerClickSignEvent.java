package me.gavvydizzle.minigameplugin.events;

import me.gavvydizzle.minigameplugin.MiniGamePlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerClickSignEvent implements Listener {

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {

        Player p = e.getPlayer();

        if (e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Sign) {

            Sign sign = (Sign) e.getClickedBlock().getState();
            String line = sign.getLine(1).trim();

            switch(Objects.requireNonNull(p.getPersistentDataContainer().get(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING))) {
                case "picross":
                    //PicrossManager.getManager().removeGameBoard(p);
                    //PicrossManager.getManager().removePlayer(p);
                    break;
                case "minesweeper":
                    //MinesweeperManager.getManager().removeGameBoard(p);
                    //MinesweeperManager.getManager().removePlayer(p);
                    break;
                case "snake":
                    if (line.equals("Play Again")) {
                        p.performCommand("snake");
                    }
                    else if (line.equals("Leave")) {
                        p.performCommand("leave");
                    }
                    e.setCancelled(true);
                    break;
                case "2048":
                    if (line.equals("Play Again")) {
                        p.performCommand("2048");
                    }
                    else if (line.equals("Leave")) {
                        p.performCommand("leave");
                    }
                    e.setCancelled(true);
                    break;
            }
        }
    }
}
