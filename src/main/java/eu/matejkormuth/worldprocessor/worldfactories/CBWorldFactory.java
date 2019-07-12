package eu.matejkormuth.worldprocessor.worldfactories;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CBWorldFactory implements WorldFactory<CraftWorld> {
    @Override
    public CraftWorld createWorld(World world) {
        return (CraftWorld) world;
    }
}
