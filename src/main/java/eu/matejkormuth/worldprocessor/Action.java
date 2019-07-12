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

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.function.Consumer;

public class Action implements Consumer<org.bukkit.block.Block> {
    private Consumer<org.bukkit.block.Block> action;
    private Action and = null;

    private Action() {
    }

    public static Action action() {
        return new Action();
    }

    public Action setType(Material material) {
        action = (b -> b.setType(material));
        return this;
    }

    public Action andSetType(Material material) {
        return and().setType(material);
    }

    public Action setData(int data) {
        action = (b -> b.setData((byte) data));
        return this;
    }

    public Action andSetData(int data) {
        return and().setData(data);
    }

    public Action and() {
        if (this.and != null) {
            return this.and = new Action();
        } else {
            return this;
        }
    }

    @Override
    public void accept(Block block) {
        if (this.action != null) {
            action.accept(block);
        }

        if (and != null) {
            and.action.accept(block);
        }
    }
}
