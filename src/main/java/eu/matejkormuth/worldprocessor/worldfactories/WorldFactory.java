package eu.matejkormuth.worldprocessor.worldfactories;

import org.bukkit.World;

public interface WorldFactory<W> {
    W createWorld(World world);
}
