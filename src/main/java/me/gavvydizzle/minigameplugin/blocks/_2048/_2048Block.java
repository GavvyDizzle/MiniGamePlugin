package me.gavvydizzle.minigameplugin.blocks._2048;

import me.gavvydizzle.minigameplugin.blocks.Block;
import org.bukkit.Location;
import org.bukkit.Material;

public class _2048Block extends Block {

    public _2048Block(Location l) {
        super(l);
    }

    public Material getMaterial() {
        return null;
    }

    public void hasMergedThisMove(boolean b){}
}
