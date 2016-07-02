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
import java.util.Map;

import com.xtra.core.command.Command;
import com.xtra.core.plugin.XtraCoreInternalPluginContainer;
import com.xtra.core.plugin.XtraCorePluginContainer;
import com.xtra.core.plugin.XtraCorePluginHandler;
import com.xtra.core.util.log.LogHandler;

public class CommandRunnableHandler {

    private Map.Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> container;

    private CommandRunnableHandler() {
    }

    public static CommandRunnableHandler create(Object plugin) {
        LogHandler.getGlobalLogger().log("Initializing the command runnable handler for " + plugin.getClass().getName());

        CommandRunnableHandler handler = new CommandRunnableHandler();
        handler.container = XtraCorePluginHandler.getEntryContainerUnchecked(plugin);
        handler.container.getValue().setCommandRunnableHandler(handler);
        return handler;
    }

    /**
     * Adds the specified {@link CommandRunnable} to be ran along with the class
     * to run it on.
     * 
     * @param clazz The class to run the runnable on
     * @param runnable The command runnable to run
     */
    public void add(Class<? extends Command> clazz, CommandRunnable runnable) {
        this.container.getValue().commandRunnables.put(clazz, runnable);
    }

    /**
     * Adds the specified {@link CommandRunnable} to be ran with the specified
     * classes.
     * 
     * @param runnable The runnable to run with the specified classes
     * @param classes The classes to run the command runnable on
     */
    @SuppressWarnings("unchecked")
    public void add(CommandRunnable runnable, Class<? extends Command>... classes) {
        for (Class<? extends Command> clazz : classes) {
            this.container.getValue().commandRunnables.put(clazz, runnable);
        }
    }

    /**
     * Adds this specified {@link CommandRunnable} to be ran for all commands.
     * 
     * @param runnable The runnable to run for all commands
     */
    public void addForAllCommands(CommandRunnable runnable) {
        for (Command command : this.container.getKey().getCommandHandler().getCommands()) {
            this.container.getValue().commandRunnables.put(command.getClass(), runnable);
        }
    }

    /**
     * Adds the {@link CommandRunnable} to be ran for all commands except for
     * the specified classes.
     * 
     * @param runnable The runnable to run
     * @param classes The classes to not run this runnable on
     */
    @SuppressWarnings("unchecked")
    public void addForAllCommandsExcept(CommandRunnable runnable, Class<? extends Command>... classes) {
        for (Command command : this.container.getKey().getCommandHandler().getCommands()) {
            if (!Arrays.asList(classes).contains(command.getClass())) {
                this.container.getValue().commandRunnables.put(command.getClass(), runnable);
            }
        }
    }

    /**
     * Checks if the specified command class has any corresponding
     * {@link CommandRunnable}s.
     * 
     * @param clazz The class to check
     * @return If the class has any corresponding runnables
     */
    public boolean doesCommandHaveRunnable(Class<? extends Command> clazz) {
        return this.container.getValue().commandRunnables.containsKey(clazz);
    }

    /**
     * Removes a {@link CommandRunnable} from the specified class. Note that if
     * the class has multiple corresponding {@link CommandRunnable}s, they will
     * all be removed.
     * 
     * @param clazz The class to remove runnables from
     */
    public void removeRunnable(Class<? extends Command> clazz) {
        this.container.getValue().commandRunnables.removeAll(clazz);
    }

    /**
     * Removes all runnables from all classes.
     */
    public void removeAllRunnables() {
        this.container.getValue().commandRunnables.clear();
    }
}
