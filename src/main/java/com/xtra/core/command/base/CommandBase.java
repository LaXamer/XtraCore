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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.CommandBlock;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.source.LocatedSource;
import org.spongepowered.api.command.source.ProxySource;
import org.spongepowered.api.command.source.RconSource;
import org.spongepowered.api.command.source.RemoteSource;
import org.spongepowered.api.command.source.SignSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.vehicle.minecart.CommandBlockMinecart;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.TextMessageException;

import com.xtra.core.command.Command;
import com.xtra.core.command.CommandPhaseChecker;
import com.xtra.core.command.CommandSourceChecker;
import com.xtra.core.command.CommandSourceGeneric;
import com.xtra.core.command.annotation.RegisterCommand;
import com.xtra.core.command.runnable.CommandPhase;
import com.xtra.core.command.runnable.CommandRunnable;
import com.xtra.core.command.runnable.CommandRunnableResult;
import com.xtra.core.command.runnable.RunAt;
import com.xtra.core.plugin.XtraCoreInternalPluginContainer;
import com.xtra.core.plugin.XtraCorePluginContainer;
import com.xtra.core.registry.CommandRegistry;
import com.xtra.core.util.map.MapSorter;

public abstract class CommandBase<T extends CommandSource> implements Command, CommandSourceGeneric, CommandSourceChecker, CommandPhaseChecker {

    private Map<CommandRunnable, RunAt> map;

    public abstract CommandResult executeCommand(T src, CommandContext args) throws Exception;

    @Override
    public final CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        // Get the plugin containers
        Map.Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> entry = CommandRegistry.getContainerForCommand(this.getClass()).get();
        // Start again with an empty map
        this.map = new HashMap<>();
        // If there is a command runnable set for this class, get them
        if (entry.getValue().commandRunnables.keySet().contains(this.getClass())) {
            Collection<CommandRunnable> runnables = entry.getValue().commandRunnables.get(this.getClass());
            try {
                for (CommandRunnable runnable : runnables) {
                    // Put the command runnable as well as the RunAt annotation
                    // into our mapping
                    this.map.put(runnable,
                            runnable.getClass().getMethod("run", CommandSource.class, CommandContext.class).getAnnotation(RunAt.class));
                }
            } catch (NoSuchMethodException | SecurityException e) {
                // Should never really happen
                entry.getKey().getLogger().log(e);
            }
        }
        if (!this.map.isEmpty()) {
            // Sort the mapping by priority specified in RunAt
            this.map = MapSorter.sortRunAtPriority(this.map);
        }

        // Execute any runnables set for 'PRE'
        Optional<CommandRunnableResult> checkRunnablesPre = this.checkPhase(CommandPhase.PRE, source, args);
        if (checkRunnablesPre.isPresent()) {
            return checkRunnablesPre.get().getResult();
        }

        // Ensure that the specified CommandSource from the generic is the
        // source that executed this command
        Class<?> type = this.getTargetCommandSource();
        Optional<Text> isCorrectCommandSource = this.checkCommandSource(type, source);
        if (isCorrectCommandSource.isPresent()) {
            source.sendMessage(isCorrectCommandSource.get());
            return CommandResult.empty();
        }

        // It's safe to cast it now that we've checked
        @SuppressWarnings("unchecked")
        T src = (T) source;

        // Execute any runnables set for 'START'
        Optional<CommandRunnableResult> checkRunnablesStart = this.checkPhase(CommandPhase.START, source, args);
        if (checkRunnablesStart.isPresent()) {
            return checkRunnablesStart.get().getResult();
        }

        // Check if our command is async. If so, then run it asynchronously
        if (this.getClass().getAnnotation(RegisterCommand.class).async()) {
            Sponge.getScheduler().createTaskBuilder().execute(
                    task -> {
                        try {
                            this.executeCommand(src, args);
                        } catch (TextMessageException e) {
                            src.sendMessage(e.getText());
                        } catch (Exception e2) {
                            src.sendMessage(Text.of(TextColors.RED, "An error has occured while attempting to execute this command."));
                            entry.getKey().getLogger().log(e2);
                        }
                    }).async().submit(entry.getKey().getPlugin());

            // Execute any runnables set for 'POST'. Note that the result is
            // effectively ignored.
            this.checkPhase(CommandPhase.POST, source, args);
            return CommandResult.success();
        }

        try {
            CommandResult result = this.executeCommand(src, args);
            // Execute any runnables set for 'POST'. Note that the result is
            // effectively ignored.
            this.checkPhase(CommandPhase.POST, source, args);
            return result;
        } catch (TextMessageException e) {
            src.sendMessage(e.getText());
        } catch (Exception e2) {
            src.sendMessage(Text.of(TextColors.RED, "An error has occured while attempting to execute this command."));
            entry.getKey().getLogger().log(e2);
        }
        // If errored
        return CommandResult.empty();
    }

    @Override
    public Class<?> getTargetCommandSource() {
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
        return type;
    }

    @Override
    public Optional<Text> checkCommandSource(Class<?> type, CommandSource source) {
        // If it's CommandSource, don't bother with the checks below.
        if (type.equals(CommandSource.class)) {
            return Optional.empty();
        }

        // Most common is player, so it's at the top. Otherwise these are to be
        // alphabetically ordered.
        if (type.equals(Player.class) && !(source instanceof Player)) {
            return Optional.of(Text.of(TextColors.RED, "You must be a player to execute this command!"));
        } else if (type.equals(CommandBlock.class) && !(source instanceof CommandBlock)) {
            return Optional.of(Text.of(TextColors.RED, "Only a command block may execute this command!"));
        } else if (type.equals(CommandBlockMinecart.class) && !(source instanceof CommandBlockMinecart)) {
            return Optional.of(Text.of(TextColors.RED, "Only a command block minecart may execute this command!"));
        } else if (type.equals(CommandBlockSource.class) && !(source instanceof CommandBlockSource)) {
            return Optional.of(Text.of(TextColors.RED, "Only a command block may execute this command!"));
        } else if (type.equals(ConsoleSource.class) && !(source instanceof ConsoleSource)) {
            return Optional.of(Text.of(TextColors.RED, "You must be the console to execute this command!"));
        } else if (type.equals(LocatedSource.class) && !(source instanceof LocatedSource)) {
            return Optional.of(Text.of(TextColors.RED, "Only a command source with a location may execute this command!"));
        } else if (type.equals(ProxySource.class) && !(source instanceof ProxySource)) {
            return Optional.of(Text.of(TextColors.RED, "Only proxy sources may execute this command!"));
        } else if (type.equals(RconSource.class) && !(source instanceof RconSource)) {
            return Optional.of(Text.of(TextColors.RED, "Only an rcon source may execute this command!"));
        } else if (type.equals(RemoteSource.class) && !(source instanceof RemoteSource)) {
            return Optional.of(Text.of(TextColors.RED, "Only remote sources may execute this command!"));
        } else if (type.equals(SignSource.class) && !(source instanceof SignSource)) {
            return Optional.of(Text.of(TextColors.RED, "Only sign may execute this command!"));
        }
        return Optional.empty();
    }

    @Override
    public Optional<CommandRunnableResult> checkPhase(CommandPhase phase, CommandSource source, CommandContext args) {
        for (Map.Entry<CommandRunnable, RunAt> runnableEntry : this.map.entrySet()) {
            if (runnableEntry.getValue().phase().equals(phase)) {
                CommandRunnableResult result = runnableEntry.getKey().run(source, args);
                if (result.getResult() != null) {
                    return Optional.of(result);
                }
            }
        }
        return Optional.empty();
    }
}
