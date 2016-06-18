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
import java.util.List;

import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import com.xtra.core.command.Command;
import com.xtra.core.internal.InternalHandler;
import com.xtra.core.internal.Internals;
import com.xtra.core.util.CommandHelper;
import com.xtra.core.util.ReflectionScanner;
import com.xtra.core.util.store.CommandStore;

/**
 * A base class for creating {@link PaginationList}s for the commands of the
 * plugin. Use of this class is optional, however it is recommended if creating
 * a help list for your plugin.
 */
public class HelpPaginationHandler extends InternalHandler {

    private PaginationList.Builder paginationBuilder;
    private Text title;
    private Text padding;
    private List<Text> contents;
    private TextColor commandColor;
    private TextColor descriptionColor;
    private ChildBehavior childBehavior;
    private CommandOrdering commandOrdering;

    private HelpPaginationHandler() {
    }

    /**
     * Creates a basis class for generating a {@link PaginationList} for the
     * plugin's commands help list. Further configuration is provided through
     * {@link HelpPaginationHandler#paginationBuilder()}.
     */
    public static HelpPaginationHandler create() {
        return new HelpPaginationHandler().init();
    }

    /**
     * Allows generating a {@link PaginationList} with a title.
     * 
     * @param plugin The plugin
     * @param title The title
     */
    public static HelpPaginationHandler create(Text title) {
        HelpPaginationHandler gen = new HelpPaginationHandler();
        gen.title = title;
        return gen.init();
    }

    /**
     * Allows generating a {@link PaginationList} with a title and padding.
     * 
     * @param plugin The plugin
     * @param title The title
     * @param padding The padding
     */
    public static HelpPaginationHandler create(Text title, Text padding) {
        HelpPaginationHandler gen = new HelpPaginationHandler();
        gen.title = title;
        gen.padding = padding;
        return gen.init();
    }

    private HelpPaginationHandler init() {
        this.checkHasCoreInitialized();

        Internals.logger.log("Initializing the help pagination generation!");
        this.paginationBuilder = PaginationList.builder();
        this.setDefaults();
        return this;
    }

    /**
     * Generates the complete list and returns it.
     * 
     * @return The pagination list
     */
    public PaginationList generateList() {
        if (this.contents == null) {
            this.generateContents();
        }
        return this.paginationBuilder.build();
    }

    /**
     * Generate the complete list and send it to the specified receiver.
     * 
     * @param receiver The receiver to send this list to
     */
    public void generateList(MessageReceiver receiver) {
        if (this.contents == null) {
            this.generateContents();
        }
        this.paginationBuilder.sendTo(receiver);
    }

    /**
     * Sets it so that, when the contents are generated, the color part of the
     * content of a command comes out this specified {@link TextColor}.
     * 
     * <p>Ex: /my-command along with the dash (-) after it in the pagination
     * list will come out yellow if {@code TextColors.YELLOW} is specified.</p>
     * 
     * <p>If this method is not called, then the command will default to
     * green.</p>
     * 
     * @param color The color to set this to
     * @return The object, for chaining
     */
    public HelpPaginationHandler setCommandColor(TextColor color) {
        this.commandColor = color;
        return this;
    }

    /**
     * Sets it so that, when the contents are generated, the color part of the
     * content of a description comes out this specified {@link TextColor}.
     * 
     * <p>Ex: If the description states 'My command description', then the color
     * specified here will come out in the description of a command in the help
     * pagination list.</p>
     * 
     * <p>If this method is not called, then the description will default to
     * gold.</p>
     * 
     * @param color The color to set this to
     * @return The object, for chaining
     */
    public HelpPaginationHandler setDescriptionColor(TextColor color) {
        this.descriptionColor = color;
        return this;
    }

