package com.github.gavvydizzle.minigameplugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.github.gavvydizzle.minigameplugin.commands.*;
import com.github.gavvydizzle.minigameplugin.events.*;
import com.github.gavvydizzle.minigameplugin.listeners.packetlisteners.SteerVehicleListener;
import com.github.gavvydizzle.minigameplugin.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class MiniGamePlugin extends JavaPlugin {

    public static String GAME_WORLD_NAME;

    private static MiniGamePlugin instance;
    private static ProtocolManager protocolManager;

    private PlayerCommandManager playerCommandManager;


    @Override
    public void onEnable() {
        instance = this;

        setupConfig();
        registerEvents();

        playerCommandManager = new PlayerCommandManager(getCommand("minigame"));

        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new SteerVehicleListener(this));

        if (!doesWorldExists()) {
            getLogger().severe("*** The world specified in the config.yml file does not exist ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
        }
    }

    private void setupConfig() {
        getConfig().addDefault("minigame-world", "world");

        GAME_WORLD_NAME = Objects.requireNonNull(getConfig().getString("minigame-world"));

        getConfig().addDefault("lobby.spawn-location", new Location(getServer().getWorld(GAME_WORLD_NAME), 0, 0, 0));

        getConfig().addDefault("picross.spawn-location", new Location(getServer().getWorld(GAME_WORLD_NAME), 0, 0, 0));
        getConfig().addDefault("picross.first-board-location", new Location(getServer().getWorld(GAME_WORLD_NAME), 0, 0, 0));
        getConfig().addDefault("picross.distance-apart", 35);
        getConfig().addDefault("picross.max-games", 10);
        getConfig().addDefault("picross.size.min", 5);
        getConfig().addDefault("picross.size.max", 15);

        getConfig().addDefault("minesweeper.spawn-location", new Location(getServer().getWorld(GAME_WORLD_NAME), 0, 0, 0));
        getConfig().addDefault("minesweeper.first-board-location", new Location(getServer().getWorld(GAME_WORLD_NAME), 0, 0, 0));
        getConfig().addDefault("minesweeper.distance-apart", 35);
        getConfig().addDefault("minesweeper.max-games", 10);
        getConfig().addDefault("minesweeper.size.min", 5);
        getConfig().addDefault("minesweeper.size.max", 30);

        getConfig().addDefault("snake.spawn-location", new Location(getServer().getWorld(GAME_WORLD_NAME), 0, 0, 0));
        getConfig().addDefault("snake.first-board-location", new Location(getServer().getWorld(GAME_WORLD_NAME), 0, 0, 0));
        getConfig().addDefault("snake.distance-apart", 35);
        getConfig().addDefault("snake.max-games", 10);
        getConfig().addDefault("snake.size", 15);

        getConfig().addDefault("2048.spawn-location", new Location(getServer().getWorld(GAME_WORLD_NAME), 0, 0, 0));
        getConfig().addDefault("2048.first-board-location", new Location(getServer().getWorld(GAME_WORLD_NAME), 0, 0, 0));
        getConfig().addDefault("2048.distance-apart", 20);
        getConfig().addDefault("2048.max-games", 10);
        getConfig().addDefault("2048.size", 4);



        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerClickBlockEvent(), this);
        getServer().getPluginManager().registerEvents(new StoneHoeClickEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerSwitchHandsEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerClickSignEvent(), this);
    }

    @Override
    public void onDisable() {

        // Remove the player boards on the event of a crash or shutdown
        for (Player p : Objects.requireNonNull(Bukkit.getWorld(GAME_WORLD_NAME)).getPlayers()) {
            GameManager.removePlayerFromGame(p);
        }

        // Removes the packet listeners associated with this plugin
        protocolManager.removePacketListeners(this);
    }

    private boolean doesWorldExists() {
        for (World world : Bukkit.getServer().getWorlds()) {
            if (world.getName().equalsIgnoreCase(GAME_WORLD_NAME)) {
                return true;
            }
        }
        return false;
    }

    public static MiniGamePlugin getInstance() {
        return instance;
    }

    public PlayerCommandManager getPlayerCommandManager() {
        return playerCommandManager;
    }
}
