package me.gavvydizzle.minigameplugin.events;

import me.gavvydizzle.minigameplugin.MiniGamePlugin;
import me.gavvydizzle.minigameplugin.boards.GameBoard;
import me.gavvydizzle.minigameplugin.boards.MinesweeperBoard;
import me.gavvydizzle.minigameplugin.boards.PicrossBoard;
import me.gavvydizzle.minigameplugin.managers.MinesweeperManager;
import me.gavvydizzle.minigameplugin.managers.PicrossManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class StoneHoeClickEvent implements Listener {

    @EventHandler
    public void onStoneHoeClick(PlayerInteractEvent e) {
        Action a = e.getAction();
        Player p = e.getPlayer();

        // If the player is holding a stone hoe, and they right-click or left-click the air
        if (e.getPlayer().getInventory().getItemInMainHand().equals(new ItemStack(Material.STONE_HOE)) && (a == Action.LEFT_CLICK_AIR || a == Action.RIGHT_CLICK_AIR)) {

            LivingEntity l = e.getPlayer();
            // Sets block equal to the block the player is targeting to up to a 30 block distance
            Block block = l.getTargetBlockExact(30);

            // If the block they are targeting with their crosshair is air
            if (block == null) {
                return;
            }

            GameBoard b = null;

            // Obtains the correct GameBoard the player is in given the game
            switch(Objects.requireNonNull(p.getPersistentDataContainer().get(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING))) {
                case "picross":

                    for (PicrossBoard board : PicrossManager.getManager().getGameBoards()) {
                        if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                            b = board;
                            break;
                        }
                    }
                    break;
                case "minesweeper":

                    for (MinesweeperBoard board : MinesweeperManager.getManager().getGameBoards()) {
                        if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                            b = board;
                            break;
                        }
                    }
                    break;
            }

            // If game over, or the board doesn't exist, don't allow the player to interact with the board
            if (b == null || b.isGameOver()) {
                return;
            }

            Location loc = block.getLocation();

            // Checks all the blocks in the GameBoard
            for (int col = 0; col < b.getGameBoard().length; col++) {
                for (int row = 0; row < b.getGameBoard()[0].length; row++) {

                    //If the block hasn't been clicked and the location matches
                    if (!b.getGameBoard()[col][row].isRevealed() && b.getGameBoard()[col][row].getLocation().equals(loc)) {

                        if (a == Action.LEFT_CLICK_AIR) {
                            b.getGameBoard()[col][row].handleClick(b, p, Action.LEFT_CLICK_BLOCK);
                        }
                        else { // Action = Action.RIGHT_CLICK_AIR
                            b.getGameBoard()[col][row].handleClick(b, p, Action.RIGHT_CLICK_BLOCK);
                        }

                        b.checkForGameOver(e.getPlayer());
                    }
                }
            }
        }
    }
}
