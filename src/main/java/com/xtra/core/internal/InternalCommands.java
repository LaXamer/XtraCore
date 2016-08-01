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

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.xtra.api.command.Command;
import com.xtra.api.command.annotation.RegisterCommand;
import com.xtra.core.CoreImpl;
import com.xtra.core.XtraCore;

public class InternalCommands {

    private static PaginationList info;

    public static void createCommands(XtraCore instance) {
        setInfo();

        CommandSpec versionCommand = CommandSpec.builder()
                .permission("xtracore.version")
                .description(Text.of("Displays various XtraCore info."))
                .executor((src, args) -> {
                    info.sendTo(src);
                    return CommandResult.success();
                })
                .build();

        CommandSpec commandCommand = CommandSpec.builder()
                .permission("xtracore.command")
                .description(Text.of("Provides information on a specified command"))
                .arguments(GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("command"))))
                .executor((src, args) -> {
                    // This one is interesting. The thing is, multiple child
                    // commands can have the same alias, as long as they have
                    // different parents. For example, this:
                    // /command1 help
                    // and
                    // /command2 help
                    // The two 'help' commands both share the same alias, but
                    // different parents allow both of them to co-exist in
                    // Sponge. To avoid collisions with the wrong command, we
                    // need both the child and parent command in the arguments
                    // and we have to match the parent command so that we do not
                    // accidentally hit a child command from another plugin.
                    String command = args.<String>getOne("command").get();
                    for (Command command2 : CoreImpl.instance.getCommandRegistry().getAllCommands()) {
                        for (String alias : command2.aliases()) {
                            // If there is a space, assume user wants to specify
                            // a child command
                            if (command.contains(" ")) {
                                // Split by the space
                                String[] splitCommand = command.split(" ");
                                // If too many spaces...not what we want
                                if (splitCommand.length > 2) {
                                    src.sendMessage(Text.of(TextColors.RED, "Too many arguments!"));
                                }
                                String parentCommand = splitCommand[0];
                                String childCommand = splitCommand[1];
                                // Now we can check if the child command is the
                                // alias.
                                if (alias.equalsIgnoreCase(childCommand)) {
                                    Command parentCommand2 = CoreImpl.instance.getCommandRegistry()
                                            .getCommand(command2.getClass().getAnnotation(RegisterCommand.class).childOf()).get();
                                    for (String alias2 : parentCommand2.aliases()) {
                                        if (parentCommand.equalsIgnoreCase(alias2)) {
                                            sendCommandInfo(src, command2, parentCommand2);
                                            return CommandResult.success();
                                        }
                                    }
                                }
                            } else if (alias.equalsIgnoreCase(command)) {
                                // No space, assume not a child command
                                sendCommandInfo(src, command2, null);
                                return CommandResult.success();
                            }
                        }
                    }
                    src.sendMessage(Text.of(TextColors.RED, "Could not find command ", TextColors.BLUE, command, TextColors.RED, "!"));
                    return CommandResult.empty();
                })
                .build();

        CommandSpec mainCommand = CommandSpec.builder()
                .permission("xtracore.base")
                .description(Text.of("XtraCore base command."))
                .executor((src, args) -> {
                    info.sendTo(src);
                    return CommandResult.success();
                })
                .child(versionCommand, "version", "v")
                .child(commandCommand, "command", "c")
                .build();

        Sponge.getCommandManager().register(instance, mainCommand, "xtracore", "xtra-core");
    }

    private static void setInfo() {
        String url = "https://github.com/XtraStudio/XtraCore";
        try {
            info = PaginationList.builder()
                    .padding(Text.of(TextColors.GOLD, "-="))
                    .title(Text.of(TextColors.GREEN, "XtraCore"))
                    .contents(Text.of(TextColors.BLUE, "Author: ", TextColors.GREEN, "12AwesomeMan34"),
                            Text.of(TextColors.BLUE, "Version: ", TextColors.GREEN, Internals.VERSION),
                            Text.of(TextColors.BLUE, "GitHub: ", TextColors.GREEN, TextActions.openUrl(new URL(url)), url))
                    .build();
        } catch (MalformedURLException e) {
            Internals.globalLogger.error("A MalformedURLException has occured while attempting to construct a URL!", e);
        }
    }

    private static void sendCommandInfo(CommandSource source, Command command, @Nullable Command parentCommand) {
        StringBuilder sb = new StringBuilder();
        // If the aliases are greater than one, do a for
        // loop
        if (command.aliases().length > 1) {
            for (int i = 0; i < command.aliases().length; i++) {
                // If this is the last one in the array,
                // do not add a comma
                if (i == command.aliases().length - 1) {
                    sb.append(command.aliases()[i]);
                } else {
                    sb.append(command.aliases()[i] + ", ");
                }
            }
        } else {
            // Else just append the only alias
            sb.append(command.aliases()[0]);
        }
        String permission = command.permission() != null ? command.permission() : "None.";
        String description = command.description() != null ? command.description() : "None.";
        String usage = command.usage() != null ? command.usage() : "None.";
        PaginationList.builder()
                .title(Text.of(TextColors.GREEN, command.aliases()[0]))
                .padding(Text.of(TextColors.GOLD, "-="))
                .contents(Text.of(TextColors.BLUE, "Aliases: ", TextColors.GREEN, sb.toString()),
                        Text.of(TextColors.BLUE, "Permission: ", TextColors.GREEN, permission),
                        Text.of(TextColors.BLUE, "Description: ", TextColors.GREEN, description),
                        Text.of(TextColors.BLUE, "Usage: ", TextColors.GREEN, usage),
                        Text.of(TextColors.BLUE, "Is async: ", TextColors.GREEN,
                                CoreImpl.instance.getCommandAnnotationHelper().isAsync(command.getClass()) ? "True." : "False."),
                        parentCommand != null ? Text.of(TextColors.BLUE, "Parent command: ", TextColors.GREEN, parentCommand.aliases()[0])
                                : Text.of(TextColors.GREEN, "No parent command."))
                .sendTo(source);
    }
}
