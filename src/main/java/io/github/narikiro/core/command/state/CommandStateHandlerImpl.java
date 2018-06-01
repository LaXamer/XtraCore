/**
 * This file is part of XtraCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 - 2018 LaXamer <https://github.com/LaXamer>
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

package io.github.narikiro.core.command.state;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import io.github.narikiro.api.command.Command;
import io.github.narikiro.api.command.state.CommandState;
import io.github.narikiro.api.command.state.CommandStateHandler;
import io.github.narikiro.core.internal.Internals;
import io.github.narikiro.core.plugin.XtraCorePluginContainerImpl;
import io.github.narikiro.core.util.store.CommandStore;

public class CommandStateHandlerImpl implements CommandStateHandler {

    private XtraCorePluginContainerImpl container;

    public static CommandStateHandlerImpl create(XtraCorePluginContainerImpl container) {
        Internals.globalLogger.info("Initializing the command state handler for " + container.getPluginContainer().getName());

        CommandStateHandlerImpl handler = new CommandStateHandlerImpl();
        handler.container = container;
        return handler;
    }

    @Override
    public void setState(Class<? extends Command> clazz, CommandState state) {
        checkNotNull(clazz, "Command class cannot be null!");
        checkNotNull(state, "Command state cannot be null!");
        for (CommandStore store : this.container.commandStores) {
            if (store.command().getClass().equals(clazz)) {
                store.setState(state);
            }
        }
    }

    @Override
    public Optional<CommandState> getState(Class<? extends Command> clazz) {
        checkNotNull(clazz, "Command class cannot be null!");
        for (CommandStore store : this.container.commandStores) {
            if (store.command().getClass().equals(clazz)) {
                return Optional.of(store.state());
            }
        }
        return Optional.empty();
    }
}
