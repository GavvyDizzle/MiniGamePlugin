package com.github.gavvydizzle.minigameplugin.blocks.minesweeper;

import com.github.gavvydizzle.minigameplugin.boards.GameBoard;
import com.github.gavvydizzle.minigameplugin.boards.MinesweeperBoard;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.Collections;

public class MinesweeperEmptyBlock extends MinesweeperBlock {

    private int numAdjacentMines;
    private final Material revealedMaterial;
    private Hologram adjacentMinesDisplay;

    public MinesweeperEmptyBlock(Location l) {
        super(l);
        numAdjacentMines = 0;
        revealedMaterial = Material.IRON_BLOCK;
        adjacentMinesDisplay = null;
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
            //incorrect click
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
            //correct click
            if (!super.isFlagPlaced()) {
                super.setRevealed(true);
                super.getLocation().getBlock().setType(revealedMaterial);
                b.safeSquareRevealed();
                placeNumAdjacentMines();

                if (numAdjacentMines == 0) {
                    //calculating col and row based on location
                    int col = Math.abs(super.getLocation().getBlockX() - b.getOriginLocation().getBlockX());
                    int row = super.getLocation().getBlockY() - b.getOriginLocation().getBlockY();

                    b.revealEmptyAdjacentTiles(col, row);
                }
            }
        }
    }

    // Places the number of adjacent mines above the block after the block has been revealed
    @Override
    public void placeNumAdjacentMines() {
        if (numAdjacentMines > 0) {
            Location textLoc = new Location(super.getLocation().getWorld(), super.getLocation().getBlockX() + 0.5, super.getLocation().getBlockY() + 0.625, super.getLocation().getBlockZ() - 0.25);
            adjacentMinesDisplay = DHAPI.createHologram(getHologramName(textLoc), textLoc, false, Collections.singletonList(String.valueOf(numAdjacentMines)));
        }
    }

    private String getHologramName(Location location) {
        return location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
    }

    /* GETTERS & SETTERS */

    @Override
    public int getNumAdjacentMines() {
        return numAdjacentMines;
    }

    public void setNumAdjacentMines(int mines) {
        numAdjacentMines = mines;
    }

    @Override
    public Hologram getNumAdjacentMinesHologram() {
        return adjacentMinesDisplay;
    }

    @Override
    public Material getRevealedMaterial() {
        return revealedMaterial;
    }
}
