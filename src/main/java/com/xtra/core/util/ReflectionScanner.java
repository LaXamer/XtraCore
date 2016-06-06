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

package com.xtra.core.util;

import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

import com.xtra.core.command.Command;
import com.xtra.core.command.annotation.RegisterCommand;
import com.xtra.core.config.Config;
import com.xtra.core.config.annotation.RegisterConfig;
import com.xtra.core.internal.Internals;

/**
 * A class that uses reflection to scan a plugin for information, such as the
 * plugin's commands.
 */
public class ReflectionScanner {

    /**
     * Uses reflection to get the commands of the plugin.
     * 
     * @return A set of the commands
     */
    public static Set<Command> getCommands() {
        Reflections reflections = new Reflections(Internals.plugin.getClass().getPackage().getName());
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RegisterCommand.class);
        Set<Command> commands = new HashSet<>();

        for (Class<?> oneClass : classes) {
            try {
                Object o = oneClass.newInstance();
                if (o instanceof Command) {
                    commands.add((Command) o);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return commands;
    }

    /**
     * Uses reflection to get the configs of a plugin.
     * 
     * @return A set of configs
     */
    public static Set<Config> getConfigs() {
        Reflections reflections = new Reflections(Internals.plugin.getClass().getPackage().getName());
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RegisterConfig.class);
        Set<Config> configs = new HashSet<>();

        for (Class<?> oneClass : classes) {
            try {
                Object o = oneClass.newInstance();
                if (o instanceof Config) {
                    configs.add((Config) o);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return configs;
    }
}
