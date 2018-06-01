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

package io.github.narikiro.core;

import io.github.narikiro.api.Core;
import io.github.narikiro.api.command.base.CommandBase;
import io.github.narikiro.api.command.base.CommandBaseLite;
import io.github.narikiro.api.config.Config;
import io.github.narikiro.api.config.annotation.DoNotReload;
import io.github.narikiro.api.config.base.ConfigBase;
import io.github.narikiro.api.text.HelpPaginationHandler.ChildBehavior;
import io.github.narikiro.core.command.base.CommandBaseImpl;
import io.github.narikiro.core.command.base.CommandBaseLiteImpl;
import io.github.narikiro.core.config.base.ConfigBaseImpl;
import io.github.narikiro.core.internal.Internals;
import io.github.narikiro.core.internal.config.ConfigChecker;
import io.github.narikiro.core.util.PluginInfo;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(name = PluginInfo.NAME, id = PluginInfo.ID, version = PluginInfo.VERSION, authors = PluginInfo.AUTHORS, description = PluginInfo.VERSION, url = PluginInfo.WEBSITE)
public class XtraCore {

    // Provide initial services to Core.
    @Listener(order = Order.FIRST)
    public void onPreInit(GamePreInitializationEvent event) {
        this.provideImplementations();
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        CoreImpl.instance.createHelpPaginationBuilder(this.getClass()).childBehavior(ChildBehavior.IGNORE_PARENT).build();
    }

    // For automatic configuration reloading.
    @Listener
    public void onReload(GameReloadEvent event) {
        ConfigChecker.commandConfig();
        for (Config config : CoreImpl.instance.getConfigRegistry().getAllConfigs()) {
            // If there is no DoNotReload annotation, then reload.
            if (config.getClass().getAnnotation(DoNotReload.class) == null) {
                config.load();
            }
        }
    }

    private void provideImplementations() {
        try {
            FieldUtils.writeStaticField(CommandBase.class, "BASE", new CommandBaseImpl(), true);
            FieldUtils.writeStaticField(CommandBaseLite.class, "BASE", new CommandBaseLiteImpl(), true);
            FieldUtils.writeStaticField(ConfigBase.class, "BASE", new ConfigBaseImpl(), true);
            FieldUtils.writeStaticField(Core.class, "CORE", new CoreImpl(this), true);
        } catch (Exception e) {
            Internals.globalLogger.error("An error has occurred while attempting to set the static API fields!", e);
        }
    }
}
