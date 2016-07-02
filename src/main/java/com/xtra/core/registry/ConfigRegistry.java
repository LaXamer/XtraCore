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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Maps;
import com.xtra.core.config.Config;
import com.xtra.core.config.annotation.RegisterConfig;
import com.xtra.core.internal.Internals;
import com.xtra.core.plugin.XtraCoreInternalPluginContainer;
import com.xtra.core.plugin.XtraCorePluginContainer;
import com.xtra.core.util.log.LogHandler;

public class ConfigRegistry {

    private static Map<Config, XtraCorePluginContainer> globalConfigs = new HashMap<>();

    public static void add(Config config, XtraCorePluginContainer container) {
        LogHandler.getGlobalLogger()
                .log("Adding config '" + config.getClass().getAnnotation(RegisterConfig.class).configName() + "' to the global config registry!");
        globalConfigs.put(config, container);
    }

    public static Optional<Config> getConfig(Class<? extends Config> clazz) {
        for (Config config : globalConfigs.keySet()) {
            if (config.getClass().equals(clazz)) {
                return Optional.of(config);
            }
        }
        return Optional.empty();
    }

    public static Optional<Map.Entry<Config, XtraCorePluginContainer>> getEntry(Class<? extends Config> clazz) {
        for (Map.Entry<Config, XtraCorePluginContainer> entry : globalConfigs.entrySet()) {
            if (entry.getKey().getClass().equals(clazz)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public static Optional<Map.Entry<XtraCorePluginContainer, XtraCoreInternalPluginContainer>>
            getContainersForConfig(Class<? extends Config> clazz) {
        for (Map.Entry<Config, XtraCorePluginContainer> entry : globalConfigs.entrySet()) {
            if (entry.getKey().getClass().equals(clazz)) {
                return Optional.of(Maps.immutableEntry(entry.getValue(), Internals.plugins.get(entry.getValue())));
            }
        }
        return Optional.empty();
    }

    public static Set<Config> getAllConfigs() {
        return globalConfigs.keySet();
    }

    public static Map<Config, XtraCorePluginContainer> getAllConfigMappings() {
        return globalConfigs;
    }
}
