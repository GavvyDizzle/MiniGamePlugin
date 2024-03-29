package com.github.gavvydizzle.minigameplugin.blocks.minesweeper;

import com.github.gavvydizzle.minigameplugin.boards.MinesweeperBoard;
import com.github.gavvydizzle.minigameplugin.boards.GameBoard;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class MinesweeperFilledBlock extends MinesweeperBlock {

    private final Material revealedMaterial;

    public MinesweeperFilledBlock(Location l) {
        super(l);
        super.setFilled(true);
        revealedMaterial = Material.TNT;
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
        MinesweeperBoard b = (MinesweeperBoard) board;

        if (action == Action.RIGHT_CLICK_BLOCK) {
            //correct click
            if (super.isFlagPlaced()) {
                b.removeFlag();
            }
            else {
                b.addFlag();
            }
            b.updateFlagsRemaining();
            super.toggleFlagPlacement();
            super.toggleFlagPlaced();
        }
        else {
            //incorrect click - game over
            if (!super.isFlagPlaced()) {
                super.setRevealed(true);
                super.getLocation().getBlock().setType(revealedMaterial);
                b.setGameOver(true);
                p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
            }
        }
    }

    @Override
    public void placeNumAdjacentMines() {

    }

    @Override
    public Hologram getNumAdjacentMinesHologram() {
        return null;
    }

    @Override
    public int getNumAdjacentMines() {
        return 0;
    }


    /* GETTERS */

    @Override
    public Material getRevealedMaterial() {
        return revealedMaterial;
    }
}
