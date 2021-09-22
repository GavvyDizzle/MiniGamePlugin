package me.gavindimpter.minigameplugin.blocks.minesweeper;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import me.gavindimpter.minigameplugin.blocks.Block;
import me.gavindimpter.minigameplugin.boards.GameBoard;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class MinesweeperBlock extends Block {

    private boolean isFlagPlaced;
    private final Material material;

    public MinesweeperBlock(Location l) {
        super(l);
        isFlagPlaced = false;
        material = Material.POLISHED_ANDESITE;
    }

    /**
     * @param board GameBoard to edit
     * @param p The player who clicked the block
     * @param action The type of click (right or left)
     */
    // To be overwritten in child class
    @Override
    public void handleClick(GameBoard board, Player p, Action action) {}

    /**
     * Toggles the placement of the flag
     */
    // Called when a block is right-clicked
    public void toggleFlagPlacement() {
        Location flagLoc = new Location(super.getLocation().getWorld(), super.getLocation().getBlockX(), super.getLocation().getBlockY(), super.getLocation().getBlockZ() - 1);
        if (isFlagPlaced) {
            flagLoc.getBlock().setType(Material.AIR);
        }
        else {
            flagLoc.getBlock().setType(Material.FLOWER_POT);
        }
    }

    // To be overwritten in child class
    public void placeNumAdjacentMines() {}

    // To be overwritten in child class
    public Hologram getNumAdjacentMinesHologram() {
        return null;
    }

    // To be overwritten in child class
    public int getNumAdjacentMines() {
        return -1;
    }

    // To be overwritten in child class
    public Material getRevealedMaterial() {
        return null;
    }


    /* GETTERS & SETTERS */

    public boolean isFlagPlaced() {
        return isFlagPlaced;
    }

    public void toggleFlagPlaced() {
        isFlagPlaced = !isFlagPlaced;
    }

    public boolean isRevealed() {
        return super.isRevealed();
    }

    public boolean isFilled() {
        return super.isFilled();
    }

    public Location getLocation() {
        return super.getLocation();
    }

    public Material getMaterial() {
        return material;
    }
}
