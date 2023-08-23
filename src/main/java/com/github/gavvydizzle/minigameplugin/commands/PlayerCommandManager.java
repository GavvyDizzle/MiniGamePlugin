package com.github.gavvydizzle.minigameplugin.commands;

import com.github.gavvydizzle.minigameplugin.commands.player.LeaveCommand;
import com.github.gavvydizzle.minigameplugin.commands.player.PlayCommand;
import com.github.gavvydizzle.minigameplugin.commands.player.PlayerHelpCommand;
import com.github.mittenmc.serverutils.CommandManager;
import org.bukkit.command.PluginCommand;

public class PlayerCommandManager extends CommandManager {

    public PlayerCommandManager(PluginCommand command) {
        super(command);

        registerCommand(new LeaveCommand(this));
        registerCommand(new PlayCommand(this));
        registerCommand(new PlayerHelpCommand(this));
        sortCommands();
    }
}
