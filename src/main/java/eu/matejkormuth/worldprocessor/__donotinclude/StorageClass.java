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
package eu.matejkormuth.worldprocessor.__donotinclude;

import eu.matejkormuth.worldprocessor.SingleTask;
import eu.matejkormuth.worldprocessor.Range;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class StorageClass {

    private World world;
    private Predicate<Block> test;
    private Consumer<Block> whenPass;

    public void neverInvokeThis() {
        Pattern mcaFile = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca");

        File regionFolder = new File(world.getWorldFolder().getAbsolutePath() + "/region/");
        List<ChunkPos> possiblyGenerated = new ArrayList<>();


        // Get all chunks from region.
        int regionX = 0;
        int regionZ = 0;

        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                possiblyGenerated.add(new ChunkPos(regionX * i, regionZ * j));
            }
        }

        List<Chunk> chunks = null;

        Block block;
        for (Chunk chunk : chunks) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 256; y++) {
                    for (int z = 0; z < 16; z++) {
                        block = chunk.getBlock(x, y, z);
                        if (test.test(block)) {
                            whenPass.accept(block);
                        }
                    }
                }
            }
        }

    }

    public void tests() {
        Range range = new Range(-128, -128, 128, 128);
        World world = Bukkit.getWorld("world");

        SingleTask.create("Fix Tree Base")
                .onRegion(world, range)
                .when(block -> eu.matejkormuth.worldprocessor.legacy.Block.isLog(block) && block.getRelative(BlockFace.DOWN).getType() == Material.DIRT)
                .then(block -> {
                    block.getRelative(BlockFace.EAST).setType(Material.AIR);
                    block.getRelative(BlockFace.WEST).setType(Material.AIR);
                    block.getRelative(BlockFace.NORTH).setType(Material.AIR);
                    block.getRelative(BlockFace.SOUTH).setType(Material.AIR);

                    block.getRelative(BlockFace.NORTH_EAST).setType(Material.AIR);
                    block.getRelative(BlockFace.SOUTH_EAST).setType(Material.AIR);
                    block.getRelative(BlockFace.NORTH_WEST).setType(Material.AIR);
                    block.getRelative(BlockFace.SOUTH_WEST).setType(Material.AIR);

                    block.getRelative(0, 24, 0).setType(Material.DIAMOND_BLOCK);
                });

        SingleTask replaceGrass = SingleTask.create("Replace Grass")
                .onRegion(world, range)
                .when(block -> block.getType() == Material.GRASS)
                .then(block -> block.setType(Material.WOOL));

        SingleTask replaceWater = SingleTask.create("Replace Water")
                .onRegion(world, range)
                .when(eu.matejkormuth.worldprocessor.legacy.Block::isWater)
                .then(block -> block.setType(Material.GLASS));

        SingleTask replaceLeaves = SingleTask.create("Replace Leaves")
                .onRegion(world, range)
                .when(eu.matejkormuth.worldprocessor.legacy.Block::isLeaves)
                .then(block -> block.setType(Material.BRICK));

        SingleTask randomLava = SingleTask.create("Random Lava")
                .onRegion(world, range)
                .when(block -> block.getType() == Material.SAND && Math.random() > 0.98)
                .then(block -> block.setType(Material.LAVA));

        SingleTask.create("Find Under Leaves")
                .onRegion(world, range)
                .when(block -> {
                    if (block.getType() != Material.GRASS) {
                        return false;
                    }

                    for (int i = 0; block.getY() + i < 256; i++) {
                        org.bukkit.block.Block a = block.getRelative(0, i, 0);

                        if (eu.matejkormuth.worldprocessor.legacy.Block.isLeaves(a)) {
                            return true;
                        }
                    }
                    return false;
                })
                .then(block -> {
                    // Set to red sandstone.
                    block.setType(Material.RED_SANDSTONE);

                    // Randomly place redstone blocks.
                    if (Math.random() > 0.94) {
                        block.getRelative(BlockFace.UP).setType(Material.REDSTONE_BLOCK);
                    }
                });

        SingleTask findUnderLeaves = SingleTask.create("Find Under Leaves")
                .onRegion(world, range)
                .when(block -> {
                    if (block.getType() != Material.WOOL) {
                        return false;
                    }

                    for (int i = 0; block.getY() + i < 196; i++) {
                        org.bukkit.block.Block a = block.getRelative(0, i, 0);

                        if (a.getType() == Material.BRICK) {

                            if (Math.random() > 0.97) {
                                block.getRelative(BlockFace.UP).setType(Material.REDSTONE_BLOCK);
                            }

                            return true;
                        }
                    }
                    return false;
                })
                .then(block -> block.setType(Material.RED_SANDSTONE));

        //Sequence
        //        .first(replaceGrass)
        //        .then(replaceWater)
        //        .then(replaceLeaves)
        //        .then(randomLava)
        //        .then(findUnderLeaves)
        //        .runInOrder();
    }

}
