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

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.spongepowered.api.event.Listener;

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

    private static final Reflections REFLECTIONS = new Reflections(Internals.plugin.getClass().getPackage().getName());

    /**
     * Uses reflection to get the commands of the plugin.
     * 
     * @return A set of the commands
     */
    public static Set<Command> getCommands() {
        Internals.logger.log("Using reflection to access the registered commands...");
        Set<Class<?>> classes = REFLECTIONS.getTypesAnnotatedWith(RegisterCommand.class);
        Set<Command> commands = new HashSet<>();

        for (Class<?> oneClass : classes) {
            try {
                Object o = oneClass.newInstance();
                if (o instanceof Command) {
                    Command c = (Command) o;
                    Internals.logger.log("Recognized command '" + c.aliases()[0] + "'! Adding to command list...");
                    commands.add(c);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                Internals.logger.log(e);
            }
        }
        Internals.logger.log("Commands added.");
        Internals.logger.log("======================================================");
        return commands;
    }

    /**
     * Uses reflection to get the configs of a plugin.
     * 
     * @return A set of configs
     */
    public static Set<Config> getConfigs() {
        Internals.logger.log("Using reflection to access the registered configs...");
        Set<Class<?>> classes = REFLECTIONS.getTypesAnnotatedWith(RegisterConfig.class);
        Set<Config> configs = new HashSet<>();

        for (Class<?> oneClass : classes) {
            try {
                Object o = oneClass.newInstance();
                if (o instanceof Config) {
                    Config c = (Config) o;
                    Internals.logger.log("Recognized config '" + c.getClass().getAnnotation(RegisterConfig.class).configName()
                            + "'! Adding to config list...");
                    configs.add(c);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                Internals.logger.log(e);
            }
        }
        Internals.logger.log("Configs added.");
        Internals.logger.log("======================================================");
        return configs;
    }

    public static Set<Object> getPluginListeners() {
        Internals.logger.log("Using reflection to access and register the listeners...");
        Set<Method> methods = REFLECTIONS.getMethodsAnnotatedWith(Listener.class);
        Set<Object> listenerClasses = new HashSet<>();
        for (Method method : methods) {
            try {
                listenerClasses.add(method.getDeclaringClass().newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                Internals.logger.log(e);
            }
        }
        Internals.logger.log("Listeners added.");
        Internals.logger.log("======================================================");
        return listenerClasses;
    }
}
