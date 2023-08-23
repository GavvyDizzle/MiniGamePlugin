package com.github.gavvydizzle.minigameplugin.events;

import com.github.gavvydizzle.minigameplugin.MiniGamePlugin;
import com.github.gavvydizzle.minigameplugin.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerQuitEvent implements Listener {

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent e) {

        Player p = e.getPlayer();

        if (p.getWorld().getName().equalsIgnoreCase((MiniGamePlugin.GAME_WORLD_NAME))) {

            // Remove the game if the player was playing then they left
            GameManager.removePlayerFromGame(p);

        }
    }

}
