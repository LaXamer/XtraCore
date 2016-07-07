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

package com.xtra.core.command.runnable;

import java.util.Arrays;

import com.xtra.api.command.Command;
import com.xtra.api.command.runnable.CommandRunnable;
import com.xtra.api.command.runnable.CommandRunnableHandler;
import com.xtra.core.internal.Internals;
import com.xtra.core.plugin.XtraCorePluginContainerImpl;

public class CommandRunnableHandlerImpl implements CommandRunnableHandler {

    private XtraCorePluginContainerImpl container;

    private CommandRunnableHandlerImpl() {
    }

    public static CommandRunnableHandlerImpl create(XtraCorePluginContainerImpl container) {
        Internals.globalLogger.log("Initializing the command runnable handler for " + container.getPlugin().getClass().getName());

        CommandRunnableHandlerImpl handler = new CommandRunnableHandlerImpl();
        handler.container = container;
        return handler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void add(CommandRunnable runnable, Class<? extends Command>... classes) {
        for (Class<? extends Command> clazz : classes) {
            this.container.commandRunnables.put(clazz, runnable);
        }
    }

    @Override
    public void addForAllCommands(CommandRunnable runnable) {
        for (Command command : this.container.getCommandHandler().get().getCommands()) {
            this.container.commandRunnables.put(command.getClass(), runnable);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addForAllCommandsExcept(CommandRunnable runnable, Class<? extends Command>... classes) {
        for (Command command : this.container.getCommandHandler().get().getCommands()) {
            if (!Arrays.asList(classes).contains(command.getClass())) {
                this.container.commandRunnables.put(command.getClass(), runnable);
            }
        }
    }

    @Override
    public boolean doesCommandHaveRunnable(Class<? extends Command> clazz) {
        return this.container.commandRunnables.containsKey(clazz);
    }

    @Override
    public void removeRunnables(Class<? extends Command> clazz) {
        this.container.commandRunnables.removeAll(clazz);
    }

    @Override
    public void removeAllRunnables() {
        this.container.commandRunnables.clear();
    }
}
