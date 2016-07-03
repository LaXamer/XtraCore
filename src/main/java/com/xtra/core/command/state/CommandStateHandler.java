/**
 * This file is part of XtraCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 - 2016 XtraStudio <https://github.com/XtraStudio>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xtra.core.command.state;

import java.util.Optional;

import com.xtra.core.command.Command;
import com.xtra.core.registry.CommandRegistry;
import com.xtra.core.util.store.CommandStore;

public class CommandStateHandler {

    /**
     * Sets the {@link CommandState} for the specified command.
     * 
     * @param clazz The class of the command
     * @param state The new state of the command
     */
    public static void setState(Class<? extends Command> clazz, CommandState state) {
        for (CommandStore store : CommandRegistry.getContainerForCommand(clazz).get().getValue().commandStores) {
            if (store.command().getClass().equals(clazz)) {
                store.setState(state);
            }
        }
    }

    /**
     * Gets the {@link CommandState} for the specified command.
     * 
     * @param clazz The class of the command
     * @return {@link Optional#empty()} is the command could not be found,
     *         otherwise the command state
     */
    public static Optional<CommandState> getState(Class<? extends Command> clazz) {
        for (CommandStore store : CommandRegistry.getContainerForCommand(clazz).get().getValue().commandStores) {
            if (store.command().getClass().equals(clazz)) {
                return Optional.of(store.state());
            }
        }
        return Optional.empty();
    }
}