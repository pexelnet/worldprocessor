/**
 * worldprocessor - tasks for world
 * Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * <p>
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

import eu.matejkormuth.worldprocessor.blockdefs.BlockDef;
import eu.matejkormuth.worldprocessor.callbacks.Callback;
import eu.matejkormuth.worldprocessor.callbacks.CallbackList;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class SingleTask implements Task {

    private Predicate<Block> test;
    private Predicate<BlockPos> postTest;
    private Consumer<Block> then;

    private Predicate<BlockDef> predicate;
    private Consumer<BlockDef> consumer;

    private final CallbackList<Void> onFinish = new CallbackList<>();
    private final CallbackList<Void> onStart = new CallbackList<>();

    private World world;
    private Range region;

    // Default name.
    private final String name;

    SingleTask(String name) {
        this.name = name;
    }

    public static SingleTask create(String name) {
        return new SingleTask(name);
    }

    public SingleTask on(World world, Range range) {
        this.world = world;
        this.region = range;
        return this;
    }

    public SingleTask whenLegacy(Predicate<Block> test) {
        this.test = test;
        return this;
    }

    public SingleTask when(Predicate<BlockDef> test) {
        this.predicate = test;
        return this;
    }

    public SingleTask whenPos(Predicate<BlockPos> test) {
        this.postTest = test;
        return this;
    }

    public SingleTask then(Consumer<BlockDef> consumer) {
        this.consumer = consumer;
        return this;
    }

    public SingleTask thenLegacy(Consumer<Block> then) {
        this.then = then;
        return this;
    }

    public SingleTask onFinish(Callback<Void> callback) {
        onFinish.add(callback);
        return this;
    }

    public SingleTask onStart(Callback<Void> callback) {
        onStart.add(callback);
        return this;
    }

    public World getWorld() {
        return world;
    }

    public Predicate<Block> getTest() {
        return test;
    }

    public Predicate<BlockPos> getPostTest() {
        return postTest;
    }

    public Predicate<BlockDef> getPredicate() {
        return predicate;
    }

    public Consumer<Block> getThen() {
        return then;
    }

    public Consumer<BlockDef> getConsumer() {
        return consumer;
    }

    public Range getRegion() {
        return region;
    }

    public String getName() {
        return name;
    }

}
