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

package com.xtra.core.command;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.xtra.core.Core;
import com.xtra.core.command.base.CommandBase;
import com.xtra.core.command.base.EmptyCommand;
import com.xtra.core.util.CommandHelper;
import com.xtra.core.util.store.CommandStore;

/**
 * A simple utility class for automatically retrieving, building and registering
 * the commands within a plugin.
 */
public class CommandRegistrar {

    private Object plugin;
    private Set<CommandStore> commandStores = new HashSet<>();
    private Set<CommandBase<?>> commands;

    public CommandRegistrar() {
        this.plugin = Core.plugin();
    }

    public void initialize() {
        // Get the commands for the plugin
        commands = Core.commands();
        for (CommandBase<?> command : commands) {
            initializeCommandSpec(command);
        }
        addChildCommands();
        for (CommandStore command : this.commandStores) {
            buildAndRegisterCommand(command.commandSpecBuilder(), command.command());
        }
    }

    private void initializeCommandSpec(CommandBase<?> command) {
        // Create the initial CommandSpec builder
        CommandSpec.Builder specBuilder = CommandSpec.builder()
                .executor(command);

        // In case null, do not use
        if (command.permission() != null) {
            specBuilder.permission(command.permission());
        }
        if (command.description() != null) {
            specBuilder.description(Text.of(command.description()));
        }
        if (command.args() != null) {
            if (command.args().length != 0) {
                specBuilder.arguments(command.args());
            }
        }

        Command parentCommand = CommandHelper.getParentCommand(command);
        if (parentCommand != null) {
            commandStores.add(new CommandStore(command, specBuilder, parentCommand));
        } else {
            commandStores.add(new CommandStore(command, specBuilder, null));
        }
    }

    private void addChildCommands() {
        // Go through the commands to find any child commands
        for (CommandStore commandStore : commandStores) {
            if (commandStore.childOf() != null) {
                if (!(commandStore.childOf() instanceof EmptyCommand)) {
                    // Iterate through to find the parent
                    for (CommandStore commandStore2 : commandStores) {
                        if (commandStore2.command().equals(commandStore.childOf())) {
                            commandStore2.commandSpecBuilder().child(commandStore.commandSpecBuilder().build(), commandStore.command().aliases());
                        }
                    }
                }
            }
        }
    }

    /**
     * Builds a {@link CommandSpec} and registers it.
     * 
     * @param commandSpec The command spec to build and register
     * @param command The command
     */
    private void buildAndRegisterCommand(CommandSpec.Builder commandSpec, Command command) {
        Sponge.getCommandManager().register(plugin, commandSpec.build(), command.aliases());
    }
}
