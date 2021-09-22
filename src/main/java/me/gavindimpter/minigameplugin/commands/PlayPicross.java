package me.gavindimpter.minigameplugin.commands;

import me.gavindimpter.minigameplugin.MiniGamePlugin;
import me.gavindimpter.minigameplugin.boards.PicrossBoard;
import me.gavindimpter.minigameplugin.managers.PicrossManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayPicross implements CommandExecutor {

    private static final int MAXSIZE = MiniGamePlugin.getInstance().getConfig().getInt("picross.size.max");
    private static final int MINSIZE = MiniGamePlugin.getInstance().getConfig().getInt("picross.size.min");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (( args.length == 1 || args.length == 2) && isInteger(args[0]) ) {

                int cols = Integer.parseInt(args[0]);
                int rows;

                // Set rows to the first or second arg
                if (args.length == 1) {
                    rows = Integer.parseInt(args[0]);
                }
                else if (isInteger(args[1])){
                    rows = Integer.parseInt(args[1]);
                }
                else {
                    return false;
                }

                // Check if the arg(s) are within the specified size
                if (cols <= MAXSIZE && rows <= MAXSIZE && cols >= MINSIZE && rows >= MINSIZE) {
                    // If the player is not in a game
                    if (!PicrossManager.getManager().isInGame(p)) {
                        PicrossManager.getManager().createGameBoard(cols, rows, p);
                    }
                    // If player in game
                    else {
                        PicrossBoard b = null;
                        for (PicrossBoard board : PicrossManager.getManager().getGameBoards()) {
                            if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                                b = board;
                                break;
                            }
                        }
                        // Check arena validity
                        if (b == null) {
                            p.sendMessage("Play Command: No linked picross board");
                            return true;
                        }

                        PicrossManager.getManager().createGameBoard(cols, rows, p, b.getID() - 1);
                    }
                }
                else {
                    p.sendMessage(ChatColor.RED + "The size must be between " + MINSIZE + " and " + MAXSIZE);
                    return false;
                }
            }
            else {
                return false;
            }
        }

        return true;
    }

    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
