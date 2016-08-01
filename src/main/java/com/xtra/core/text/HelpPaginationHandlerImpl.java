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

package com.xtra.core.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import com.xtra.api.command.Command;
import com.xtra.api.text.ContentEntry;
import com.xtra.api.text.HelpPaginationHandler;
import com.xtra.core.CoreImpl;
import com.xtra.core.event.XtraCoreHelpPaginationHandlerInitializedEventImpl;
import com.xtra.core.internal.Internals;
import com.xtra.core.plugin.XtraCorePluginContainerImpl;
import com.xtra.core.util.CommandHelper;
import com.xtra.core.util.store.CommandStore;

/**
 * A base class for creating {@link PaginationList}s for the commands of the
 * plugin. Use of this class is optional, however it is recommended if creating
 * a help list for your plugin.
 */
public class HelpPaginationHandlerImpl implements HelpPaginationHandler {

    private XtraCorePluginContainerImpl container;
    private CommandHelper helper;
    private PaginationList.Builder paginationBuilder;
    private Text title;
    private Text padding;
    private List<ContentEntry> contents;
    private List<Class<? extends Command>> ignoredCommands = new ArrayList<>();
    private TextColor commandColor;
    private TextColor descriptionColor;
    private ChildBehavior childBehavior;
    private CommandOrdering commandOrdering;
    // For the builder
    private HelpPaginationHandlerImpl instance;

    // For the builder
    public HelpPaginationHandlerImpl() {
    }

    @Override
    public PaginationList getList() {
        return this.paginationBuilder.build();
    }

    @Override
    public void sendList(MessageReceiver receiver) {
        this.paginationBuilder.sendTo(receiver);
    }

    @Override
    public TextColor getCommandColor() {
        return this.commandColor;
    }

    @Override
    public TextColor getDescriptionColor() {
        return this.descriptionColor;
    }

    @Override
    public Collection<Class<? extends Command>> getIgnoredCommands() {
        return this.ignoredCommands;
    }

    @Override
    public List<ContentEntry> getContents() {
        return this.contents;
    }

    @Override
    public PaginationList.Builder getPaginationBuilder() {
        return this.paginationBuilder;
    }

    private HelpPaginationHandlerImpl generateContents() {
        this.container.getLogger().info("Generating the contents for the help pagination list!");
        this.contents = new ArrayList<>();
        if (this.childBehavior == null) {
            this.childBehavior = ChildBehavior.BOTH;
        }
        if (this.commandOrdering == null) {
            this.commandOrdering = CommandOrdering.A_Z;
        }
        if (this.commandColor == null) {
            this.commandColor = TextColors.GOLD;
        }
        if (this.descriptionColor == null) {
            this.descriptionColor = TextColors.GREEN;
        }
        this.container.getLogger().info("Using settings:");
        this.container.getLogger().info("Child behavior: " + this.childBehavior);
        this.container.getLogger().info("Command ordering: " + this.commandOrdering);
        this.container.getLogger().info("Command color: " + this.commandColor.getName());
        this.container.getLogger().info("Description color: " + this.descriptionColor.getName());
        List<CommandStore> commandStores = this.helper.orderContents(this.container.commandStores, this.commandOrdering);
        for (CommandStore store : commandStores) {
            if (!this.ignoredCommands.contains(store.command().getClass())) {
                Command cmd = store.command();
                Command parentCommand = this.helper.getParentCommand(cmd);
                String commandString = null;
                if (this.childBehavior.equals(ChildBehavior.IGNORE_PARENT)) {
                    // If the child commands is empty, then this is not a
                    // parent command
                    if (this.helper.getChildCommands(store.command()).isEmpty()) {
                        if (parentCommand == null) {
                            commandString = "/" + cmd.aliases()[0];
                        } else {
                            commandString = "/" + parentCommand.aliases()[0] + " " + cmd.aliases()[0];
                        }
                    } else {
                        continue;
                    }
                } else if (this.childBehavior.equals(ChildBehavior.IGNORE_CHILD)) {
                    // If the parent is null, then there is no parent
                    // command. Therefore this command is not a child.
                    if (parentCommand == null) {
                        commandString = "/" + cmd.aliases()[0];
                    } else {
                        continue;
                    }
                } else if (this.childBehavior.equals(ChildBehavior.BOTH)) {
                    // Don't ignore anything
                    commandString = parentCommand != null ? "/" + parentCommand.aliases()[0] + " " + cmd.aliases()[0] : "/" + cmd.aliases()[0];
                }

                if (store.command().usage() != null) {
                    commandString += " " + store.command().usage();
                }
                this.container.getLogger().info("Adding command string: " + commandString);
                if (cmd.description() != null) {
                    this.container.getLogger().info("Adding command description: " + cmd.description());
                    this.contents
                            .add(new ContentEntryImpl(cmd, Text.of(this.commandColor, commandString, " - ", this.descriptionColor, cmd.description()),
                                    Text.of(this.commandColor, commandString), Text.of(this.descriptionColor, cmd.description())));
                } else {
                    this.contents.add(
                            new ContentEntryImpl(cmd, Text.of(this.commandColor, commandString), Text.of(this.commandColor, commandString), null));
                }
            }
        }
        List<Text> textContents = new ArrayList<>();
        for (ContentEntry entry : this.contents) {
            textContents.add(entry.getCompleteText());
        }
        this.paginationBuilder.contents(textContents);
        this.container.getLogger().info("Help pagination list contents generated!");
        return this;
    }

