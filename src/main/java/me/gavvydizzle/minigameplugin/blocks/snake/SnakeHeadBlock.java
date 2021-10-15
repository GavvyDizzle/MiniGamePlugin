package me.gavvydizzle.minigameplugin.blocks.snake;

import org.bukkit.Material;

public class SnakeHeadBlock extends SnakeBlock {

    private static final Material material = Material.GREEN_CONCRETE;

    public SnakeHeadBlock(int col, int row) {
        super(col, row);
    }

    public static Material getMaterial() {
        return material;
    }
}
