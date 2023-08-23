package com.github.gavvydizzle.minigameplugin.commands.player;

import com.github.gavvydizzle.minigameplugin.MiniGamePlugin;
import com.github.gavvydizzle.minigameplugin.commands.PlayerCommandManager;
import com.github.gavvydizzle.minigameplugin.managers.GameManager;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LeaveCommand extends SubCommand {

    public LeaveCommand(PlayerCommandManager playerCommandManager) {
        setName("leave");
        setDescription("Leave your current game");
        setSyntax("/" + playerCommandManager.getCommandDisplayName() + " leave");
        setColoredSyntax(ChatColor.YELLOW + getSyntax());
        setPermission(playerCommandManager.getPermissionPrefix() + getName().toLowerCase());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player && player.getWorld().getName().equalsIgnoreCase((MiniGamePlugin.GAME_WORLD_NAME))) {
            GameManager.removePlayerFromGame(player);
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}