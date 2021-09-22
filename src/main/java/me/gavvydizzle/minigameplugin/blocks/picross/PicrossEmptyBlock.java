package me.gavvydizzle.minigameplugin.blocks.picross;

import me.gavvydizzle.minigameplugin.boards.GameBoard;
import me.gavvydizzle.minigameplugin.boards.PicrossBoard;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class PicrossEmptyBlock extends PicrossBlock {

    private final Material revealedMaterial;

    public PicrossEmptyBlock(Location l) {
        super(l);
        revealedMaterial = Material.LIGHT_GRAY_CONCRETE;
    }

    /**
     * Does the necessary actions given a right or left click
     *
     * @param board GameBoard to edit
     * @param p The player who clicked the block
     * @param action The type of click (right or left)
     */
    // Must cast the GameBoard parameter as a board of this type
    @Override
    public void handleClick(GameBoard board, Player p, Action action) {
        PicrossBoard b = (PicrossBoard) board;

        if (action == Action.LEFT_CLICK_BLOCK) {
            //incorrect click
            b.madeMistake();
            placeButton();
        }

        super.getLocation().getBlock().setType(revealedMaterial);
        super.setRevealed(true);
    }
}
