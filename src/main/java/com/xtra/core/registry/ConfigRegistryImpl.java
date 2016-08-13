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

package com.xtra.core.registry;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.xtra.api.config.Config;
import com.xtra.api.config.annotation.RegisterConfig;
import com.xtra.api.plugin.XtraCorePluginContainer;
import com.xtra.api.registry.ConfigRegistry;
import com.xtra.core.internal.Internals;

public class ConfigRegistryImpl implements ConfigRegistry {

    private Map<Config, XtraCorePluginContainer> globalConfigs = new HashMap<>();

    public void add(Config config, XtraCorePluginContainer container) {
        Internals.globalLogger
                .info("Adding config '" + config.getClass().getAnnotation(RegisterConfig.class).configName() + "' to the global config registry!");
        this.globalConfigs.put(config, container);
    }

    @Override
    public Optional<Config> getConfig(Class<? extends Config> clazz) {
        checkNotNull(clazz, "Config class cannot be null!");
        for (Config config : this.globalConfigs.keySet()) {
            if (config.getClass().equals(clazz)) {
                return Optional.of(config);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Config> getConfig(String configName) {
        checkNotNull(configName, "Config name cannot be null!");
        for (Config config : this.globalConfigs.keySet()) {
            if (config.getClass().getAnnotation(RegisterConfig.class).configName().equals(configName)) {
                return Optional.of(config);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Map.Entry<Config, XtraCorePluginContainer>> getEntry(Class<? extends Config> clazz) {
        checkNotNull(clazz, "Config class cannot be null!");
        for (Map.Entry<Config, XtraCorePluginContainer> entry : this.globalConfigs.entrySet()) {
            if (entry.getKey().getClass().equals(clazz)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Map.Entry<Config, XtraCorePluginContainer>> getEntry(String configName) {
        checkNotNull(configName, "Config name cannot be null!");
        for (Map.Entry<Config, XtraCorePluginContainer> entry : this.globalConfigs.entrySet()) {
            if (entry.getKey().getClass().getAnnotation(RegisterConfig.class).configName().equals(configName)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Config> getAllConfigs() {
        return this.globalConfigs.keySet();
    }

    @Override
    public Map<Config, XtraCorePluginContainer> getAllConfigMappings() {
        return this.globalConfigs;
    }
}
