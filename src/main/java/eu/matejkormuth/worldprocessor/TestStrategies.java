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

public class TestStrategies {

    private TestStrategies() {
    }

    public static TestStrategy createStrategy(SingleTask task) {
        if(task.getPostTest() != null) {
            return new BlockPosTestStrategy(task);
        } else {
            return new StandardTestStrategy(task);
        }
    }

    interface TestStrategy {
        boolean test(final int x, final int y, final int z);

        void accept();
    }

    public static class StandardTestStrategy implements TestStrategy {
        private final SingleTask task;
        private org.bukkit.block.Block lastBlock;

        public StandardTestStrategy(SingleTask task) {
            this.task = task;
        }

        @Override
        public boolean test(final int x, final int y, final int z) {
            lastBlock = task.getWorld().getBlockAt(x, y, z);
            return task.getTest().test(lastBlock);
        }

        @Override
        public void accept() {
            task.getConsumer().accept(lastBlock);
        }
    }

    public static class BlockPosTestStrategy implements TestStrategy {
        private final SingleTask task;
        private BlockPos lastBlockPos = new BlockPos(0,0,0);

        public BlockPosTestStrategy(SingleTask task) {
            this.task = task;
        }

        @Override
        public boolean test(final int x, final int y, final int z) {
            lastBlockPos.a(x,y,z);
            return task.getPostTest().test(lastBlockPos);
        }

        @Override
        public void accept() {
            task.getConsumer().accept(lastBlockPos.b(task.getWorld()));
        }
    }
}
