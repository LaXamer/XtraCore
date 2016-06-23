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

package com.xtra.core.internal;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.api.plugin.PluginContainer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.xtra.core.command.Command;
import com.xtra.core.command.runnable.CommandRunnable;
import com.xtra.core.config.Config;
import com.xtra.core.util.log.Logger;
import com.xtra.core.util.store.CommandStore;

/**
 * This is an internal class for storing various information that should only
 * ever be accessed by XtraCore. It is recommended to NOT touch or access these
 * values directly (unless you're XtraCore itself)!
 */
public class Internals {

    public static final String VERSION = "@project.version@";
    public static Object plugin;
    public static PluginContainer pluginContainer;
    public static Set<Command> commands;
    public static Set<CommandStore> commandStores = new HashSet<>();
    public static Set<Config> configs;
    public static boolean initialized = false;
    public static Logger logger;
    public static Multimap<Class<? extends Command>, CommandRunnable> commandRunnables = ArrayListMultimap.create();

    /**
     * Checks if the specified class has already been instantiated and if so
     * then returns its object. If not, then this will instantiate a new object
     * for the specified class.
     * 
     * @param clazz The class to check
     * @return The object if it has already been instantiated, otherwise a new
     *         instance of the specified class
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object checkIfAlreadyExists(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        if (commands != null) {
            for (Command command : commands) {
                if (clazz.equals(command.getClass())) {
                    return command;
                }
            }
        }
        if (configs != null) {
            for (Config config : configs) {
                if (clazz.equals(config.getClass())) {
                    return config;
                }
            }
        }
        return clazz.newInstance();
    }
}
