package me.gavvydizzle.minigameplugin.commands;

import me.gavvydizzle.minigameplugin.MiniGamePlugin;
import me.gavvydizzle.minigameplugin.managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LeaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.getWorld().getName().equalsIgnoreCase((MiniGamePlugin.GAME_WORLD_NAME))) {

                // Remove the sender of the command from the current game
                GameManager.removePlayerFromGame(p);

            }
        }
        return true;
    }
}
