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

import com.xtra.core.command.Command;
import com.xtra.core.plugin.XtraCorePluginHandler;

public class GlobalCommandRunnableHandler {

    /**
     * Adds the specified {@link CommandRunnable} to be ran along with the class
     * to run it on.
     * 
     * @param plugin The plugin
     * @param clazz The class to run the runnable on
     * @param runnable The command runnable to run
     */
    public static void create(Class<?> plugin, Class<? extends Command> clazz, CommandRunnable runnable) {
        XtraCorePluginHandler.getEntryContainerUnchecked(plugin).getValue().commandRunnables.put(clazz, runnable);
    }

    /**
     * Adds the specified {@link CommandRunnable} to be ran with the specified
     * classes.
     * 
     * @param plugin The plugin
     * @param runnable The runnable to run with the specified classes
     * @param classes The classes to run the command runnable on
     */
    @SafeVarargs
    public static void create(Class<?> plugin, CommandRunnable runnable, Class<? extends Command>... classes) {
        for (Class<? extends Command> clazz : classes) {
            XtraCorePluginHandler.getEntryContainerUnchecked(plugin).getValue().commandRunnables.put(clazz, runnable);
        }
    }

    /**
     * Adds this specified {@link CommandRunnable} to be ran for all commands.
     * 
     * @param plugin The plugin
     * @param runnable The runnable to run for all commands
     */
    public static void createForAllCommands(Class<?> plugin, CommandRunnable runnable) {
        for (Command command : XtraCorePluginHandler.getEntryContainerUnchecked(plugin).getKey().getCommandHandler().getCommands()) {
            XtraCorePluginHandler.getEntryContainerUnchecked(plugin).getValue().commandRunnables.put(command.getClass(), runnable);
        }
    }

    /**
     * Adds the {@link CommandRunnable} to be ran for all commands except for
     * the specified classes.
     * 
     * @param plugin The plugin
     * @param runnable The runnable to run
     * @param classes The classes to not run this runnable on
     */
    @SafeVarargs
    public static void createForAllCommandsExcept(Class<?> plugin, CommandRunnable runnable, Class<? extends Command>... classes) {
        for (Command command : XtraCorePluginHandler.getEntryContainerUnchecked(plugin).getKey().getCommandHandler().getCommands()) {
            if (!Arrays.asList(classes).contains(command.getClass())) {
                XtraCorePluginHandler.getEntryContainerUnchecked(plugin).getValue().commandRunnables.put(command.getClass(), runnable);
            }
        }
    }

    /**
     * Checks if the specified command class has any corresponding
     * {@link CommandRunnable}s.
     * 
     * @param plugin The plugin
     * @param clazz The class to check
     * @return If the class has any corresponding runnables
     */
    public static boolean doesCommandHaveRunnable(Class<?> plugin, Class<? extends Command> clazz) {
        return XtraCorePluginHandler.getEntryContainerUnchecked(plugin).getValue().commandRunnables.containsKey(clazz);
    }

    /**
     * Removes a {@link CommandRunnable} from the specified class. Note that if
     * the class has multiple corresponding {@link CommandRunnable}s, they will
     * all be removed.
     * 
     * @param plugin The plugin
     * @param clazz The class to remove runnables from
     */
    public static void removeRunnable(Class<?> plugin, Class<? extends Command> clazz) {
        XtraCorePluginHandler.getEntryContainerUnchecked(plugin).getValue().commandRunnables.removeAll(clazz);
    }

    /**
     * Removes all runnables from all classes.
     * 
     * @param plugin The plugin
     */
    public static void removeAllRunnables(Class<?> plugin) {
        XtraCorePluginHandler.getEntryContainerUnchecked(plugin).getValue().commandRunnables.clear();
    }
}
