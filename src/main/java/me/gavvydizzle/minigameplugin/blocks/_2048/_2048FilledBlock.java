package me.gavvydizzle.minigameplugin.blocks._2048;

import org.bukkit.Material;

public class _2048FilledBlock extends _2048Block {

    // The "nothing" block

    private final int value;
    private boolean hasMergedThisMove;

    public _2048FilledBlock(int value) {
        super(null);
        this.value = value;
        hasMergedThisMove = false;
    }

    /**
     * @return The material based on the block's score
     */
    @Override
    public Material getMaterial() {
        switch (value) {
            case 2:
                return Material.DIRT;
            case 4:
                return Material.OAK_LOG;
            case 8:
                return Material.STONE;
            case 16:
                return Material.COBBLESTONE;
            case 32:
                return Material.IRON_ORE;
            case 64:
                return Material.GOLD_ORE;
            case 128:
                return Material.DIAMOND_ORE;
            case 256:
                return Material.IRON_BLOCK;
            case 512:
                return Material.GOLD_BLOCK;
            case 1024:
                return Material.EMERALD_BLOCK;
            case 2048:
                return Material.DIAMOND_BLOCK;
            default:
                return Material.NETHERITE_BLOCK;
        }
    }

    public int getValue() { return value; }
    public boolean isHasMergedThisMove() { return hasMergedThisMove; }

    @Override
    public void hasMergedThisMove(boolean b) { hasMergedThisMove = b; }

}
