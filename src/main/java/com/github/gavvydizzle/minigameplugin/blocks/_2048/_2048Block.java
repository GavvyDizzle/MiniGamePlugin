package com.github.gavvydizzle.minigameplugin.blocks._2048;

import com.github.gavvydizzle.minigameplugin.blocks.Block;
import com.github.gavvydizzle.minigameplugin.boards.GameBoard;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class _2048Block extends Block {

    public _2048Block(Location l) {
        super(l);
    }

    @Override
    public void handleClick(GameBoard board, Player p, Action action) {

    }

    public Material getMaterial() {
        return null;
    }

    public void hasMergedThisMove(boolean b){}
}