    /**
     * Specifies if a specific command should be ignored in the help list. Use
     * this if you want better control over what goes into the help list besides
     * what {@link ChildBehavior} can offer.
     * 
     * @param cmd The command to be ignored in the help list
     * @return The object, for chaining
     */
    public <T extends Command> HelpPaginationHandler specifyCommandShouldBeIgnored(Class<T> clazz) {
        // We still need the command store, so don't bother calling
        // CommandHelper#getEquivalentCommand
        for (CommandStore store : Internals.commandStores) {
            if (clazz.isInstance(store.command())) {
                Internals.logger.log("Command " + store.command().aliases()[0] + " will be ignored!");
                store.setIgnore(true);
            }
        }
        return this;
    }

    /**
     * Specifies if a group of commands should be ignored in the help list. Use
     * this if you want better control over what goes into the help list besides
     * what {@link ChildBehavior} can offer.
     * 
     * @param cmds The commands to be ignored
     * @return The object, for chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends Command> HelpPaginationHandler specifyCommandShouldBeIgnored(Class<T>... clazz) {
        for (Class<T> cmd : clazz) {
            // As long as this cmd class is a real command
            if (CommandHelper.getEquivalentCommand(cmd) != null) {
                this.specifyCommandShouldBeIgnored(cmd);
            }
        }
        return this;
    }

    /**
     * Specifies the behavior of child commands in the help list. See
     * {@link ChildBehavior} for more information.
     * 
     * @param childBehavior The child behavior
     * @return The object, for chaining
     */
    public HelpPaginationHandler specifyChildBehavior(ChildBehavior childBehavior) {
        this.childBehavior = childBehavior;
        return this;
    }

    /**
     * Specifies how commands should be ordered in the help list. See
     * {@link CommandOrdering} for more information.
     * 
     * @param commandOrdering The command ordering
     * @return The object, for chaining
     */
    public HelpPaginationHandler specifyCommandOrdering(CommandOrdering commandOrdering) {
        this.commandOrdering = commandOrdering;
        return this;
    }

    /**
     * Returns the pagination builder for more custom handling if desired.
     * 
     * @return The pagination builder
     */
    public PaginationList.Builder paginationBuilder() {
        return this.paginationBuilder;
    }

    /**
     * Gets the default list of contents for this pagination list. Use this for
     * modification of the contents if necessary.
     * 
     * @return The default contents
     */
    public List<Text> contents() {
        return this.contents;
    }

    /**
     * A default implementation of generating the contents for the pagination
     * list. This method should usually suffice most plugins, unless custom
     * handling over the contents is desired.
     * 
     * <p>This method is called by XtraCore automatically, however you may call
     * it yourself if you wish to refresh the contents after making changes
     * later in the plugin cycle.</p>
     * 
     * @return The object, for chaining
     */
    public HelpPaginationHandler generateContents() {
        Internals.logger.log("Generating the contents for the help pagination list!");
        this.contents = new ArrayList<>();
        if (this.childBehavior == null) {
            this.childBehavior = ChildBehavior.BOTH;
        }
        if (this.commandOrdering == null) {
            this.commandOrdering = CommandOrdering.A_Z;
        }
        Internals.logger.log("Using settings:");
        Internals.logger.log("Child behavior: " + this.childBehavior);
        Internals.logger.log("Command ordering: " + this.commandOrdering);
        Internals.logger.log("Command color: " + this.commandColor.getName());
        Internals.logger.log("Description color: " + this.descriptionColor.getName());
        // Don't override the commands in Internals, just store our own
        List<CommandStore> commandStores = CommandHelper.orderContents(Internals.commandStores, this.commandOrdering);
        for (CommandStore command : commandStores) {
            if (!command.ignore()) {
                Command cmd = command.command();
                Command parentCommand = CommandHelper.getParentCommand(cmd);
                String commandString = null;
                switch (this.childBehavior) {
                    case IGNORE_PARENT:
                        // If the child commands is empty, then this is not a
                        // parent command
                        if (CommandHelper.getChildCommands(command.command()).isEmpty()) {
                            commandString = "/" + cmd.aliases()[0];
                        }
                        break;
                    case IGNORE_CHILD:
                        // If the parent is null, then there is no parent
                        // command. Therefore this command is not a child.
                        if (parentCommand == null) {
                            commandString = "/" + cmd.aliases()[0];
                        }
                        break;
                    case BOTH:
                        // Don't ignore anything
                        commandString = parentCommand != null ? "/" + parentCommand.aliases()[0] + " " + cmd.aliases()[0] : "/" + cmd.aliases()[0];
                        break;
                }
                TextColor commandColor = this.commandColor != null ? this.commandColor : TextColors.GREEN;
                TextColor descriptionColor = this.descriptionColor != null ? this.descriptionColor : TextColors.GOLD;
                Internals.logger.log("Adding command string: " + commandString);
                Internals.logger.log("Adding command description: " + cmd.description());
                this.contents.add(Text.of(commandColor, commandString, " - ", descriptionColor, cmd.description()));
            }
        }
        this.paginationBuilder.contents(this.contents);
        Internals.logger.log("Help pagination list contents generated!");
        return this;
    }

