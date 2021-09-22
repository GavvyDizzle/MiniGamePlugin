package me.gavvydizzle.minigameplugin.blocks.picross;

import me.gavvydizzle.minigameplugin.boards.GameBoard;
import me.gavvydizzle.minigameplugin.boards.PicrossBoard;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class PicrossFilledBlock extends PicrossBlock {

    private final Material revealedMaterial;

    public PicrossFilledBlock(Location l) {
        super(l);
        super.setFilled(true);
        revealedMaterial = Material.LIGHT_BLUE_CONCRETE;
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

        if (action == Action.RIGHT_CLICK_BLOCK) {
            //incorrect click
            b.madeMistake();
            super.placeButton();
        }

        super.getLocation().getBlock().setType(revealedMaterial);
        b.filledTileClicked();
        super.setRevealed(true);
    }
}
