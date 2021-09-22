package me.gavvydizzle.minigameplugin.events;

import me.gavvydizzle.minigameplugin.MiniGamePlugin;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerJoinEvent implements Listener {

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {

        Player p = e.getPlayer();

        // Sets the NBT tag currentGame for the player with the default value "lobby" every time they join
        p.getPersistentDataContainer().set(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING, "lobby");

        // Sets the player's location to the lobby every time they join
        p.teleport((Location) Objects.requireNonNull(MiniGamePlugin.getInstance().getConfig().get("lobby.spawn-location")));
    }

}
