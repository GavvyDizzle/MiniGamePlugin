package me.gavvydizzle.minigameplugin;

import me.gavvydizzle.minigameplugin.commands.PlayPicross;
import me.gavvydizzle.minigameplugin.commands.LeaveCommand;
import me.gavvydizzle.minigameplugin.commands.PlayMinesweeper;
import me.gavvydizzle.minigameplugin.events.*;
import me.gavvydizzle.minigameplugin.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class MiniGamePlugin extends JavaPlugin {

    private static MiniGamePlugin instance;

    public static String GAME_WORLD_NAME;


    @Override
    public void onEnable() {

        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        }

        setupConfig();
        registerEvents();
        registerCommands();

        if (!doesWorldExists()) {
            getLogger().severe("*** The world specified in the config.yml file does not exist ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        }

        instance = this;
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

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerClickBlockEvent(), this);
        getServer().getPluginManager().registerEvents(new StoneHoeClickEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerSwitchHandsEvent(), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("picross")).setExecutor(new PlayPicross());
        Objects.requireNonNull(getCommand("minesweeper")).setExecutor(new PlayMinesweeper());
        Objects.requireNonNull(getCommand("leave")).setExecutor(new LeaveCommand());
    }

    @Override
    public void onDisable() {

        // Remove the player boards on the event of a crash or shutdown
        for (Player p : Bukkit.getOnlinePlayers()) {
            GameManager.removePlayerFromGame(p);
        }
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
}
