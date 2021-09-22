package me.gavindimpter.minigameplugin.events;

import me.gavindimpter.minigameplugin.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerQuitEvent implements Listener {

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent e) {

        Player p = e.getPlayer();

        // Remove the game if the player was playing then they left
        GameManager.removePlayerFromGame(p);
    }

}
