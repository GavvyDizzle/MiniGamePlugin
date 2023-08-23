package com.github.gavvydizzle.minigameplugin.events;

import com.github.gavvydizzle.minigameplugin.MiniGamePlugin;
import com.github.gavvydizzle.minigameplugin.managers.GameManager;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerSwitchWorldEvent implements Listener {

    @EventHandler
    public void onPlayerSwitchWorld(PlayerChangedWorldEvent e) {

        Player p = e.getPlayer();

        // When the player switches to this world
        if (p.getWorld().getName().equalsIgnoreCase((MiniGamePlugin.GAME_WORLD_NAME))) {

            // Sets the NBT tag currentGame for the player with the default value "lobby" every time they join
            p.getPersistentDataContainer().set(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING, "lobby");

            // Sets the player's location to the lobby every time they join
            p.teleport((Location) Objects.requireNonNull(MiniGamePlugin.getInstance().getConfig().get("lobby.spawn-location")));

        }
        // When the player switches to a different world from this one
        else {

            // Remove the game if the player was playing then they left
            GameManager.removePlayerFromGame(p);

        }
    }
}
