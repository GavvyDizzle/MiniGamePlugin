package me.gavvydizzle.minigameplugin.commands;

import me.gavvydizzle.minigameplugin.managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;

            // Remove the sender of the command from the current game
            GameManager.removePlayerFromGame(p);
        }
        return true;
    }
}