    private void setDefaults() {
        this.container.getLogger().info("Setting the pagination default values.");
        if (this.title != null) {
            this.paginationBuilder.title(this.title);
        } else {
            // Default to plugin name
            this.paginationBuilder.title(Text.of(TextColors.GOLD, this.container.getPluginContainer().getName()));
        }
        if (this.padding != null) {
            this.paginationBuilder.padding(padding);
        } else {
            this.paginationBuilder.padding(Text.of("-="));
        }
        this.commandColor = TextColors.GREEN;
        this.descriptionColor = TextColors.GOLD;
    }

    public class Builder implements HelpPaginationHandler.Builder {

        public Builder(HelpPaginationHandlerImpl impl, Class<?> clazz) {
            instance = impl;
            container = (XtraCorePluginContainerImpl) CoreImpl.instance.getPluginHandler().getContainerUnchecked(clazz);
        }

        @Override
        public HelpPaginationHandler.Builder commandColor(TextColor color) {
            commandColor = color;
            return this;
        }

        @Override
        public HelpPaginationHandler.Builder descriptionColor(TextColor color) {
            descriptionColor = color;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public HelpPaginationHandler.Builder ignoreCommands(Class<? extends Command>... commands) {
            ignoredCommands = Arrays.asList(commands);
            return this;
        }

        @Override
        public HelpPaginationHandler.Builder childBehavior(ChildBehavior behavior) {
            childBehavior = behavior;
            return this;
        }

        @Override
        public HelpPaginationHandler.Builder commandOrdering(CommandOrdering ordering) {
            commandOrdering = ordering;
            return this;
        }

        @Override
        public HelpPaginationHandler build() {
            Internals.globalLogger.info(Internals.LOG_HEADER);
            Internals.globalLogger.info("Initializing help pagination handler for " + container.getPluginContainer().getName());
            container.getLogger().info(Internals.LOG_HEADER);
            container.getLogger().info("Initializing the help pagination handler!");
            container.setHelpPaginationHandler(instance);
            instance.helper = new CommandHelper(instance.container);
            instance.paginationBuilder = PaginationList.builder();
            instance.generateContents();
            instance.setDefaults();
            Sponge.getEventManager().post(new XtraCoreHelpPaginationHandlerInitializedEventImpl(container, instance));
            return instance;
        }
    }
}
