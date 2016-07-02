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

package com.xtra.core.config;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.xtra.core.plugin.XtraCoreInternalPluginContainer;
import com.xtra.core.plugin.XtraCorePluginContainer;
import com.xtra.core.plugin.XtraCorePluginHandler;
import com.xtra.core.registry.ConfigRegistry;
import com.xtra.core.util.log.LogHandler;

public class ConfigHandler {

    private Set<Config> configs = new HashSet<>();

    private ConfigHandler() {
    }

    /**
     * Creates and initializes a {@link ConfigHandler}.
     * 
     * @return The new config handler
     */
    public static ConfigHandler create(Object plugin) {
        return new ConfigHandler().init(XtraCorePluginHandler.getEntryContainerUnchecked(plugin));
    }

    private ConfigHandler init(Map.Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer> entry) {
        LogHandler.getGlobalLogger().log("======================================================");
        LogHandler.getGlobalLogger().log("Initializing config handler for " + entry.getKey().getPlugin().getClass().getName());
        entry.getKey().getLogger().log("======================================================");
        this.configs = entry.getValue().scanner.getConfigs();

        entry.getKey().getLogger().log("======================================================");
        entry.getKey().getLogger().log("Initializing the configs!");
        for (Config config : this.configs) {
            ConfigRegistry.add(config, entry.getKey());
            config.init();
        }
        entry.getValue().setConfigHandler(this);
        return this;
    }

    /**
     * Gets the specified config object.
     * 
     * @param clazz The class of the config
     * @return The config object
     */
    public Config getConfig(Class<? extends Config> clazz) {
        for (Config config : this.configs) {
            if (clazz.isInstance(config)) {
                return config;
            }
        }
        return null;
    }

    public Set<Config> getConfigs() {
        return this.configs;
    }
}