    /**
     * Set the defaults if ones were not provided, or use the ones that were
     * provided. These can be overridden through the pagination builder itself
     * {@link HelpPaginationHandler#paginationBuilder()}.
     */
    private void setDefaults() {
        Internals.logger.log("Setting the help pagination gen default values.");
        if (this.title != null) {
            this.paginationBuilder.title(this.title);
        } else {
            // Default to plugin name
            this.paginationBuilder.title(Text.of(TextColors.GOLD, Internals.pluginContainer.getName()));
        }
        if (this.padding != null) {
            this.paginationBuilder.padding(padding);
        } else {
            this.paginationBuilder.padding(Text.of("-="));
        }
        this.commandColor = TextColors.GREEN;
        this.descriptionColor = TextColors.GOLD;
    }

    /**
     * How the pagination list should treat child commands when registering
     * commands to the help list.
     */
    public enum ChildBehavior {

        /*
         * If child commands should be ignored in the help list.
         */
        IGNORE_CHILD,

        /**
         * If parent commands should be ignored in the help list.
         */
        IGNORE_PARENT,

        /**
         * If both should exist in the help list.
         */
        BOTH;
    }

    /**
     * How commands should be ordered in the help list.
     */
    public enum CommandOrdering {

        /**
         * Commands will be ordered alphabetically (a-z) in the help list.
         */
        A_Z,

        /**
         * Commands will be ordered alphabetically backwards (z-a) in the help
         * list.
         */
        Z_A,

        /**
         * Commands will be ordered with parent commands coming first and then
         * ordered alphabetically (a-z) in the help list.
         */
        PARENT_COMMANDS_FIRST_A_Z,

        /**
         * Commands will be ordered with parent commands coming first and then
         * ordered alphabetically backwards (z-a) in the help list.
         */
        PARENT_COMMANDS_FIRST_Z_A,

        /**
         * Commands will be ordered with child commands coming first and then
         * ordered alphabetically (a-z) in the help list.
         */
        CHILD_COMMANDS_FIRST_A_Z,

        /**
         * Commands will be ordered with child commands coming first and then
         * ordered alphabetically backwards (z-a) in the help list.
         */
        CHILD_COMMANDS_FIRST_Z_A,

        /**
         * The parent and their child commands will be grouped together first,
         * followed by other parent commands and their children. At the end is
         * an alphabetically sorted (a-z) list of non-parent and non-child
         * commands.
         */
        PARENT_AND_CHILD_FIRST_NON_LAST_A_Z,

        /**
         * The parent and their child commands will be grouped together first,
         * followed by other parent commands and their children. At the end is
         * an alphabetically backwards sorted (z-a) list of non-parent and
         * non-child commands.
         */
        PARENT_AND_CHILD_FIRST_NON_LAST_Z_A,

        /**
         * Commands will be ordered by how the {@link ReflectionScanner} reads
         * them. Note that this ordering might not be the same every time the
         * commands are read.
         */
        DEFAULT;
    }
}
