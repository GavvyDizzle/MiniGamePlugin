package me.gavvydizzle.minigameplugin.commands;

import me.gavvydizzle.minigameplugin.MiniGamePlugin;
import me.gavvydizzle.minigameplugin.boards.MinesweeperBoard;
import me.gavvydizzle.minigameplugin.managers.MinesweeperManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayMinesweeper implements CommandExecutor {

    private static final int MAXSIZE = MiniGamePlugin.getInstance().getConfig().getInt("minesweeper.size.max");
    private static final int MINSIZE = MiniGamePlugin.getInstance().getConfig().getInt("minesweeper.size.min");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.getWorld().getName().equalsIgnoreCase((MiniGamePlugin.GAME_WORLD_NAME))) {

                if (args.length == 1) {

                    //If player is not in a game
                    if (!MinesweeperManager.getManager().isInGame(p)) {
                        switch (args[0]) {
                            case "easy":
                            case "e":
                                MinesweeperManager.getManager().createGameBoard(9, 9, 10, p);
                                break;
                            case "medium":
                            case "m":
                                MinesweeperManager.getManager().createGameBoard(16, 16, 40, p);
                                break;
                            case "hard":
                            case "h":
                                MinesweeperManager.getManager().createGameBoard(30, 16, 99, p);
                                break;
                            default:
                                return false;
                        }
                    }
                    // If player is already in a game
                    else {
                        MinesweeperBoard b = null;
                        for (MinesweeperBoard board : MinesweeperManager.getManager().getGameBoards()) {
                            if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                                b = board;
                                break;
                            }
                        }
                        // Check arena validity
                        if (b == null) {
                            p.sendMessage("Play Command: No linked minesweeper board");
                            return true;
                        }

                        switch (args[0]) {
                            case "easy":
                            case "e":
                                MinesweeperManager.getManager().createGameBoard(9, 9, 10, p, b.getID() - 1);
                                break;
                            case "medium":
                            case "m":
                                MinesweeperManager.getManager().createGameBoard(16, 16, 40, p, b.getID() - 1);
                                break;
                            case "hard":
                            case "h":
                                MinesweeperManager.getManager().createGameBoard(30, 16, 99, p, b.getID() - 1);
                                break;
                            default:
                                return false;
                        }
                    }
                } else if ((args.length == 4 && args[0].equals("custom") || args.length == 4 && args[0].equals("c")) && isInteger(args[1]) && isInteger(args[2]) && isInteger(args[3])) {

                    int cols = Integer.parseInt(args[1]);
                    int rows = Integer.parseInt(args[2]);
                    int numMines = Integer.parseInt(args[3]);

                    if (cols >= MINSIZE && rows >= MINSIZE && cols <= MAXSIZE && rows <= MAXSIZE && numMines >= 1 && numMines <= (cols * rows)) {

                        // If player is not in a game
                        if (!MinesweeperManager.getManager().isInGame(p)) {
                            MinesweeperManager.getManager().createGameBoard(cols, rows, numMines, p);
                        }
                        // If in game
                        else {
                            MinesweeperBoard b = null;
                            for (MinesweeperBoard board : MinesweeperManager.getManager().getGameBoards()) {
                                if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                                    b = board;
                                    break;
                                }
                            }
                            // Check arena validity
                            if (b == null) {
                                p.sendMessage("Play Command: No linked minesweeper board");
                                return true;
                            }
                            MinesweeperManager.getManager().createGameBoard(cols, rows, numMines, p, b.getID() - 1);
                        }
                    } else {
                        p.sendMessage("Size: 5x5 to 30x30");
                        p.sendMessage("Mines: 1 to cols * rows");
                    }
                } else if (doesArgsContainCustom(args)) {
                    p.sendMessage("You need to input the column size, row size and number of mines");
                    p.sendMessage("Usage: /minesweeper custom <cols> <rows> <mines>");
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean doesArgsContainCustom(String[] args) {
        for (String s : args) {
            if (s.equals("custom") || s.equals("c")) {
                return true;
            }
        }
        return false;
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
