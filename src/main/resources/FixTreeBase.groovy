import eu.matejkormuth.worldprocessor.legacy.Block
import eu.matejkormuth.worldprocessor.GroovyBlockTask
import eu.matejkormuth.worldprocessor.Range
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.BlockFace

class FixTreeBase implements GroovyBlockTask {

    @Override
    Range getRange() {
        return new Range(-5500, -4000, 5500, 4000)
    }

    @Override
    World getWorld() {
        return Bukkit.getWorld("Starving")
    }

    @Override
    int getSpeed() {
        return 16 * 16 * 256 * 8
    }

    @Override
    boolean when(Block b) {
        return b.getType() == Material.WOOL && inRange(0, 5, b.getData())
    }

    @Override
    void then(Block b) {
        org.bukkit.block.Block b1;

        b1 = block.getRelative(BlockFace.EAST);
        if (b1.getType().isSolid()) {
            b1.setType(Material.AIR);
        }

        b1 = block.getRelative(BlockFace.SOUTH);
        if (b1.getType().isSolid()) {
            b1.setType(Material.AIR);
        }

        b1 = block.getRelative(BlockFace.NORTH);
        if (b1.getType().isSolid()) {
            b1.setType(Material.AIR);
        }

        b1 = block.getRelative(BlockFace.WEST);
        if (b1.getType().isSolid()) {
            b1.setType(Material.AIR);
        }

        b1 = block.getRelative(BlockFace.NORTH_EAST);
        if (b1.getType().isSolid()) {
            b1.setType(Material.AIR);
        }

        b1 = block.getRelative(BlockFace.NORTH_WEST);
        if (b1.getType().isSolid()) {
            b1.setType(Material.AIR);
        }

        b1 = block.getRelative(BlockFace.SOUTH_WEST);
        if (b1.getType().isSolid()) {
            b1.setType(Material.AIR);
        }

        b1 = block.getRelative(BlockFace.SOUTH_EAST);
        if (b1.getType().isSolid()) {
            b1.setType(Material.AIR);
        }
    }
}