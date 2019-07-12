/**
 * Starving - Bukkit API server mod with Zombies.
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
package eu.matejkormuth.worldprocessor.callbacks;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents list of callbacks.
 *
 * @param <T> type of callback argument
 */
public class CallbackList<T> {

    /**
     * Internal list of callbacks.
     */
    private final List<Callback<T>> callbacks = new ArrayList<>();

    /**
     * Adds new callback.
     *
     * @param callback new callback
     */
    public void add(Callback<T> callback) {
        callbacks.add(callback);
    }

    /**
     * Removes existing callback.
     *
     * @param callback callback to remove
     */
    public void remove(Callback<T> callback) {
        callbacks.remove(callback);
    }

    /**
     * Calls all registered callbacks with specified optional argument(s).
     *
     * @param argument argument(s)
     */
    public void call(T argument) {
        for (Callback<T> callback : callbacks) {
            callback.call(argument);
        }
    }
}
