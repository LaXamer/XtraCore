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

package com.xtra.core.command.base;

import java.lang.reflect.Method;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.TextMessageException;

import com.xtra.core.command.Command;
import com.xtra.core.command.annotation.RegisterCommand;
import com.xtra.core.internal.Internals;

public abstract class CommandBase<T extends CommandSource> implements Command {

    // For async(), due to anonymous inner class
    private static CommandResult result;

    public abstract CommandResult executeCommand(T src, CommandContext args) throws Exception;

    @Override
    public final CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        // Iterate through the methods to find executeCommand()
        Class<?> type = null;
        for (Method method : this.getClass().getMethods()) {
            // Find our executeCommand method
            if (method.getName().equals("executeCommand")) {
                // Find one without type erasure :S
                if (!method.getParameterTypes()[0].equals(CommandSource.class)) {
                    type = method.getParameterTypes()[0];
                    break;
                }
            }
        }
        // It is possible that CommandSource was specified, so if we didn't find
        // one, then use CommandSource as a default.
        if (type == null) {
            type = CommandSource.class;
        }

        if (type.equals(Player.class) && !(source instanceof Player)) {
            source.sendMessage(Text.of(TextColors.RED, "You must be a player to execute this command!"));
            return CommandResult.empty();
        } else if (type.equals(ConsoleSource.class) && !(source instanceof ConsoleSource)) {
            source.sendMessage(Text.of(TextColors.RED, "You must be the console to execute this command!"));
            return CommandResult.empty();
        } else if (type.equals(CommandBlockSource.class) && !(source instanceof CommandBlockSource)) {
            source.sendMessage(Text.of(TextColors.RED, "Only a command block may execute this command!"));
            return CommandResult.empty();
        }

        @SuppressWarnings("unchecked")
        T src = (T) source;

        boolean isAsync = this.getClass().getAnnotation(RegisterCommand.class).async();

        if (isAsync) {
            Sponge.getScheduler().createTaskBuilder().execute(
                    task -> {
                        try {
                            CommandBase.result = this.executeCommand(src, args);
                        } catch (TextMessageException e) {
                            src.sendMessage(e.getText());
                            CommandBase.result = CommandResult.empty();
                        } catch (Exception e2) {
                            Internals.logger.log(e2);
                            CommandBase.result = CommandResult.empty();
                        }
                    }).async().submit(Internals.plugin);
            return CommandBase.result;
        }

        try {
            return this.executeCommand(src, args);
        } catch (TextMessageException e) {
            src.sendMessage(e.getText());
        } catch (Exception e2) {
            Internals.logger.log(e2);
        }
        // If errored
        return CommandResult.empty();
    }
}
