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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;

public class BlockTaskSyncExecutor {

    private final SingleTask task;

    private long currentX;
    private long currentZ;

    private long maxX;
    private long maxZ;

    private long blocksPerTick = 256L * 32L;
    private long totalBlocks = 0;
    private long blocksProcessed = 0;
    private long testsPassed = 0;
    private long ticks = 0;

    private int taskId = -1;
    private TestStrategies.TestStrategy testStrategy;
    private Runnable ondone;

    public BlockTaskSyncExecutor(SingleTask task) {
        this.task = task;
        this.testStrategy = TestStrategies.createStrategy(task);
    }

    public boolean isRunning() {
        return taskId != -1;
    }

    public void beginRun() {
        if (this.taskId != -1) {
            throw new IllegalStateException("Already running!");
        }

        this.currentX = task.getRegion().getMinX();
        this.currentZ = task.getRegion().getMinZ();

        this.maxX = task.getRegion().getMaxX();
        this.maxZ = task.getRegion().getMaxZ();

        this.totalBlocks = task.getRegion().getBlockCount();

        if ((blocksPerTick % 256L) != 0L) {
            throw new IllegalArgumentException("maxTickBlocks must be multiple of 256");
        }

        // Schedule task.
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                Bukkit.getPluginManager().getPlugin("a"), this::timerTick, 20L, 1L);

        Bukkit.broadcastMessage(ChatColor.GRAY + "Starting in one second!");
    }

    public void cancelRun() {
        if (this.taskId == -1) {
            throw new IllegalStateException("Already not running!");
        }

        Bukkit.getScheduler().cancelTask(this.taskId);
        this.taskId = -1;

        Bukkit.broadcastMessage(ChatColor.GRAY + "Interrupted!");
    }

    private void timerTick() {
        long processed = this.doRun();
        if (processed == -1) {
            Bukkit.getScheduler().cancelTask(this.taskId);
            this.taskId = -1;

            Bukkit.broadcastMessage(ChatColor.GRAY + "Finished!");
            if (this.ondone != null) {
                this.ondone.run();
            }
        } else {
            this.blocksProcessed += processed;
        }

        ticks++;
        if ((ticks % (20L * 5)) == 0) {
            long blocksLeft = totalBlocks - blocksProcessed;
            long seconds = blocksLeft / (blocksPerTick * 20);

            Bukkit.broadcastMessage(ChatColor.WHITE + task.getName() + ": " + ChatColor.RED + flong(blocksProcessed) +
                    ChatColor.WHITE + "/" + ChatColor.BLUE + flong(testsPassed) + ChatColor.WHITE + "/" +
                    ChatColor.GREEN + flong(totalBlocks) + ChatColor.WHITE + " +" + ChatColor.YELLOW + formatTime(seconds) + " left");
        }
    }

    private static final DecimalFormat df = new DecimalFormat("#.##");
    private static final DecimalFormat df2 = new DecimalFormat("#.###");

    private String flong(long num) {
        if (num > 1_000_000L) {
            return df2.format(num / 1_000_000D) + "M";
        } else if (num > 1000) {
            return df.format(num / 1000D) + "K";
        } else {
            return num + "";
        }
    }

    private String formatTime(long seconds) {
        long mins = seconds / 60L;
        long secs = seconds % 60L;
        if (mins >= 10) {
            if (secs >= 10) {
                return mins + ":" + secs;
            } else {
                return mins + ":0" + secs;
            }
        } else {
            if (secs >= 10) {
                return "0" + mins + ":" + secs;
            } else {
                return "0" + mins + ":0" + secs;
            }
        }
    }

    private long doRun() {
        long tickBlocks = 0;

        while (currentX <= maxX) {
            while (currentZ <= maxZ) {
                for (int y = 0; y < 256; y++) {

                    // Check for max tick blocks.
                    if (tickBlocks >= blocksPerTick) {
                        return tickBlocks;
                    }

                    // Execute.
                    //block = task.getWorld().getBlockAt((int) currentX, y, (int) currentZ);
                    //if (task.getTest().test(block)) {
                    //    testsPassed++;
                    //    task.getConsumer().accept(block);
                    //}
                    if (testStrategy.test((int) currentX, y, (int) currentZ)) {
                        testsPassed++;
                        testStrategy.accept();
                    }
                    tickBlocks++;
                }
                currentZ++;
            }
            currentZ = task.getRegion().getMinZ();
            currentX++;
        }
        return -1;
    }

    public long getBlocksPerTick() {
        return blocksPerTick;
    }

    public long getBlocksProcessed() {
        return blocksProcessed;
    }

    public long getCurrentX() {
        return currentX;
    }

    public long getCurrentZ() {
        return currentZ;
    }

    public void setBlocksPerTick(int blocksPerTick) {
        this.blocksPerTick = blocksPerTick;

        if ((blocksPerTick % 256) != 0) {
            throw new IllegalArgumentException("maxTickBlocks must be multiple of 256");
        }
    }

    public void onFinish(Runnable runnable) {
        this.ondone = runnable;
    }
}
