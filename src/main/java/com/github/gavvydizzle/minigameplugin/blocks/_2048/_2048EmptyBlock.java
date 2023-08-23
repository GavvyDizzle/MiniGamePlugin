package com.github.gavvydizzle.minigameplugin.blocks._2048;

import org.bukkit.Material;

public class _2048EmptyBlock extends _2048Block {

    // The "nothing" block

    private final Material material;

    public _2048EmptyBlock() {
        super(null);
        material = Material.WHITE_CONCRETE;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

}
