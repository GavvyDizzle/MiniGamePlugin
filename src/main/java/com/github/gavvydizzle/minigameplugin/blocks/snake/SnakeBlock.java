package com.github.gavvydizzle.minigameplugin.blocks.snake;

import com.github.gavvydizzle.minigameplugin.blocks.Block;
import com.github.gavvydizzle.minigameplugin.boards.GameBoard;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

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

    @Override
    public void handleClick(GameBoard board, Player p, Action action) {

    }
}
