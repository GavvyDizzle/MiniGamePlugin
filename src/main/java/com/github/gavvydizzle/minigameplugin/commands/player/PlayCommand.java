package com.github.gavvydizzle.minigameplugin.commands.player;

import com.github.gavvydizzle.minigameplugin.MiniGamePlugin;
import com.github.gavvydizzle.minigameplugin.boards.MinesweeperBoard;
import com.github.gavvydizzle.minigameplugin.boards.PicrossBoard;
import com.github.gavvydizzle.minigameplugin.boards.SnakeBoard;
import com.github.gavvydizzle.minigameplugin.boards._2048Board;
import com.github.gavvydizzle.minigameplugin.commands.PlayerCommandManager;
import com.github.gavvydizzle.minigameplugin.managers.MinesweeperManager;
import com.github.gavvydizzle.minigameplugin.managers.PicrossManager;
import com.github.gavvydizzle.minigameplugin.managers.SnakeManager;
import com.github.gavvydizzle.minigameplugin.managers._2048Manager;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayCommand extends SubCommand {

    private static final int MINESWEEPER_MAXSIZE = MiniGamePlugin.getInstance().getConfig().getInt("minesweeper.size.max");
    private static final int MINESWEEPER_MINSIZE = MiniGamePlugin.getInstance().getConfig().getInt("minesweeper.size.min");

    private static final int PICROSS_MAXSIZE = MiniGamePlugin.getInstance().getConfig().getInt("picross.size.max");
    private static final int PICROSS_MINSIZE = MiniGamePlugin.getInstance().getConfig().getInt("picross.size.min");

    private final List<String> gameList = new ArrayList<>(Arrays.asList("2048", "minesweeper", "picross", "snake"));
    private final List<String> minesweeperArgs = new ArrayList<>(Arrays.asList("easy", "medium", "hard", "custom"));

    public PlayCommand(PlayerCommandManager playerCommandManager) {
        setName("play");
        setDescription("Join a new game");
        setSyntax("/" + playerCommandManager.getCommandDisplayName() + " play <game> ...");
        setColoredSyntax(ChatColor.YELLOW + getSyntax());
        setPermission(playerCommandManager.getPermissionPrefix() + getName().toLowerCase());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p) || !p.getWorld().getName().equalsIgnoreCase((MiniGamePlugin.GAME_WORLD_NAME))) return;

        if (args.length < 2) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        switch (args[1].toLowerCase()) {
            case "2048" -> {
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
                        return;
                    }

                    _2048Manager.getManager().createGameBoard(p, b.getID() - 1);
                }
            }
            case "minesweeper" -> {
                if (args.length == 3) {

                    //If player is not in a game
                    if (!MinesweeperManager.getManager().isInGame(p)) {
                        switch (args[2]) {
                            case "easy", "e" -> MinesweeperManager.getManager().createGameBoard(9, 9, 10, p);
                            case "medium", "m" -> MinesweeperManager.getManager().createGameBoard(16, 16, 40, p);
                            case "hard", "h" -> MinesweeperManager.getManager().createGameBoard(30, 16, 99, p);
                            default -> sender.sendMessage(ChatColor.YELLOW + "Please select a difficulty");
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
                            return;
                        }

                        switch (args[2]) {
                            case "easy" -> MinesweeperManager.getManager().createGameBoard(9, 9, 10, p, b.getID() - 1);
                            case "medium" ->
                                    MinesweeperManager.getManager().createGameBoard(16, 16, 40, p, b.getID() - 1);
                            case "hard" ->
                                    MinesweeperManager.getManager().createGameBoard(30, 16, 99, p, b.getID() - 1);
                            default -> sender.sendMessage(ChatColor.YELLOW + "Please select a difficulty");
                        }
                    }
                } else if ((args.length == 6 && args[2].equals("custom") && isInteger(args[3]) && isInteger(args[4]) && isInteger(args[5]))) {

                    int cols = Integer.parseInt(args[3]);
                    int rows = Integer.parseInt(args[4]);
                    int numMines = Integer.parseInt(args[5]);

                    if (cols >= MINESWEEPER_MINSIZE && rows >= MINESWEEPER_MINSIZE && cols <= MINESWEEPER_MAXSIZE && rows <= MINESWEEPER_MAXSIZE && numMines >= 1 && numMines <= (cols * rows)) {

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
                                return;
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
                    return;
                }
            }
            case "picross" -> {
                if ((args.length == 3 || args.length == 4) && isInteger(args[2])) {

                    int cols = Integer.parseInt(args[2]);
                    int rows;

                    // Set rows to the first or second arg
                    if (args.length == 3) {
                        rows = Integer.parseInt(args[2]);
                    } else if (isInteger(args[3])) {
                        rows = Integer.parseInt(args[3]);
                    } else {
                        sender.sendMessage(ChatColor.YELLOW + "Please provide a valid board size");
                        return;
                    }

                    // Check if the arg(s) are within the specified size
                    if (cols <= PICROSS_MAXSIZE && rows <= PICROSS_MAXSIZE && cols >= PICROSS_MINSIZE && rows >= PICROSS_MINSIZE) {
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
                                return;
                            }

                            PicrossManager.getManager().createGameBoard(cols, rows, p, b.getID() - 1);
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "The size must be between " + PICROSS_MINSIZE + " and " + PICROSS_MAXSIZE);
                        return;
                    }
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Please provide a board size");
                    return;
                }
            }
            case "snake" -> {
                // If the player is not in a game
                if (!SnakeManager.getManager().isInGame(p)) {
                    SnakeManager.getManager().createGameBoard(p);
                }
                // If player in game
                else {
                    SnakeBoard b = null;
                    for (SnakeBoard board : SnakeManager.getManager().getGameBoards()) {
                        if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                            b = board;
                            break;
                        }
                    }
                    // Check arena validity
                    if (b == null) {
                        p.sendMessage("Play Command: No linked snake board");
                        return;
                    }

                    SnakeManager.getManager().createGameBoard(p, b.getID() - 1);
                }
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], gameList, list);
        }
        else if (args.length == 3 && args[1].equalsIgnoreCase("minesweeper")) {
            StringUtil.copyPartialMatches(args[2], minesweeperArgs, list);
        }
        return list;
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