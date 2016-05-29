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
import java.util.Set;

import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;

import com.xtra.core.Core;
import com.xtra.core.command.Command;
import com.xtra.core.command.annotation.RegisterCommand;
import com.xtra.core.command.base.CommandBase;
import com.xtra.core.command.base.EmptyCommand;
import com.xtra.core.util.ReflectionScanner;

/**
 * A base class for creating {@link PaginationList}s for the commands of the
 * plugin.
 */
public class HelpPaginationGen {

    private Object plugin;
    private PaginationList.Builder paginationBuilder;
    private Text title;
    private Text padding;
    private Set<CommandBase<?>> commands;
    private List<Text> contents;

    /**
     * Creates a basis class for generating a {@link PaginationList} for the
     * plugin's commands help list. Further configuration is provided through
     * {@link HelpPaginationGen#paginationBuilder()}.
     * 
     * @param plugin The plugin
     */
    public HelpPaginationGen() {
        this.plugin = Core.plugin();
    }

    /**
     * Allows generating a {@link PaginationList} with a title.
     * 
     * @param plugin The plugin
     * @param title The title
     */
    public HelpPaginationGen(Text title) {
        this.plugin = Core.plugin();
        this.title = title;
    }

    /**
     * Allows generating a {@link PaginationList} with a title and padding.
     * 
     * @param plugin The plugin
     * @param title The title
     * @param padding The padding
     */
    public HelpPaginationGen(Text title, Text padding) {
        this.plugin = Core.plugin();
        this.title = title;
        this.padding = padding;
    }

    /**
     * Initializes the basis for creation of a {@link PaginationList}. Call this
     * method before any others in {@link HelpPaginationGen}.
     */
    public void initializeList() {
        commands = ReflectionScanner.getCommands(plugin);
        paginationBuilder = PaginationList.builder();
        generateContents();
    }

    /**
     * Generates the complete list and returns it.
     * 
     * @return The pagination list
     */
    public PaginationList generateList() {
        setDefaults();
        return paginationBuilder.build();
    }

    /**
     * Generate the complete list and send it to the specified receiver.
     * 
     * @param receiver The receiver to send this list to
     */
    public void generateList(MessageReceiver receiver) {
        setDefaults();
        paginationBuilder.sendTo(receiver);
    }

    /**
     * A default implementation of generating the contents for the pagination
     * list. This method should usually suffice most plugins, unless custom
     * handling over the contents is desired.
     */
    private void generateContents() {
        contents = new ArrayList<>();
        for (CommandBase<?> command : commands) {
            String parentCommandAlias = null;
            try {
                Class<? extends Command> parentCommand = command.getClass().getAnnotation(RegisterCommand.class).childOf();
                Command parentCommand2 = parentCommand.newInstance();
                if (!(parentCommand2 instanceof EmptyCommand)) {
                    parentCommandAlias = parentCommand2.aliases()[0];
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            String commandString = parentCommandAlias != null ? "/" + parentCommandAlias + " " + command.aliases()[0] : "/" + command.aliases()[0];
            contents.add(Text.of(TextColors.GREEN, commandString, " - ", TextColors.GOLD, command.description()));
        }
        paginationBuilder.contents(contents);
    }

    /**
     * Set the defaults if ones were not provided, or use the ones that were
     * provided.
     */
    private void setDefaults() {
        if (title != null) {
            paginationBuilder.title(title);
        } else {
            // Default, dev was too lazy to set their own
            paginationBuilder.title(Text.of(TextColors.GOLD, "Command List"));
        }
        if (padding != null) {
            paginationBuilder.padding(padding);
        } else {
            // Same here
            paginationBuilder.padding(Text.of("-="));
        }
    }

    /**
     * Returns a list of the commands if more custom handling of the contents of
     * the pagination list is desired. Using these, you may retrieve information
     * about the commands, such as their aliases or descriptions.
     * 
     * @return The commands
     */
    public Set<CommandBase<?>> commands() {
        return commands;
    }

    /**
     * Returns the pagination builder for more custom handling if desired.
     * 
     * @return The pagination builder
     */
    public PaginationList.Builder paginationBuilder() {
        return paginationBuilder;
    }

    /**
     * Gets the default list of contents for this pagination list. Use this for
     * modification of the contents if necessary.
     * 
     * @return The default contents
     */
    public List<Text> contents() {
        return contents;
    }
}
