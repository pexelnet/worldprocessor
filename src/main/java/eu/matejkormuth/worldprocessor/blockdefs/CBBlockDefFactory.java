package eu.matejkormuth.worldprocessor.blockdefs;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CBBlockDefFactory implements BlockDefFactory<CraftWorld, CBBlockDef> {
    @Override
    public CBBlockDef supply(int x, int y, int z, CraftWorld world) {
        return new CBBlockDef(x, y, z, world);
    }
}
