package me.gavvydizzle.minigameplugin.blocks;

import me.gavvydizzle.minigameplugin.boards.GameBoard;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class Block {

    private boolean isRevealed;
    private boolean isFilled;
    private final Location loc;

    public Block(Location l) {
        isRevealed = false;
        isFilled = false;
        loc = l;
    }

    /**
     * @param board GameBoard to edit
     * @param p The player who clicked the block
     * @param action The type of click (right or left)
     */
    // To be overwritten in child class
    public void handleClick(GameBoard board, Player p, Action action) {}

    /* GETTERS & SETTERS */

    public boolean isRevealed() {
        return isRevealed;
    }

    public void setFilled(boolean b) {
        isFilled = b;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setRevealed(boolean b) {
        isRevealed = b;
    }

    public Location getLocation() {
        return loc;
    }
}