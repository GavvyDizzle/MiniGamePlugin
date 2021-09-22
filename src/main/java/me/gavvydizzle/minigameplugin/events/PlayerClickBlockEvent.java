package me.gavvydizzle.minigameplugin.events;

import me.gavvydizzle.minigameplugin.MiniGamePlugin;
import me.gavvydizzle.minigameplugin.boards.MinesweeperBoard;
import me.gavvydizzle.minigameplugin.boards.PicrossBoard;
import me.gavvydizzle.minigameplugin.managers.MinesweeperManager;
import me.gavvydizzle.minigameplugin.managers.PicrossManager;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerClickBlockEvent implements Listener {

    @EventHandler
    public void onPlayerClickBlockEvent(PlayerInteractEvent e) {

        Player p = e.getPlayer();

        // Specified to listed to only the main hand so a right click only happens once
        if ( Objects.equals(e.getHand(), EquipmentSlot.HAND) && (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {

            Location loc = Objects.requireNonNull(e.getClickedBlock()).getLocation();

            // Checks the value of the currentGame string saved in the player's NBT data
            switch (Objects.requireNonNull(p.getPersistentDataContainer().get(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING))) {
                case "picross":

                    e.setCancelled(true);

                    // End check if the player clicked a button
                    if (Tag.BUTTONS.isTagged(e.getClickedBlock().getType()) ) {
                        return;
                    }

                    PicrossBoard picrossBoard = null;
                    for (PicrossBoard board : PicrossManager.getManager().getGameBoards()) {
                        if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                            picrossBoard = board;
                            break;
                        }
                    }
                    // Check arena validity
                    if (picrossBoard == null) {
                        p.sendMessage("On block click: No linked picross board");
                        return;
                    }

                    // If game over, don't allow the player to interact with the board
                    if (picrossBoard.isGameOver()) {
                        return;
                    }

                    // If the location of the block is equal to one of the blocks in the game
                    // handle the block click then check to see if the game ended
                    for (int col = 0; col < picrossBoard.getGameBoard().length; col++) {
                        for (int row = 0; row < picrossBoard.getGameBoard()[0].length; row++) {

                            if (!picrossBoard.getGameBoard()[col][row].isRevealed() && picrossBoard.getGameBoard()[col][row].getLocation().equals(loc)) {

                                picrossBoard.getGameBoard()[col][row].handleClick(picrossBoard, p, e.getAction());
                                picrossBoard.checkForGameOver(p);
                            }
                        }
                    }

                    break;


                case "minesweeper":

                    e.setCancelled(true);

                    // End check if the player clicked a flower pot
                    if (Tag.FLOWER_POTS.isTagged(e.getClickedBlock().getType())) {
                        return;
                    }

                    MinesweeperBoard minesweeperBoard = null;
                    for (MinesweeperBoard board : MinesweeperManager.getManager().getGameBoards()) {
                        if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                            minesweeperBoard = board;
                            break;
                        }
                    }
                    // Check arena validity
                    if (minesweeperBoard == null) {
                        p.sendMessage("On block click: No linked minesweeper board");
                        return;
                    }

                    // If game over, don't allow the player to interact with the board
                    if (minesweeperBoard.isGameOver()) {
                        return;
                    }


                    // If the location of the block is equal to one of the blocks in the game
                    // handle the block click then check to see if the game ended
                    for (int col = 0; col < minesweeperBoard.getGameBoard().length; col++) {
                        for (int row = 0; row < minesweeperBoard.getGameBoard()[0].length; row++) {

                            if (!minesweeperBoard.getGameBoard()[col][row].isRevealed() && minesweeperBoard.getGameBoard()[col][row].getLocation().equals(loc)) {

                                minesweeperBoard.getGameBoard()[col][row].handleClick(minesweeperBoard, p, e.getAction());
                                minesweeperBoard.checkForGameOver(p);
                            }
                        }
                    }

                    break;
            }
        }
    }
}
