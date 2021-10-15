package me.gavvydizzle.minigameplugin.blocks.snake;

import org.bukkit.Material;

public class SnakeBodyBlock extends SnakeBlock {

    private static final Material material = Material.LIME_CONCRETE;

    public SnakeBodyBlock(int col, int row) {
        super(col, row);
    }

    public static Material getMaterial() {
        return material;
    }
}
