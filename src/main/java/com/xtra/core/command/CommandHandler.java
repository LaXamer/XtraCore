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

import java.util.Map;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.xtra.core.command.base.EmptyCommand;
import com.xtra.core.plugin.XtraCoreInternalPluginContainer;
import com.xtra.core.plugin.XtraCorePluginContainer;
import com.xtra.core.plugin.XtraCorePluginHandler;
import com.xtra.core.registry.CommandRegistry;
import com.xtra.core.util.CommandHelper;
import com.xtra.core.util.store.CommandStore;

/**
 * A simple utility class for automatically retrieving, building and registering
 * the commands within a plugin.
 */
public class CommandHandler {

    private Map.Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> entry;
    private CommandHelper helper;
    private Set<Command> commands;

    private CommandHandler() {
    }

    public static CommandHandler create(Object plugin) {
        return new CommandHandler().init(XtraCorePluginHandler.getEntryContainerUnchecked(plugin));
    }

    private CommandHandler init(Map.Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> entry) {
        this.entry = entry;

        this.commands = this.entry.getValue().scanner.getCommands();
        this.entry.getValue().setCommandHandler(this);
        this.helper = new CommandHelper(this.entry);
        this.entry.getKey().getLogger().log("Initializing the command handler!");
        this.entry.getKey().getLogger().log("Initializing the command specs for the commands...");

        for (Command command : this.commands) {
            this.initializeCommandSpec(command);
        }
        this.entry.getKey().getLogger().log("======================================================");
        this.entry.getKey().getLogger().log("Adding any necessary child commands to the command specs!");
        this.addChildCommands();
        this.entry.getKey().getLogger().log("======================================================");
        this.entry.getKey().getLogger().log("Building and registering the commands!");
        for (CommandStore command : this.entry.getValue().commandStores) {
            this.buildAndRegisterCommand(command.commandSpecBuilder(), command.command());
            CommandRegistry.add(command.command(), this.entry.getKey());
        }
        this.entry.getKey().getLogger().log("======================================================");
        return this;
    }

    private void initializeCommandSpec(Command command) {
        // Create the initial CommandSpec builder
        CommandSpec.Builder specBuilder = CommandSpec.builder().executor(command);

        this.entry.getKey().getLogger().log("======================================================");
        this.entry.getKey().getLogger().log("Initializing the command spec for the command '" + command.aliases()[0] + "'.");

        // In case null, do not use
        if (command.permission() != null) {
            specBuilder.permission(command.permission());
            this.entry.getKey().getLogger().log("Command permission: '" + command.permission() + "'");
        }
        if (command.description() != null) {
            specBuilder.description(Text.of(command.description()));
            this.entry.getKey().getLogger().log("Command description: '" + command.description() + "'");
        }
        if (command.args() != null) {
            if (command.args().length != 0) {
                specBuilder.arguments(command.args());
                this.entry.getKey().getLogger().log("Command has " + command.args().length + " argument(s).");
            }
        }

        Command parentCommand = this.helper.getParentCommand(command);
        if (parentCommand != null) {
            this.entry.getKey().getLogger().log("Adding the command and its parent command to the command stores.");
            this.entry.getValue().commandStores.add(new CommandStore(command, specBuilder, parentCommand));
        } else {
            this.entry.getKey().getLogger().log("Parent command not found. Presuming command does not have one.");
            this.entry.getValue().commandStores.add(new CommandStore(command, specBuilder, null));
        }
    }

    private void addChildCommands() {
        // Go through the commands to find any child commands
        for (CommandStore commandStore : this.entry.getValue().commandStores) {
            if (commandStore.childOf() != null) {
                if (!(commandStore.childOf() instanceof EmptyCommand)) {
                    // Iterate through to find the parent
                    for (CommandStore commandStore2 : this.entry.getValue().commandStores) {
                        if (commandStore2.command().equals(commandStore.childOf())) {
                            this.entry.getKey().getLogger().log("Adding '" + commandStore.command().aliases()[0] + "' as a child command of '"
                                    + commandStore2.command().aliases()[0] + "'");
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
        this.entry.getKey().getLogger().log("Building and registering the command: '" + command.aliases()[0] + "'");
        Sponge.getCommandManager().register(this.entry.getKey().getPlugin(), commandSpec.build(), command.aliases());
    }

    public Command getCommand(Class<? extends Command> clazz) {
        for (Command command : this.commands) {
            if (clazz.isInstance(command)) {
                return command;
            }
        }
        return null;
    }

    public Set<Command> getCommands() {
        return this.commands;
    }
}
