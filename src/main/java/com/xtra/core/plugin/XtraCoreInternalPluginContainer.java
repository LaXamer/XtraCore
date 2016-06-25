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

package com.xtra.core.plugin;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.xtra.core.command.Command;
import com.xtra.core.command.CommandHandler;
import com.xtra.core.command.runnable.CommandRunnable;
import com.xtra.core.command.runnable.CommandRunnableHandler;
import com.xtra.core.config.ConfigHandler;
import com.xtra.core.text.HelpPaginationHandler;
import com.xtra.core.util.ReflectionScanner;
import com.xtra.core.util.log.Logger;
import com.xtra.core.util.store.CommandStore;

public class XtraCoreInternalPluginContainer {

    public XtraCorePluginContainer container;
    public Set<CommandStore> commandStores = new HashSet<>();
    public Multimap<Class<? extends Command>, CommandRunnable> commandRunnables = ArrayListMultimap.create();
    public ReflectionScanner scanner;

    public XtraCoreInternalPluginContainer(XtraCorePluginContainer container) {
        this.container = container;
        this.scanner = ReflectionScanner.create(container);
    }

    public void setLogger(Logger logger) {
        this.container.logger = logger;
    }

    public void setCommandHandler(CommandHandler commandHandler) {
        this.container.commandHandler = commandHandler;
    }

    public void setConfigHandler(ConfigHandler configHandler) {
        this.container.configHandler = configHandler;
    }

    public void setCommandRunnableHandler(CommandRunnableHandler commandRunnableHandler) {
        this.container.commandRunnableHandler = commandRunnableHandler;
    }

    public void setHelpPaginationHandler(HelpPaginationHandler helpPaginationHandler) {
        this.container.helpPaginationHandler = helpPaginationHandler;
    }
}
