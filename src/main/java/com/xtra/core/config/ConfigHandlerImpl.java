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
import java.util.Optional;
import java.util.Set;

import com.xtra.api.Core;
import com.xtra.api.config.Config;
import com.xtra.api.config.ConfigHandler;
import com.xtra.api.plugin.XtraCorePluginContainer;
import com.xtra.core.CoreImpl;
import com.xtra.core.internal.Internals;
import com.xtra.core.plugin.XtraCorePluginContainerImpl;
import com.xtra.core.registry.ConfigRegistryImpl;

public class ConfigHandlerImpl implements ConfigHandler {

    private Set<Config> configs = new HashSet<>();

    private ConfigHandlerImpl() {
    }

    public static ConfigHandler create(Class<?> clazz) {
        return new ConfigHandlerImpl().init(Core.getPluginHandler().getContainerUnchecked(clazz));
    }

    private ConfigHandlerImpl init(XtraCorePluginContainer container) {
        Internals.globalLogger.log("======================================================");
        Internals.globalLogger.log("Initializing config handler for " + container.getPluginContainer().getName());
        container.getLogger().log("======================================================");
        XtraCorePluginContainerImpl implContainer = (XtraCorePluginContainerImpl) container;
        this.configs = implContainer.scanner.getConfigs();

        container.getLogger().log("======================================================");
        container.getLogger().log("Initializing the configs!");
        implContainer.setConfigHandler(this);
        ConfigRegistryImpl implRegistry = (ConfigRegistryImpl) CoreImpl.instance.getConfigRegistry();
        for (Config config : this.configs) {
            implRegistry.add(config, container);
            config.init();
        }
        return this;
    }

    @Override
    public Optional<Config> getConfig(Class<? extends Config> clazz) {
        for (Config config : this.configs) {
            if (clazz.isInstance(config)) {
                return Optional.of(config);
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Config> getConfigs() {
        return this.configs;
    }
}
