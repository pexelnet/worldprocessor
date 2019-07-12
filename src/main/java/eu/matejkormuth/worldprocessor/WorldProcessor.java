/**
 * worldprocessor - tasks for world
 * Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.matejkormuth.worldprocessor;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import eu.matejkormuth.worldprocessor.legacy.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static eu.matejkormuth.worldprocessor.Action.action;
import static eu.matejkormuth.worldprocessor.Rule.rule;

public class WorldProcessor extends JavaPlugin implements Listener {

    /**
     * Ticks in one second.
     */
    public static final long SECOND = 20L;

    @Override
    public void onEnable() {
        getCommand("kokot").setExecutor(this::onKokot);
        getCommand("kokot1").setExecutor(this::onKokot1);
        getCommand("kokot2").setExecutor(this::onKokot2);
        getCommand("kokot3").setExecutor(this::onKokot3);
        getCommand("kokot4").setExecutor(this::onKokot4);
        getCommand("kokot5").setExecutor(this::onKokot5);
    }

    private boolean onKokot4(CommandSender sender, Command command, String label, String[] args) {
        Range range = new Range(1, 1, 8192, 8192);
        World starvingTiger = Bukkit.getWorld("starvingMacka");

        byte[] bytes = new byte[8192 * 8192 + 1];

        SingleTask createHeightmap = SingleTask.create("Heightmap")
                .onRegion(starvingTiger, range)
                .whenPos(pos -> bytes[pos.x * 8192 + pos.z] == 0)
                .then(block -> {
                    int x = block.getX();
                    int z = block.getZ();
                    int y = ((CraftWorld) block.getWorld()).getHandle().getHighestBlockYAt(new BlockPosition(x, 0, z)).getY();
                    bytes[x * 8192 + z] = (byte) (y & 0xFF);
                });

        createHeightmap.runAtSpeed(16 * 16 * 256 * 8);
        createHeightmap.finish(() -> {
            try {
                Path file = getDataFolder().toPath().resolve("heightmap.raw");
                Files.createDirectories(file.getParent());
                Files.write(file, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return true;
    }

    private boolean onKokot5(CommandSender sender, Command command, String label, String[] args) {
        WorldEdit worldEdit = WorldEdit.getInstance();
        Region region = null;
        try {
            region = worldEdit.getSession(sender.getName()).getSelection(worldEdit.getSession(sender.getName()).getSelectionWorld());
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }
        if (region == null) {
            return false;
        }

        Range range = new Range(region.getMinimumPoint().getBlockX(),
                region.getMinimumPoint().getBlockZ(),
                region.getMaximumPoint().getBlockX(),
                region.getMaximumPoint().getBlockZ());
        World starvingTiger = Bukkit.getWorld(region.getWorld().getName());

        SingleTask treeTransform = SingleTask.create("Odkonárovizovavatorovač")
                .onRegion(starvingTiger, range)
                .when(rule().andType(Material.REDSTONE_BLOCK))
                .then((block) -> {
                    block.setType(Material.RAILS);
                    block.setData((byte) 0);
                    int height = 0;
                    org.bukkit.block.Block current = block.getRelative(BlockFace.UP);
                    while (current.getType() == Material.LOG) {
                        height++;
                        current = current.getRelative(BlockFace.UP);
                    }
                    if(height > 3) {
                        height += 3;
                    }
                    for (int i = 1; i + 2 < height; i += 3) {
                        block.getRelative(0, i, 0).setType(Material.BARRIER);
                        block.getRelative(0, i + 1, 0).setType(Material.CAULDRON);
                        block.getRelative(0, i + 2, 0).setType(Material.BARRIER);
                    }
                });

        SingleTask treeTransform2 = SingleTask.create("Odkonárovizovavatorovač2")
                .onRegion(starvingTiger, range)
                .when(rule().andType(Material.EMERALD_BLOCK))
                .then((block) -> {
                    block.setType(Material.RAILS);
                    block.setData((byte) 1);
                    int height = 0;
                    org.bukkit.block.Block current = block.getRelative(BlockFace.UP);
                    while (current.getType() == Material.LOG) {
                        height++;
                        current = current.getRelative(BlockFace.UP);
                    }
                    if(height > 3) {
                        height += 3;
                    }
                    for (int i = 1; i + 2 < height; i += 3) {
                        block.getRelative(0, i, 0).setType(Material.BARRIER);
                        block.getRelative(0, i + 1, 0).setType(Material.CAULDRON);
                        block.getRelative(0, i + 1, 0).setData((byte) 1);
                        block.getRelative(0, i + 2, 0).setType(Material.BARRIER);
                    }
                });

        SingleTask treeTransform3 = SingleTask.create("Odkonárovizovavatorovač3")
                .onRegion(starvingTiger, range)
                .when(rule().andType(Material.DIAMOND_BLOCK))
                .then((block) -> {
                    block.setType(Material.RAILS);
                    block.setData((byte) 6);
                    int height = 0;
                    org.bukkit.block.Block current = block.getRelative(BlockFace.UP);
                    while (current.getType() == Material.LOG) {
                        height++;
                        current = current.getRelative(BlockFace.UP);
                    }
                    if(height > 3) {
                        height += 3;
                    }
                    for (int i = 1; i + 2 < height; i += 3) {
                        block.getRelative(0, i, 0).setType(Material.BARRIER);
                        block.getRelative(0, i + 1, 0).setType(Material.CAULDRON);
                        block.getRelative(0, i + 1, 0).setData((byte) 2);
                        block.getRelative(0, i + 2, 0).setType(Material.BARRIER);
                    }
                });

        SingleTask treeTransform4 = SingleTask.create("Odkonárovizovavatorovač4")
                .onRegion(starvingTiger, range)
                .when(rule().andType(Material.IRON_BLOCK))
                .then((block) -> {
                    block.setType(Material.RAILS);
                    block.setData((byte) 7);
                    int height = 0;
                    org.bukkit.block.Block current = block.getRelative(BlockFace.UP);
                    while (current.getType() == Material.LOG) {
                        height++;
                        current = current.getRelative(BlockFace.UP);
                    }
                    if(height > 3) {
                        height += 3;
                    }
                    for (int i = 1; i + 2 < height; i += 3) {
                        block.getRelative(0, i, 0).setType(Material.BARRIER);
                        block.getRelative(0, i + 1, 0).setType(Material.CAULDRON);
                        block.getRelative(0, i + 1, 0).setData((byte) 3);
                        block.getRelative(0, i + 2, 0).setType(Material.BARRIER);
                    }
                });

        SingleTask treeTransform5 = SingleTask.create("Odkonárovizovavatorovač5")
                .onRegion(starvingTiger, range)
                .when(rule().andType(Material.GOLD_BLOCK))
                .then((block) -> {
                    block.setType(Material.RAILS);
                    block.setData((byte) 8);
                    int height = 0;
                    org.bukkit.block.Block current = block.getRelative(BlockFace.UP);
                    while (current.getType() == Material.LOG_2) {
                        height++;
                        current = current.getRelative(BlockFace.UP);
                    }
                    if(height > 3) {
                        height += 3;
                    }
                    for (int i = 1; i + 2 < height; i += 3) {
                        block.getRelative(0, i, 0).setType(Material.BARRIER);
                        block.getRelative(0, i + 1, 0).setType(Material.PISTON_BASE);
                        block.getRelative(0, i + 1, 0).setData((byte) 0);
                        block.getRelative(0, i + 2, 0).setType(Material.BARRIER);
                    }
                });

        SingleTask treeTransform6 = SingleTask.create("Odkonárovizovavatorovač6")
                .onRegion(starvingTiger, range)
                .when(rule().andType(Material.LAPIS_BLOCK))
                .then((block) -> {
                    block.setType(Material.RAILS);
                    block.setData((byte) 9);
                    int height = 0;
                    org.bukkit.block.Block current = block.getRelative(BlockFace.UP);
                    while (current.getType() == Material.LOG_2) {
                        height++;
                        current = current.getRelative(BlockFace.UP);
                    }
                    if(height > 3) {
                        height += 3;
                    }
                    for (int i = 1; i + 2 < height; i += 3) {
                        block.getRelative(0, i, 0).setType(Material.BARRIER);
                        block.getRelative(0, i + 1, 0).setType(Material.PISTON_BASE);
                        block.getRelative(0, i + 1, 0).setData((byte) 1);
                        block.getRelative(0, i + 2, 0).setType(Material.BARRIER);
                    }
                });


        short[] leavesScore = new short[64]; // max. 8 different data values
        byte baseSize = 2;
        final byte[] size = {2}; // (2*size)^3 blocks will be checked
        SingleTask treeTransform100 = SingleTask.create("Lisťomenič")
                .onRegion(starvingTiger, range)
                .when(rule().type(Material.LOG, Material.LOG_2))
                .then((block) -> {
                    size[0] = baseSize;
                    Arrays.fill(leavesScore, (short) 0);

                    org.bukkit.block.Block current;

                    boolean empty = true;
                    while(empty) {
                        // Build favorites table.
                        for (int x = -size[0]; x < size[0]; x++) {
                            for (int y = -size[0]; y < size[0]; y++) {
                                for (int z = -size[0]; z < size[0]; z++) {
                                    current = block.getRelative(x, y, z);
                                    if (current.getType() == Material.LEAVES) {
                                        leavesScore[current.getData() % 4]++;
                                    } else if (current.getType() == Material.LEAVES_2) {
                                        leavesScore[32 + (current.getData() % 4)]++;
                                    }
                                }
                            }
                        }

                        // Check if table is empty.
                        for (int i = 0; i < leavesScore.length; i++) {
                            if (leavesScore[i] > 0) {
                                empty = false;
                                break;
                            }
                        }

                        if(empty) {
                            if(size[0] < 12) {
                                // Increase kernel size.
                                size[0]++;
                            } else {
                                Bukkit.broadcastMessage("Can't find score table at " + block.getLocation().toVector().toString());
                                empty = true;
                                break;
                            }
                        }
                    }

                    // Replace only if table not empty.
                    if(!empty) {
                        // Find favorite.
                        int favIndex = -1;
                        int favMax = -1;
                        for (int i = 0; i < leavesScore.length; i++) {
                            if (leavesScore[i] > 0) {
                                //Bukkit.broadcastMessage(i + " -> " + leavesScore[i]);
                            }

                            if (leavesScore[i] > favMax) {
                                favIndex = i;
                                favMax = leavesScore[i];
                            }
                        }

                        if (favIndex >= 32) {
                            block.setType(Material.LEAVES_2);
                            //Bukkit.broadcastMessage("!!! -> " + (byte) ((favIndex - 32) + 4));
                            block.setData((byte) ((favIndex - 32) + 4));
                        } else {
                            block.setType(Material.LEAVES);
                            block.setData((byte) (favIndex + 4));
                        }
                    }
                });

        final long[] counter = {0};
        SingleTask invisible = SingleTask.create("Remove invisible leaves")
                .onRegion(starvingTiger, range)
                .when(block -> block.getType() == Material.LEAVES || block.getType() == Material.LEAVES_2)
                .then(block -> {
                    if (Block.isLeaves(block.getRelative(BlockFace.UP)) &&
                            Block.isLeaves(block.getRelative(BlockFace.DOWN)) &&
                            Block.isLeaves(block.getRelative(BlockFace.NORTH)) &&
                            Block.isLeaves(block.getRelative(BlockFace.EAST)) &&
                            Block.isLeaves(block.getRelative(BlockFace.SOUTH)) &&
                            Block.isLeaves(block.getRelative(BlockFace.WEST))) {
                        block.setType(Material.AIR);
                    }
                })
                .finish(() -> sender.sendMessage(ChatColor.BLUE + "" + (counter[0]++)));
        //current.runAtSpeed(16 * 16 * 256 * 1);

        treeTransform.runAtSpeed(16 * 16 * 256 * 2);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> treeTransform2.runAtSpeed(16 * 16 * 256 * 2), 20 * 2);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> treeTransform3.runAtSpeed(16 * 16 * 256 * 2), 20 * 4);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> treeTransform4.runAtSpeed(16 * 16 * 256 * 2), 20 * 6);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> treeTransform5.runAtSpeed(16 * 16 * 256 * 2), 20 * 8);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> treeTransform6.runAtSpeed(16 * 16 * 256 * 2), 20 * 10);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> treeTransform100.runAtSpeed(16 * 16 * 256 * 2), 20 * 12);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> invisible.runAtSpeed(16 * 16 * 256 * 2), 20 * 14);

        return true;
    }

    private boolean onKokot3(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.broadcastMessage("a");
        Range range = new Range(950, 6900, 1350, 8000);
        World starvingTiger = Bukkit.getWorld("starvingMacka");

        SingleTask treeTransform = SingleTask.create("Odkonárovizovavatorovač")
                .onRegion(starvingTiger, range)
                .when(rule().andType(Material.REDSTONE_BLOCK))
                .then((block) -> {
                    block.setType(Material.RAILS);
                    block.setData((byte) 0);
                    int height = 0;
                    org.bukkit.block.Block current = block.getRelative(BlockFace.UP);
                    while (current.getType() == Material.LOG) {
                        height++;
                        current = current.getRelative(BlockFace.UP);
                    }
                    for (int i = 1; i + 2 < height; i += 3) {
                        block.getRelative(0, i, 0).setType(Material.BARRIER);
                        block.getRelative(0, i + 1, 0).setType(Material.CAULDRON);
                        block.getRelative(0, i + 2, 0).setType(Material.BARRIER);
                    }
                });

        SingleTask treeTransform2 = SingleTask.create("Odkonárovizovavatorovač2")
                .onRegion(starvingTiger, range)
                .when(rule().andType(Material.EMERALD_BLOCK))
                .then((block) -> {
                    block.setType(Material.RAILS);
                    block.setData((byte) 1);
                    int height = 0;
                    org.bukkit.block.Block current = block.getRelative(BlockFace.UP);
                    while (current.getType() == Material.LOG) {
                        height++;
                        current = current.getRelative(BlockFace.UP);
                    }
                    for (int i = 1; i + 2 < height; i += 3) {
                        block.getRelative(0, i, 0).setType(Material.BARRIER);
                        block.getRelative(0, i + 1, 0).setType(Material.CAULDRON);
                        block.getRelative(0, i + 1, 0).setData((byte) 1);
                        block.getRelative(0, i + 2, 0).setType(Material.BARRIER);
                    }
                });

        SingleTask treeTransform3 = SingleTask.create("Odkonárovizovavatorovač3")
                .onRegion(starvingTiger, range)
                .when(rule().andType(Material.DIAMOND_BLOCK))
                .then((block) -> {
                    block.setType(Material.RAILS);
                    block.setData((byte) 6);
                    int height = 0;
                    org.bukkit.block.Block current = block.getRelative(BlockFace.UP);
                    while (current.getType() == Material.LOG) {
                        height++;
                        current = current.getRelative(BlockFace.UP);
                    }
                    for (int i = 1; i + 2 < height; i += 3) {
                        block.getRelative(0, i, 0).setType(Material.BARRIER);
                        block.getRelative(0, i + 1, 0).setType(Material.CAULDRON);
                        block.getRelative(0, i + 1, 0).setData((byte) 2);
                        block.getRelative(0, i + 2, 0).setType(Material.BARRIER);
                    }
                });

        SingleTask treeTransform4 = SingleTask.create("Odkonárovizovavatorovač4")
                .onRegion(starvingTiger, range)
                .when(rule().andType(Material.IRON_BLOCK))
                .then((block) -> {
                    block.setType(Material.RAILS);
                    block.setData((byte) 7);
                    int height = 0;
                    org.bukkit.block.Block current = block.getRelative(BlockFace.UP);
                    while (current.getType() == Material.LOG) {
                        height++;
                        current = current.getRelative(BlockFace.UP);
                    }
                    for (int i = 1; i + 2 < height; i += 3) {
                        block.getRelative(0, i, 0).setType(Material.BARRIER);
                        block.getRelative(0, i + 1, 0).setType(Material.CAULDRON);
                        block.getRelative(0, i + 1, 0).setData((byte) 3);
                        block.getRelative(0, i + 2, 0).setType(Material.BARRIER);
                    }
                });

        SingleTask treeTransform5 = SingleTask.create("Odkonárovizovavatorovač5")
                .onRegion(starvingTiger, range)
                .when(rule().andType(Material.GOLD_BLOCK))
                .then((block) -> {
                    block.setType(Material.RAILS);
                    block.setData((byte) 8);
                    int height = 0;
                    org.bukkit.block.Block current = block.getRelative(BlockFace.UP);
                    while (current.getType() == Material.LOG_2) {
                        height++;
                        current = current.getRelative(BlockFace.UP);
                    }
                    for (int i = 1; i + 2 < height; i += 3) {
                        block.getRelative(0, i, 0).setType(Material.BARRIER);
                        block.getRelative(0, i + 1, 0).setType(Material.PISTON_BASE);
                        block.getRelative(0, i + 1, 0).setData((byte) 0);
                        block.getRelative(0, i + 2, 0).setType(Material.BARRIER);
                    }
                });

        SingleTask treeTransform6 = SingleTask.create("Odkonárovizovavatorovač6")
                .onRegion(starvingTiger, range)
                .when(rule().andType(Material.LAPIS_BLOCK))
                .then((block) -> {
                    block.setType(Material.RAILS);
                    block.setData((byte) 9);
                    int height = 0;
                    org.bukkit.block.Block current = block.getRelative(BlockFace.UP);
                    while (current.getType() == Material.LOG_2) {
                        height++;
                        current = current.getRelative(BlockFace.UP);
                    }
                    for (int i = 1; i + 2 < height; i += 3) {
                        block.getRelative(0, i, 0).setType(Material.BARRIER);
                        block.getRelative(0, i + 1, 0).setType(Material.PISTON_BASE);
                        block.getRelative(0, i + 1, 0).setData((byte) 1);
                        block.getRelative(0, i + 2, 0).setType(Material.BARRIER);
                    }
                });

        short[] leavesScore = new short[64]; // max. 8 different data values
        byte size = 3; // (2*size)^3 blocks will be checked
        SingleTask treeTransform100 = SingleTask.create("Lisťomenič")
                .onRegion(starvingTiger, range)
                .when(rule().type(Material.LOG, Material.LOG_2))
                .then((block) -> {
                    org.bukkit.block.Block current;
                    // Build favorites table.
                    for (int x = -size; x < size; x++) {
                        for (int y = -size; y < size; y++) {
                            for (int z = -size; z < size; z++) {
                                current = block.getRelative(x, y, z);
                                if (current.getType() == Material.LEAVES) {
                                    leavesScore[current.getData()]++;
                                } else if (current.getType() == Material.LEAVES_2) {
                                    leavesScore[32 + current.getData()]++;
                                }
                            }
                        }
                    }

                    // Find favorite.
                    int favIndex = -1;
                    int favMax = -1;
                    for (int i = 0; i < leavesScore.length; i++) {
                        if (leavesScore[i] > favMax) {
                            favIndex = i;
                            favMax = leavesScore[i];
                        }
                    }

                    if (leavesScore[favIndex] >= 32) {
                        block.setType(Material.LEAVES_2);
                        block.setData((byte) (leavesScore[favIndex] - 32));
                    } else {
                        block.setType(Material.LEAVES);
                        block.setData((byte) leavesScore[favIndex]);
                    }
                });

        Bukkit.broadcastMessage("about to start async.");
        treeTransform.runAtSpeed(16 * 16 * 256);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> treeTransform2.runAtSpeed(16 * 16 * 256), 20 * 2);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> treeTransform3.runAtSpeed(16 * 16 * 256), 20 * 4);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> treeTransform4.runAtSpeed(16 * 16 * 256), 20 * 6);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> treeTransform5.runAtSpeed(16 * 16 * 256), 20 * 8);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> treeTransform6.runAtSpeed(16 * 16 * 256), 20 * 10);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> treeTransform100.runAtSpeed(16 * 16 * 256), 20 * 12);
        Bukkit.broadcastMessage("everyting seems to be scheduled!");

        return true;
    }

    private boolean onKokot2(CommandSender sender, Command command, String label, String[] args) {
        WorldEdit worldEdit = WorldEdit.getInstance();
        Region region = null;
        try {
            region = worldEdit.getSession(sender.getName()).getSelection(worldEdit.getSession(sender.getName()).getSelectionWorld());
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }
        if (region == null) {
            return false;
        }

        Range range = new Range(region.getMinimumPoint().getBlockX(),
                region.getMinimumPoint().getBlockZ(),
                region.getMaximumPoint().getBlockX(),
                region.getMaximumPoint().getBlockZ());
        World starvingTiger = Bukkit.getWorld(region.getWorld().getName());

        SingleTask basicGrass = SingleTask.create("Basic Grass")
                .onRegion(starvingTiger, range)
                .when(rule()
                        .andTypeDown(Material.GRASS, Material.DIRT)
                        .andTypeUp(Material.AIR)
                        .andNotSolid()
                        .andChance(60)
                        .andHighLinearCutoff(80, 160))
                .then(action()
                        .andSetType(Material.LONG_GRASS)
                        .andSetData(1));

        SingleTask tallGrass = SingleTask.create("Tall Grass")
                .onRegion(starvingTiger, range)
                .when(rule()
                        .andType(Material.LONG_GRASS)
                        .andData(1)
                        .andCustom(b -> Math.random() > (b.getBiome() == Biome.PLAINS ? 0.55 : 0.68)))
                .then(action().setData(2));

        SingleTask doubleTallGrass = SingleTask.create("Double Tall Grass")
                .onRegion(starvingTiger, range)
                .when(rule()
                        .andTypeDown(Material.GRASS, Material.DIRT)
                        .andTypeUp(Material.AIR)
                        .andNotSolid()
                        .andCustom(b -> Math.random() > (b.getBiome() == Biome.PLAINS ? 0.975 : (b.getBiome() == Biome.TAIGA ? 0.996 : 0.985)))
                        .andHighLinearCutoff(80, 130))
                .then(b -> {
                    b.setType(Material.DOUBLE_PLANT);
                    if (Math.random() > 0.4) {
                        b.setData((byte) 2);
                    } else {
                        b.setData((byte) 5);
                    }
                    b.getRelative(BlockFace.UP).setType(Material.DOUBLE_PLANT);
                    b.getRelative(BlockFace.UP).setData((byte) 8);
                });

        SingleTask pinkTulip = SingleTask.create("Pink Tulip Seeder")
                .onRegion(starvingTiger, range)
                .when(rule()
                        .yAbove(100)
                        .andTypeDown(Material.GRASS, Material.DIRT)
                        .andTypeUp(Material.AIR)
                        .andNotSolid()
                        .andChance(10)
                        .andLowLinearCutoff(100, 180))
                .then(action()
                        .setType(Material.RED_ROSE)
                        .andSetData(7));

        SingleTask walkingFern = SingleTask.create("Walking Fern")
                .onRegion(starvingTiger, range)
                .when(rule()
                        .typeDown(Material.GRASS, Material.DIRT)
                        .andTypeUp(Material.AIR)
                        .andNotSolid()
                        .andCustom(b -> b.getLightFromSky() < 14)
                        .andCustom(b -> Math.random() > b.getLightFromSky() / 14)
                        .andChance(20))
                .then(action()
                        .setType(Material.RED_ROSE)
                        .andSetData((byte) 2));

        SingleTask mushrooms = SingleTask.create("Mushrooms")
                .onRegion(starvingTiger, range)
                .when(rule()
                        .typeDown(Material.GRASS, Material.DIRT)
                        .andTypeUp(Material.AIR)
                        .andNotSolid()
                        .andCustom(b -> b.getLightFromSky() < 12)
                        .andCustom(b -> Math.random() > b.getLightFromSky() / 12)
                        .andChance(3))
                .then(b -> {
                    if (Math.random() > 0.5) {
                        b.setType(Material.RED_MUSHROOM);
                    } else {
                        b.setType(Material.BROWN_MUSHROOM);
                    }
                });

        SingleTask dandelion = SingleTask.create("Dandelion")
                .onRegion(starvingTiger, range)
                .when(rule()
                        .typeDown(Material.GRASS)
                        .andType(Material.LONG_GRASS)
                        .andCustom(b -> b.getLightFromSky() > 10)
                        .andCustom(b -> Math.random() > (b.getBiome() == Biome.PLAINS ? 0.85 : 0.92)))
                .then(action().setType(Material.YELLOW_FLOWER));

        basicGrass.runAtSpeed(16 * 16 * 256);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> tallGrass.runAtSpeed(16 * 16 * 256), 20 * 2);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> doubleTallGrass.runAtSpeed(16 * 16 * 256), 20 * 4);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> pinkTulip.runAtSpeed(16 * 16 * 256), 20 * 6);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> walkingFern.runAtSpeed(16 * 16 * 256), 20 * 8);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> mushrooms.runAtSpeed(16 * 16 * 256), 20 * 10);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> dandelion.runAtSpeed(16 * 16 * 256), 20 * 12);

        return true;
    }

    private boolean onKokot1(CommandSender sender, Command command, String label, String[] args) {
        Range range = new Range(-5028, -790, -4774, -515);
        World world = Bukkit.getWorld("Starving");
        final long[] counter = {0};

        SingleTask current = SingleTask.create("Remove invisible leaves")
                .onRegion(world, range)
                .when(block -> block.getType() == Material.LEAVES || block.getType() == Material.LEAVES_2)
                .then(block -> {
                    if (Block.isLeaves(block.getRelative(BlockFace.UP)) &&
                            Block.isLeaves(block.getRelative(BlockFace.DOWN)) &&
                            Block.isLeaves(block.getRelative(BlockFace.NORTH)) &&
                            Block.isLeaves(block.getRelative(BlockFace.EAST)) &&
                            Block.isLeaves(block.getRelative(BlockFace.SOUTH)) &&
                            Block.isLeaves(block.getRelative(BlockFace.WEST))) {
                        block.setType(Material.AIR);
                        sender.sendMessage(ChatColor.BLUE + "" + (counter[0]++));
                    }
                });
        current.runAtSpeed(16 * 16 * 256 * 1);
        return true;
    }

    public boolean onKokot(CommandSender sender, Command command, String label, String[] args) {
        Range range = new Range(-5500, -4000, 5500, 4000);
        World world = Bukkit.getWorld("Starving");

        SingleTask current = SingleTask.create("Fix Tree Base")
                .onRegion(world, range)
                .when(block -> block.getType() == Material.WOOL && GroovyBlockTask.inRange(0, 5, block.getData()))
                .then(block -> {
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
                });

        current.runAtSpeed(16 * 16 * 256 * 8);
        return true;
    }
}
