package me.gavvydizzle.minigameplugin.blocks.snake;

import me.gavvydizzle.minigameplugin.blocks.Block;
import org.bukkit.Material;

public class SnakeBlock extends Block {

    // The "nothing" block

    private final int col;
    private final int row;
    private static final Material material = Material.BLACK_CONCRETE;

    public SnakeBlock(int col, int row) {
        super(null);
        this.col = col;
        this.row = row;
    }

    public static Material getMaterial() {
        return material;
    }

    public int getCol() {
        return col;
    }
    public int getRow() {
        return row;
    }
}
