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

package io.github.narikiro.core.command.base;

import io.github.narikiro.api.command.annotation.RegisterCommand;
import io.github.narikiro.api.command.annotation.RunAt;
import io.github.narikiro.api.command.base.CommandBase;
import io.github.narikiro.api.command.runnable.CommandPhase;
import io.github.narikiro.api.command.runnable.CommandRunnable;
import io.github.narikiro.api.command.runnable.CommandRunnableResult;
import io.github.narikiro.api.command.state.CommandState;
import io.github.narikiro.api.util.command.CommandBaseExecutor;
import io.github.narikiro.core.CoreImpl;
import io.github.narikiro.core.plugin.XtraCorePluginContainerImpl;
import io.github.narikiro.core.util.map.MapSorter;
import io.github.narikiro.core.util.store.CommandStore;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.CommandBlock;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.source.ProxySource;
import org.spongepowered.api.command.source.RconSource;
import org.spongepowered.api.command.source.RemoteSource;
import org.spongepowered.api.command.source.SignSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.vehicle.minecart.CommandBlockMinecart;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandBaseImpl implements CommandBaseExecutor {

    private CommandBase<?> base;
    private XtraCorePluginContainerImpl container;
    private Map<CommandRunnable, RunAt> map;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public CommandResult execute(CommandBase commandBase, Class<?> targetSource, CommandSource source, CommandContext args)
            throws CommandException {
        if (this.container == null) {
            this.container = (XtraCorePluginContainerImpl) CoreImpl.instance.getCommandRegistry().getEntry(commandBase.getClass()).get().getValue();
        }
        this.base = commandBase;
        // Start again with an empty map
        this.map = new HashMap<>();
        // If there is a command runnable set for this class, get them
        if (this.container.commandRunnables.keySet().contains(commandBase.getClass())) {
            Collection<CommandRunnable> runnables = this.container.commandRunnables.get(commandBase.getClass());
            try {
                for (CommandRunnable runnable : runnables) {
                    // Put the command runnable as well as the RunAt annotation
                    // into our mapping
                    RunAt runAt = runnable.getClass().getMethod("run", CommandSource.class, CommandContext.class).getAnnotation(RunAt.class);
                    if (runAt == null) {
                        // If RunAt wasn't specified, use defaults
                        runAt = new RunAt() {

                            @Override
                            public Class<? extends Annotation> annotationType() {
                                return RunAt.class;
                            }

                            @Override
                            public int priority() {
                                return 1000;
                            }

                            @Override
                            public CommandPhase phase() {
                                return CommandPhase.START;
                            }
                        };
                    }
                    this.map.put(runnable, runAt);
                }
            } catch (NoSuchMethodException | SecurityException e) {
                this.container.getLogger().error("An error has occurred while attempting to gather the RunAt's for the CommandRunnable's!", e);
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

        // If the CommandState is disabled, inform and return empty
        if (!this.checkCommandState()) {
            source.sendMessage(Text.of(TextColors.RED, "This command is currently disabled."));
            return CommandResult.empty();
        }

        Optional<Text> isCorrectCommandSource = this.checkCommandSource(targetSource, source);
        if (isCorrectCommandSource.isPresent()) {
            source.sendMessage(isCorrectCommandSource.get());
            return CommandResult.empty();
        }

        // Execute any runnables set for 'START'
        Optional<CommandRunnableResult> checkRunnablesStart = this.checkPhase(CommandPhase.START, source, args);
        if (checkRunnablesStart.isPresent()) {
            return checkRunnablesStart.get().getResult();
        }

        // Check if our command is async. If so, then run it asynchronously
        if (commandBase.getClass().getAnnotation(RegisterCommand.class).async()) {
            // Create a synchronous task to be executed after the asynchronous
            // command has completed
            Task.Builder postTask = Sponge.getScheduler().createTaskBuilder().execute(
                    task -> {
                        this.checkPhase(CommandPhase.POST, source, args);
                    });

            Sponge.getScheduler().createTaskBuilder().execute(
                    task -> {
                        try {
                            commandBase.executeCommand(source, args);
                            postTask.submit(this.container.getPlugin());
                        } catch (CommandException e) {
                            source.sendMessage(e.getText());
                        } catch (Exception e2) {
                            source.sendMessage(Text.of(TextColors.RED, "An error has occurred while attempting to execute this command!"));
                            this.container.getLogger()
                                    .error("An exception has occurred while attempting to execute the command " + this.base.aliases()[0] + "!", e2);
                        }
                    }).async().submit(this.container.getPlugin());

            return CommandResult.success();
        }

        try {
            CommandResult result = commandBase.executeCommand(source, args);
            // Execute any runnables set for 'POST'. Note that the result is
            // effectively ignored.
            this.checkPhase(CommandPhase.POST, source, args);
            return result;
        } catch (CommandException e) {
            source.sendMessage(e.getText());
        } catch (Exception e2) {
            source.sendMessage(Text.of(TextColors.RED, "An error has occurred while attempting to execute this command."));
            this.container.getLogger().error("An exception has occurred while attempting to execute the command " + this.base.aliases()[0] + "!", e2);
        }
        // If errored
        return CommandResult.empty();
    }

    private Optional<Text> checkCommandSource(Class<?> type, CommandSource source) {
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

    private Optional<CommandRunnableResult> checkPhase(CommandPhase phase, CommandSource source, CommandContext args) {
        for (Map.Entry<CommandRunnable, RunAt> runnableEntry : this.map.entrySet()) {
            // Check if the runnable's phase is equal to the current phase
            if (runnableEntry.getValue().phase().equals(phase)) {
                // Run the runnable
                CommandRunnableResult result = runnableEntry.getKey().run(source, args);
                // If there is a result, return it so that we may stop the
                // command
                if (result.getResult() != null) {
                    return Optional.of(result);
                }
            }
        }
        // Either no runnables were found, or they all allowed the command to
        // continue running
        return Optional.empty();
    }

    private boolean checkCommandState() {
        for (CommandStore store : this.container.commandStores) {
            if (store.command().getClass().equals(this.base.getClass())) {
                return store.state().equals(CommandState.ENABLED);
            }
        }
        // Should never really happen, but if it does, then allow the command to
        // process anyway
        return true;
    }
}
