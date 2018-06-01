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

package io.github.narikiro.core.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import io.github.narikiro.api.config.Config;
import io.github.narikiro.api.config.ConfigHandler;
import io.github.narikiro.api.config.annotation.RegisterConfig;
import io.github.narikiro.api.plugin.XtraCorePluginContainer;
import io.github.narikiro.core.CoreImpl;
import io.github.narikiro.core.internal.Internals;
import io.github.narikiro.core.plugin.XtraCorePluginContainerImpl;
import io.github.narikiro.core.registry.ConfigRegistryImpl;

public class ConfigHandlerImpl implements ConfigHandler {

    private Set<Config> configs = new HashSet<>();

    private ConfigHandlerImpl() {
    }

    public static ConfigHandler create(XtraCorePluginContainer container) {
        return new ConfigHandlerImpl().init(container);
    }

    private ConfigHandlerImpl init(XtraCorePluginContainer container) {
        Internals.globalLogger.info(Internals.LOG_HEADER);
        Internals.globalLogger.info("Initializing config handler for " + container.getPluginContainer().getName());
        XtraCorePluginContainerImpl implContainer = (XtraCorePluginContainerImpl) container;
        this.configs = implContainer.scanner.getConfigs();

        container.getLogger().info(Internals.LOG_HEADER);
        container.getLogger().info("Initializing the configs!");
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
        checkNotNull(clazz, "Config class cannot be null!");
        for (Config config : this.configs) {
            if (clazz.isInstance(config)) {
                return Optional.of(config);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Config> getConfig(String configName) {
        checkNotNull(configName, "Config name cannot be null!");
        for (Config config : this.configs) {
            if (config.getClass().getAnnotation(RegisterConfig.class).configName().equals(configName)) {
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
