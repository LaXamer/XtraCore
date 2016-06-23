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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.xtra.core.command.base.EmptyCommand;
import com.xtra.core.internal.InternalHandler;
import com.xtra.core.internal.Internals;
import com.xtra.core.util.CommandHelper;
import com.xtra.core.util.ReflectionScanner;
import com.xtra.core.util.store.CommandStore;

/**
 * A simple utility class for automatically retrieving, building and registering
 * the commands within a plugin.
 */
public class CommandHandler extends InternalHandler {

    private static CommandHandler instance;

    private CommandHandler() {
    }

    /**
     * Creates and initializes a {@link CommandHandler}.
     * 
     * @return The new command handler
     */
    public static CommandHandler create() {
        return new CommandHandler().init();
    }

    private CommandHandler init() {
        super.checkHasCoreInitialized();
        instance = this;

        Internals.commands = ReflectionScanner.getCommands();
        Internals.logger.log("Initializing the command handler!");
        Internals.logger.log("Initializing the command specs for the commands...");

        for (Command command : Internals.commands) {
            this.initializeCommandSpec(command);
        }
        Internals.logger.log("======================================================");
        Internals.logger.log("Adding any necessary child commands to the command specs!");
        this.addChildCommands();
        Internals.logger.log("======================================================");
        Internals.logger.log("Building and registering the commands!");
        for (CommandStore command : Internals.commandStores) {
            this.buildAndRegisterCommand(command.commandSpecBuilder(), command.command());
        }
        Internals.logger.log("======================================================");
        return this;
    }

    private void initializeCommandSpec(Command command) {
        // Create the initial CommandSpec builder
        CommandSpec.Builder specBuilder = CommandSpec.builder().executor(command);

        Internals.logger.log("======================================================");
        Internals.logger.log("Initializing the command spec for the command '" + command.aliases()[0] + "'.");

        // In case null, do not use
        if (command.permission() != null) {
            specBuilder.permission(command.permission());
            Internals.logger.log("Command permission: '" + command.permission() + "'");
        }
        if (command.description() != null) {
            specBuilder.description(Text.of(command.description()));
            Internals.logger.log("Command description: '" + command.description() + "'");
        }
        if (command.args() != null) {
            if (command.args().length != 0) {
                specBuilder.arguments(command.args());
                Internals.logger.log("Command has " + command.args().length + " argument(s).");
            }
        }

        Command parentCommand = CommandHelper.getParentCommand(command);
        if (parentCommand != null) {
            Internals.logger.log("Adding the command and its parent command to the command stores.");
            Internals.commandStores.add(new CommandStore(command, specBuilder, parentCommand));
        } else {
            Internals.logger.log("Parent command not found. Presuming command does not have one.");
            Internals.commandStores.add(new CommandStore(command, specBuilder, null));
        }
    }

    private void addChildCommands() {
        // Go through the commands to find any child commands
        for (CommandStore commandStore : Internals.commandStores) {
            if (commandStore.childOf() != null) {
                if (!(commandStore.childOf() instanceof EmptyCommand)) {
                    // Iterate through to find the parent
                    for (CommandStore commandStore2 : Internals.commandStores) {
                        if (commandStore2.command().equals(commandStore.childOf())) {
                            Internals.logger.log("Adding '" + commandStore.command().aliases()[0] + "' as a child command of '"
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
        Internals.logger.log("Building and registering the command: '" + command.aliases()[0] + "'");
        Sponge.getCommandManager().register(Internals.plugin, commandSpec.build(), command.aliases());
    }

    /**
     * Gets the specified command object.
     * 
     * @param clazz The class of the command to get
     * @return The command object
     */
    public static Command getCommand(Class<? extends Command> clazz) {
        for (Command command : Internals.commands) {
            if (clazz.isInstance(command)) {
                return command;
            }
        }
        return null;
    }

    public static CommandHandler get() {
        return instance;
    }
}
