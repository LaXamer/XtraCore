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
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.xtra.api.command.Command;
import com.xtra.api.command.annotation.RegisterCommand;
import com.xtra.api.config.Config;
import com.xtra.api.config.annotation.RegisterConfig;
import com.xtra.api.plugin.XtraCorePluginContainer;
import com.xtra.core.internal.Internals;

/**
 * A class that uses reflection to scan a plugin for information, such as the
 * plugin's commands.
 */
public class ReflectionScanner {

    private XtraCorePluginContainer container;
    private Reflections reflections;

    private ReflectionScanner() {
    }

    public static ReflectionScanner create(XtraCorePluginContainer container) {
        ReflectionScanner scanner = new ReflectionScanner();
        scanner.container = container;
        scanner.reflections = new Reflections(container.getPlugin().getClass().getPackage().getName(), new SubTypesScanner(),
                new TypeAnnotationsScanner(), new MethodAnnotationsScanner());
        return scanner;
    }

    /**
     * Uses reflection to get the commands of the plugin.
     * 
     * @return A set of the commands
     */
    public Set<Command> getCommands() {
        this.container.getLogger().info(Internals.LOG_HEADER);
        this.container.getLogger().info("Using reflection to access the registered commands...");
        Set<Class<?>> classes = this.reflections.getTypesAnnotatedWith(RegisterCommand.class);
        Set<Command> commands = new HashSet<>();

        for (Class<?> oneClass : classes) {
            try {
                Object o = oneClass.newInstance();
                if (o instanceof Command) {
                    Command c = (Command) o;
                    this.container.getLogger().info("Recognized command '" + c.aliases()[0] + "'! Adding to command list...");
                    commands.add(c);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                this.container.getLogger().error("An error has occurred while attempting to instantiate the commands!", e);
            }
        }
        this.container.getLogger().info("Commands added.");
        return commands;
    }

    /**
     * Uses reflection to get the configs of a plugin.
     * 
     * @return A set of configs
     */
    public Set<Config> getConfigs() {
        this.container.getLogger().info(Internals.LOG_HEADER);
        this.container.getLogger().info("Using reflection to access the registered configs...");
        Set<Class<?>> classes = this.reflections.getTypesAnnotatedWith(RegisterConfig.class);
        Set<Config> configs = new HashSet<>();

        for (Class<?> oneClass : classes) {
            try {
                Object o = oneClass.newInstance();
                if (o instanceof Config) {
                    Config c = (Config) o;
                    this.container.getLogger().info("Recognized config '" + c.getClass().getAnnotation(RegisterConfig.class).configName()
                            + "'! Adding to config list...");
                    configs.add(c);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                this.container.getLogger().error("An error has occurred while attempting to instantiate the configs!", e);
            }
        }
        this.container.getLogger().info("Configs added.");
        return configs;
    }

    public Multimap<Class<?>, Method> getPluginListeners() {
        this.container.getLogger().info(Internals.LOG_HEADER);
        this.container.getLogger().info("Using reflection to access and register the listeners...");
        Set<Method> methods = this.reflections.getMethodsAnnotatedWith(Listener.class);
        Multimap<Class<?>, Method> map = ArrayListMultimap.create();
        for (Method method : methods) {
            if (method.getDeclaringClass().getAnnotation(Plugin.class) == null) {
                this.container.getLogger().info("Registering method listener:");
                this.container.getLogger().info(method.toString());
                if (map.containsKey(method.getDeclaringClass())) {
                    map.get(method.getDeclaringClass()).add(method);
                } else {
                    map.put(method.getDeclaringClass(), method);
                }
            }
        }
        return map;
    }
}
