package me.gavindimpter.minigameplugin.blocks.picross;

import me.gavindimpter.minigameplugin.blocks.Block;
import me.gavindimpter.minigameplugin.boards.GameBoard;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class PicrossBlock extends Block {

    private final Material material;

    public PicrossBlock(Location l) {
        super(l);
        material = Material.IRON_BLOCK;
    }

    /**
     * @param board GameBoard to edit
     * @param p The player who clicked the block
     * @param action The type of click (right or left)
     */
    // To be overwritten in child class
    public void handleClick(GameBoard board, Player p, Action action) {}

    /**
     * Places the button on the block
     */
    // Called on an incorrect click
    public void placeButton() {
        Location buttonLoc = new Location(super.getLocation().getWorld(), super.getLocation().getBlockX(), super.getLocation().getBlockY(), super.getLocation().getBlockZ());
        buttonLoc.setZ( buttonLoc.getBlockZ() - 1 );
        buttonLoc.getBlock().setType(Material.OAK_BUTTON);
    }


    /* GETTERS */

    public Material getMaterial() {
        return material;
    }

    public Location getLocation() {
        return super.getLocation();
    }
}
