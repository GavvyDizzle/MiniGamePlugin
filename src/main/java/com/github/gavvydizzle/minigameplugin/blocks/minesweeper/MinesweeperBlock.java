package com.github.gavvydizzle.minigameplugin.blocks.minesweeper;

import com.github.gavvydizzle.minigameplugin.blocks.Block;
import com.github.gavvydizzle.minigameplugin.boards.GameBoard;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.Objects;

public abstract class MinesweeperBlock extends Block {

    private boolean isFlagPlaced;
    private final Material material;
    private ItemDisplay stickDisplay, flagDisplay;

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
        if (isFlagPlaced) {
            removeFlagDisplays();
        }
        else {
            placeFlagDisplays();
        }
    }

    private void placeFlagDisplays() {
        stickDisplay = (ItemDisplay) Objects.requireNonNull(super.getLocation().getWorld()).spawnEntity(super.getLocation().clone().add(0.8, 0.45, -0.05), EntityType.ITEM_DISPLAY);
        stickDisplay.setPersistent(false);
        stickDisplay.setItemStack(new ItemStack(Material.STICK));
        stickDisplay.setTransformation(new Transformation(
                stickDisplay.getTransformation().getTranslation(),
                stickDisplay.getTransformation().getLeftRotation().rotateXYZ( 0f, 0f, (float) Math.toRadians(-45)),
                new Vector3f(0.5f, 0.7f, 0.5f),
                stickDisplay.getTransformation().getRightRotation()
        ));

        flagDisplay = (ItemDisplay) Objects.requireNonNull(super.getLocation().getWorld()).spawnEntity(super.getLocation().clone().add(0.5, 0.625, -0.06), EntityType.ITEM_DISPLAY);
        flagDisplay.setPersistent(false);
        flagDisplay.setItemStack(new ItemStack(Material.RED_CONCRETE));
        flagDisplay.setBrightness(new Display.Brightness(15, 15));
        flagDisplay.setTransformation(new Transformation(
                flagDisplay.getTransformation().getTranslation(),
                flagDisplay.getTransformation().getLeftRotation(),
                new Vector3f(0.6f, 0.45f, 0.04f),
                flagDisplay.getTransformation().getRightRotation()
        ));
    }

    public void removeFlagDisplays() {
        if (isFlagPlaced) {
            stickDisplay.remove();
            flagDisplay.remove();
        }
    }

    public abstract void placeNumAdjacentMines();

    public abstract Hologram getNumAdjacentMinesHologram();

    public abstract int getNumAdjacentMines();

    public abstract Material getRevealedMaterial();


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
