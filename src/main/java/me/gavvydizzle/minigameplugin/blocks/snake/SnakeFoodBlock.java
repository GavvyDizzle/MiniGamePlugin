package me.gavvydizzle.minigameplugin.blocks.snake;

import org.bukkit.Material;

public class SnakeFoodBlock extends SnakeBlock {

    private static final Material material = Material.RED_CONCRETE;

    public SnakeFoodBlock(int col, int row) {
        super(col, row);
    }

    public static Material getMaterial() {
        return material;
    }
}
