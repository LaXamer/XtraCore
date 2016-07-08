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
import java.util.Optional;
import java.util.Set;

import com.xtra.api.command.Command;
import com.xtra.api.command.runnable.CommandRunnable;
import com.xtra.api.command.state.CommandState;
import com.xtra.api.plugin.XtraCorePluginContainer;
import com.xtra.api.registry.CommandRegistry;
import com.xtra.core.internal.Internals;
import com.xtra.core.plugin.XtraCorePluginContainerImpl;

public class CommandRegistryImpl implements CommandRegistry {
    
    private Map<Command, XtraCorePluginContainer> globalCommands = new HashMap<>();

    public void add(Command command, XtraCorePluginContainerImpl container) {
        Internals.globalLogger.log("Adding command '" + command.aliases()[0] + "' to the global command registry!");
        this.globalCommands.put(command, container);
    }

    public Optional<Command> getCommand(Class<? extends Command> clazz) {
        for (Command command : this.globalCommands.keySet()) {
            if (command.getClass().equals(clazz)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }

    public Optional<Map.Entry<Command, XtraCorePluginContainer>> getEntry(Class<? extends Command> clazz) {
        for (Map.Entry<Command, XtraCorePluginContainer> entry : this.globalCommands.entrySet()) {
            if (entry.getKey().getClass().equals(clazz)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public Set<Command> getAllCommands() {
        return this.globalCommands.keySet();
    }

    public Map<Command, XtraCorePluginContainer> getAllCommandMappings() {
        return this.globalCommands;
    }

    @SuppressWarnings("unchecked")
    public void addRunnables(CommandRunnable runnable, Class<? extends Command>... classes) {
        for (Class<? extends Command> clazz : classes) {
            for (Map.Entry<Command, XtraCorePluginContainer> entry : this.globalCommands.entrySet()) {
                if (entry.getKey().getClass().equals(clazz)) {
                    entry.getValue().getCommandHandler().get().getCommandRunnableHandler().add(runnable, classes);
                }
            }
        }
    }

    public boolean doesCommandHaveRunnable(Class<? extends Command> clazz) {
        for (Command command : this.globalCommands.keySet()) {
            if (command.getClass().equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    public void removeRunnables(Class<? extends Command> clazz) {
        for (Command command : this.globalCommands.keySet()) {
            if (command.getClass().equals(clazz)) {
                this.globalCommands.remove(command);
            }
        }
    }

    public void setState(Class<? extends Command> clazz, CommandState state) {
        for (Map.Entry<Command, XtraCorePluginContainer> entry : this.globalCommands.entrySet()) {
            if (entry.getKey().getClass().equals(clazz)) {
                entry.getValue().getCommandHandler().get().getCommandStateHandler().setState(clazz, state);
            }
        }
    }

    public Optional<CommandState> getState(Class<? extends Command> clazz) {
        for (Map.Entry<Command, XtraCorePluginContainer> entry : this.globalCommands.entrySet()) {
            if (entry.getKey().getClass().equals(clazz)) {
                return Optional.of(entry.getValue().getCommandHandler().get().getCommandStateHandler().getState(clazz).get());
            }
        }
        return Optional.empty();
    }
}
