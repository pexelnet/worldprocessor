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
import org.bukkit.block.*;
import org.bukkit.block.Block;

import java.util.function.Predicate;

public class Rule implements Predicate<org.bukkit.block.Block> {

    private Predicate<org.bukkit.block.Block> current;
    private Rule and = null;

    private Rule() {
    }

    private Rule(Predicate<org.bukkit.block.Block> rule) {
        this.current = rule;
    }

    public static Rule rule() {
        return new Rule();
    }

    public static Rule any() {
        return new Rule() {
            @Override
            public boolean test(Block block) {
                return true;
            }
        };
    }

    // from yAll - 100% (y= 100)
    // to yNone - 0% (y= 200)
    public Rule highLinearCutoff(int yAll, int yNone) {
        if (yAll > yNone) {
            throw new RuntimeException("fuck -> yAll > yNone");
        }

        long length = yNone - yAll;
        this.current = b -> Math.random() * length > b.getY() - yAll;
        return this;
    }

    // from yNone - 0% (y= 100)
    // to yAll - 100% (y= 200)
    public Rule lowLinearCutoff(int yNone, int yAll) {
        if (yAll < yNone) {
            throw new RuntimeException("fuck -> yAll < yNone");
        }

        long length = yAll - yNone;
        this.current = b -> Math.random() * length < yAll - (b.getY() - yAll);
        return this;
    }

    public Rule andHighLinearCutoff(int yAll, int yNone) {
        return and().highLinearCutoff(yAll, yNone);
    }

    public Rule andLowLinearCutoff(int yNone, int yAll) {
        return and().lowLinearCutoff(yNone, yAll);
    }

    public Rule solid() {
        this.current = (b -> b.getType().isSolid());
        return this;
    }

    public Rule andSolid() {
        return and().solid();
    }

    public Rule notSolid() {
        this.current = (b -> !b.getType().isSolid());
        return this;
    }

    public Rule andNotSolid() {
        return and().notSolid();
    }

    public Rule custom(Predicate<org.bukkit.block.Block> b) {
        this.current = b;
        return this;
    }

    public Rule andCustom(Predicate<org.bukkit.block.Block> b) {
        return and().custom(b);
    }

    public Rule chance(int percent) {
        this.current = (b -> Math.random() * 100 <= percent);
        return this;
    }

    public Rule andChance(int percent) {
        return and().chance(percent);
    }

    public Rule chance(float f) {
        this.current = (b -> Math.random() > f);
        return this;
    }

    public Rule andChance(float f) {
        return and().chance(f);
    }

    public Rule data(int data) {
        this.current = (b -> b.getData() == data);
        return this;
    }

    public Rule andData(int data) {
        return and().data(data);
    }

    public Rule dataUp(int data) {
        this.current = (b -> b.getRelative(BlockFace.UP).getData() == data);
        return this;
    }

    public Rule andDataUp(int data) {
        return and().dataUp(data);
    }

    public Rule dataDown(int data) {
        this.current = (b -> b.getRelative(BlockFace.DOWN).getData() == data);
        return this;
    }

    public Rule andDataDown(int data) {
        return and().dataDown(data);
    }

    public Rule type(Material material) {
        this.current = (b -> b.getType() == material);
        return this;
    }

    public Rule andType(Material material) {
        return and().type(material);
    }

    public Rule type(Material... more) {
        this.current = (b -> {
            for (int i = 0; i < more.length; i++) {
                if (more[i] == b.getType()) {
                    return true;
                }
            }
            return false;
        });
        return this;
    }

    public Rule andType(Material... more) {
        return and().type(more);
    }

    public Rule typeDown(Material material) {
        this.current = (b -> b.getRelative(BlockFace.DOWN).getType() == material);
        return this;
    }

    public Rule andTypeDown(Material material) {
        return and().typeDown(material);
    }

    public Rule typeDown(Material... more) {
        this.current = (b -> {
            b = b.getRelative(BlockFace.DOWN);
            for (int i = 0; i < more.length; i++) {
                if (more[i] == b.getType()) {
                    return true;
                }
            }
            return false;
        });
        return this;
    }

    public Rule andTypeDown(Material... more) {
        return and().typeDown(more);
    }

    public Rule typeUp(Material material) {
        this.current = (b -> b.getRelative(BlockFace.UP).getType() == material);
        return this;
    }

    public Rule andTypeUp(Material material) {
        return and().typeUp(material);
    }

    public Rule typeUp(Material... more) {
        this.current = (b -> {
            b = b.getRelative(BlockFace.UP);
            for (int i = 0; i < more.length; i++) {
                if (more[i] == b.getType()) {
                    return true;
                }
            }
            return false;
        });
        return this;
    }

    public Rule andTypeUp(Material... more) {
        return and().typeUp(more);
    }

    public Rule yAbove(int minY) {
        this.current = b -> b.getY() > minY;
        return this;
    }

    public Rule andYAbove(int minY) {
        return and().yAbove(minY);
    }

    public Rule and() {
        if (this.current != null) {
            return this.and = new Rule();
        } else {
            return this;
        }
    }

    @Override
    public boolean test(org.bukkit.block.Block block) {
        boolean result = false;
        if (current != null) {
            result |= current.test(block);
        }

        // Lazy resolve. false && true && true... never equals to true.
        if (!result) {
            return false;
        }

        // Recursive
        if (and != null) {
            result &= and.test(block);
        }
        return result;
    }
}
