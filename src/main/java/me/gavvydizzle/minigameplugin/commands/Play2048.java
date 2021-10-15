package me.gavvydizzle.minigameplugin.commands;

import me.gavvydizzle.minigameplugin.MiniGamePlugin;
import me.gavvydizzle.minigameplugin.boards._2048Board;
import me.gavvydizzle.minigameplugin.managers._2048Manager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Play2048 implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.getWorld().getName().equalsIgnoreCase((MiniGamePlugin.GAME_WORLD_NAME))) {

                // If the player is not in a game
                if (!_2048Manager.getManager().isInGame(p)) {
                    _2048Manager.getManager().createGameBoard(p);
                }
                // If player in game
                else {
                    _2048Board b = null;
                    for (_2048Board board : _2048Manager.getManager().getGameBoards()) {
                        if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                            b = board;
                            break;
                        }
                    }
                    // Check arena validity
                    if (b == null) {
                        p.sendMessage("Play Command: No linked 2048 board");
                        return true;
                    }

                    _2048Manager.getManager().createGameBoard(p, b.getID() - 1);
                }
            }
        }
        return true;
    }
}
