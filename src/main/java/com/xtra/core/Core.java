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

package com.xtra.core;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.api.text.Text;

import com.xtra.core.command.CommandRegistrar;
import com.xtra.core.command.base.CommandBase;
import com.xtra.core.text.HelpPaginationGen;
import com.xtra.core.util.ReflectionScanner;

public class Core {

    private static Object plugin;
    private static Set<CommandBase<?>> commands = new HashSet<>();

    /**
     * Initializes the base core class for function.
     * 
     * <p>CALL THIS BEFORE ATTEMPTING TO DO ANYTHING ELSE WITH XTRACORE OR
     * EVERYTHING WILL BREAK.</p>
     * 
     * @param plugin The plugin class
     * @return The core class
     */
    public static Core initialize(Object plugin) {
        Core.plugin = plugin;
        Core.commands = ReflectionScanner.getCommands(plugin);
        return new Core();
    }

    /**
     * Initializes the {@link CommandRegistrar}.
     * 
     * @return A command registrar
     * @see CommandRegistrar#create(Object)
     */
    public CommandRegistrar initializeCommandRegistrar() {
        return CommandRegistrar.create(this);
    }

    /**
     * Initializes the {@link HelpPaginationGen}.
     * 
     * @return A help pagination gen
     * @see HelpPaginationGen#create(Object)
     */
    public HelpPaginationGen initializeHelpPaginationGen() {
        return HelpPaginationGen.create(this);
    }

    /**
     * Initializes the {@link HelpPaginationGen}.
     * 
     * @param title The title of the pagination list
     * @return A help pagination gen
     * @see HelpPaginationGen#create(Object, Text)
     */
    public HelpPaginationGen initializeHelpPaginationGen(Text title) {
        return HelpPaginationGen.create(this);
    }

    /**
     * Initializes the {@link HelpPaginationGen}.
     * 
     * @param title The title of the pagination list
     * @param padding The padding of the pagination list
     * @return A help pagination gen
     * @see HelpPaginationGen#create(Object, Text, Text)
     */
    public HelpPaginationGen initializeHelpPaginationGen(Text title, Text padding) {
        return HelpPaginationGen.create(this);
    }

    /**
     * Gets the stored plugin object from the Core.
     * 
     * @return The plugin object
     */
    public static Object plugin() {
        return plugin;
    }

    /**
     * Gets the commands for this instance.
     * 
     * @return The commands
     */
    public static Set<CommandBase<?>> commands() {
        return commands;
    }
}
