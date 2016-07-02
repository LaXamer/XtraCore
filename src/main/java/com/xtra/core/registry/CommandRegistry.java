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

package com.xtra.core.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.xtra.core.command.Command;
import com.xtra.core.internal.Internals;
import com.xtra.core.plugin.XtraCoreInternalPluginContainer;
import com.xtra.core.plugin.XtraCorePluginContainer;
import com.xtra.core.util.log.LogHandler;

public class CommandRegistry {
    
    private static Map<Command, XtraCorePluginContainer> globalCommands = new HashMap<>();

    public static void add(Command command, XtraCorePluginContainer container) {
        LogHandler.getGlobalLogger().log("Adding command '" + command.aliases()[0] + "' to the global command registry!");
        globalCommands.put(command, container);
    }

    public static Command getCommand(Class<? extends Command> clazz) {
        for (Command command : globalCommands.keySet()) {
            if (command.getClass().equals(clazz)) {
                return command;
            }
        }
        return null;
    }

    public static Map.Entry<Command, XtraCorePluginContainer> getEntry(Class<? extends Command> clazz) {
        for (Map.Entry<Command, XtraCorePluginContainer> entry : globalCommands.entrySet()) {
            if (entry.getKey().getClass().equals(clazz)) {
                return entry;
            }
        }
        return null;
    }

    public static Map.Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> getContainerForCommand(Class<? extends Command> clazz) {
        for (Map.Entry<Command, XtraCorePluginContainer> entry : globalCommands.entrySet()) {
            if (entry.getKey().getClass().equals(clazz)) {
                return Maps.immutableEntry(entry.getValue(), Internals.plugins.get(entry.getValue()));
            }
        }
        return null;
    }

    public static Set<Command> getAllCommands() {
        return globalCommands.keySet();
    }

    public static Map<Command, XtraCorePluginContainer> getAllCommandMappings() {
        return globalCommands;
    }
}
