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

package io.github.narikiro.core.registry;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.github.narikiro.api.command.Command;
import io.github.narikiro.api.command.runnable.CommandRunnable;
import io.github.narikiro.api.command.state.CommandState;
import io.github.narikiro.api.plugin.XtraCorePluginContainer;
import io.github.narikiro.api.registry.CommandRegistry;
import io.github.narikiro.core.internal.Internals;
import io.github.narikiro.core.plugin.XtraCorePluginContainerImpl;
import io.github.narikiro.core.util.CommandGetter;

public class CommandRegistryImpl implements CommandRegistry {

    private Map<Command, XtraCorePluginContainer> globalCommands = new HashMap<>();

    public void add(Command command, XtraCorePluginContainerImpl container) {
        Internals.globalLogger.info("Adding command '" + command.aliases()[0] + "' to the global command registry!");
        this.globalCommands.put(command, container);
    }

    @Override
    public Optional<Command> getCommand(Class<? extends Command> clazz) {
        checkNotNull(clazz, "Command class cannot be null!");
        for (Command command : this.globalCommands.keySet()) {
            if (command.getClass().equals(clazz)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Command> getCommand(String primaryAlias) {
        Optional<Map.Entry<Command, XtraCorePluginContainer>> optionalEntry = CommandGetter.getEntry(primaryAlias, globalCommands);
        if (optionalEntry.isPresent()) {
            return Optional.of(optionalEntry.get().getKey());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Map.Entry<Command, XtraCorePluginContainer>> getEntry(Class<? extends Command> clazz) {
        checkNotNull(clazz, "Command class caannot be null!");
        for (Map.Entry<Command, XtraCorePluginContainer> entry : this.globalCommands.entrySet()) {
            if (entry.getKey().getClass().equals(clazz)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Map.Entry<Command, XtraCorePluginContainer>> getEntry(String primaryAlias) {
        return CommandGetter.getEntry(primaryAlias, globalCommands);
    }

    @Override
    public Set<Command> getAllCommands() {
        return this.globalCommands.keySet();
    }

    @Override
    public Map<Command, XtraCorePluginContainer> getAllCommandMappings() {
        return this.globalCommands;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addRunnables(CommandRunnable runnable, Class<? extends Command>... classes) {
        checkNotNull(runnable, "Command runnable cannot be null!");
        for (Class<? extends Command> clazz : classes) {
            for (Map.Entry<Command, XtraCorePluginContainer> entry : this.globalCommands.entrySet()) {
                if (entry.getKey().getClass().equals(clazz)) {
                    entry.getValue().getCommandHandler().get().getCommandRunnableHandler().add(runnable, classes);
                }
            }
        }
    }

    @Override
    public boolean doesCommandHaveRunnable(Class<? extends Command> clazz) {
        checkNotNull(clazz, "Command class cannot be null!");
        for (Map.Entry<Command, XtraCorePluginContainer> entry : this.globalCommands.entrySet()) {
            if (entry.getKey().getClass().equals(clazz)) {
                return entry.getValue().getCommandHandler().get().getCommandRunnableHandler().doesCommandHaveRunnable(clazz);
            }
        }
        return false;
    }

    @Override
    public void removeRunnables(Class<? extends Command> clazz) {
        checkNotNull(clazz, "Command class cannot be null!");
        for (Map.Entry<Command, XtraCorePluginContainer> entry : this.globalCommands.entrySet()) {
            if (entry.getKey().getClass().equals(clazz)) {
                entry.getValue().getCommandHandler().get().getCommandRunnableHandler().removeRunnables(clazz);
            }
        }
    }

    @Override
    public void setState(Class<? extends Command> clazz, CommandState state) {
        checkNotNull(clazz, "Command class cannot be null!");
        checkNotNull(state, "Command state cannot be null!");
        for (Map.Entry<Command, XtraCorePluginContainer> entry : this.globalCommands.entrySet()) {
            if (entry.getKey().getClass().equals(clazz)) {
                entry.getValue().getCommandHandler().get().getCommandStateHandler().setState(clazz, state);
            }
        }
    }

    @Override
    public Optional<CommandState> getState(Class<? extends Command> clazz) {
        checkNotNull(clazz, "Command class cannot be null!");
        for (Map.Entry<Command, XtraCorePluginContainer> entry : this.globalCommands.entrySet()) {
            if (entry.getKey().getClass().equals(clazz)) {
                return Optional.of(entry.getValue().getCommandHandler().get().getCommandStateHandler().getState(clazz).get());
            }
        }
        return Optional.empty();
    }
}
