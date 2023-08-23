package com.github.gavvydizzle.minigameplugin.events;

import com.github.gavvydizzle.minigameplugin.MiniGamePlugin;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerJoinEvent implements Listener {

    //TODO - Remove event after creating a hub world

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {

        Player p = e.getPlayer();

        if (p.getWorld().getName().equalsIgnoreCase((MiniGamePlugin.GAME_WORLD_NAME))) {

            // Sets the NBT tag currentGame for the player with the default value "lobby" every time they join
            p.getPersistentDataContainer().set(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING, "lobby");

            // Sets the player's location to the lobby every time they join
            p.teleport((Location) Objects.requireNonNull(MiniGamePlugin.getInstance().getConfig().get("lobby.spawn-location")));

        }
    }

}
