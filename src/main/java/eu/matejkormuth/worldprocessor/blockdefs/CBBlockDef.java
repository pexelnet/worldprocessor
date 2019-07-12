package eu.matejkormuth.worldprocessor.blockdefs;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CBBlockDef extends BlockDef<CraftWorld> {

    private org.bukkit.block.Block block;

    public CBBlockDef(int x, int y, int z, CraftWorld world) {
        super(x, y, z, world);
    }

    // Lazy load.
    private void load() {
        if (block == null) {
            block = world.getBlockAt(x, y, z);
        }
    }

    @Override
    public void setType(Material material) {
        load();
        block.setType(material);
    }

    @Override
    public void setType(int material) {
        load();
        block.setTypeId(material);
    }

    @Override
    public void setType(int material, boolean applyPhysics) {
        load();
        block.setTypeId(material, applyPhysics);
    }

    @Override
    public Material getType() {
        load();
        return block.getType();
    }

    @Override
    public int getTypeId() {
        load();
        return block.getTypeId();
    }

    @Override
    public void setData(byte data) {
        load();
        block.setData(data);
    }

    @Override
    public void setData(byte data, boolean applyPhysics) {
        load();
        block.setData(data, applyPhysics);
    }

    @Override
    public byte getData() {
        load();
        return block.getData();
    }

    @Override
    public BlockDef getRelative(BlockFace face) {
        return new CBBlockDef(x + face.getModX(), y + face.getModY(), z + face.getModZ(), world);
    }

    @Override
    public BlockDef getRelative(int modx, int mody, int modz) {
        return new CBBlockDef(x + modx, y + mody, z + modz, world);
    }
}
