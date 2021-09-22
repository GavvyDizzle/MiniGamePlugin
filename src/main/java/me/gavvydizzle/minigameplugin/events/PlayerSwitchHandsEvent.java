package me.gavvydizzle.minigameplugin.events;

import me.gavvydizzle.minigameplugin.MiniGamePlugin;
import me.gavvydizzle.minigameplugin.boards.MinesweeperBoard;
import me.gavvydizzle.minigameplugin.managers.MinesweeperManager;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerSwitchHandsEvent implements Listener {

    @EventHandler
    public void onStoneHoeDrop(PlayerSwapHandItemsEvent e) {

        Player p = e.getPlayer();

        if (p.getWorld().getName().equalsIgnoreCase((MiniGamePlugin.GAME_WORLD_NAME))) {

            // If the player is playing minesweeper
            if (Objects.requireNonNull(p.getPersistentDataContainer().get(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING)).equals("minesweeper")) {
                e.setCancelled(true);

                LivingEntity l = e.getPlayer();
                // Sets block equal to the block the player is targeting to up to a 30 block distance
                Block block = l.getTargetBlockExact(30);

                // If the block they are targeting with their crosshair is air
                if (block == null) {
                    return;
                }

                // Gets the player's current GameBoard
                MinesweeperBoard b = null;
                for (MinesweeperBoard board : MinesweeperManager.getManager().getGameBoards()) {
                    if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                        b = board;
                        break;
                    }
                }

                // If game over, or the board doesn't exist, don't allow the player to interact with the board
                if (b == null || b.isGameOver()) {
                    return;
                }


                Location loc = block.getLocation();

                // Checks all blocks in the board
                for (int col = 0; col < b.getGameBoard().length; col++) {
                    for (int row = 0; row < b.getGameBoard()[0].length; row++) {

                        // If revealed and matches the clicked block, try to reveal the 8 surrounding tiles
                        if (b.getGameBoard()[col][row].isRevealed() && b.getGameBoard()[col][row].getLocation().equals(loc)) {

                            //calculating col and row based on location
                            int c = Math.abs(b.getGameBoard()[col][row].getLocation().getBlockX() - b.getOriginLocation().getBlockX());
                            int r = b.getGameBoard()[col][row].getLocation().getBlockY() - b.getOriginLocation().getBlockY();

                            b.revealAdjacentTiles(c, r);
                            b.checkForGameOver(p);
                        }
                    }
                }
            }
        }
    }

}
