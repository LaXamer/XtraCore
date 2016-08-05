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

package com.xtra.core.config.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.xtra.api.config.annotation.RegisterConfig;
import com.xtra.api.config.base.ConfigBase;
import com.xtra.api.plugin.XtraCorePluginContainer;
import com.xtra.api.util.config.ConfigExecutor;
import com.xtra.api.util.config.ConfigStore;
import com.xtra.core.CoreImpl;
import com.xtra.core.internal.Internals;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

/**
 * A base class for a config implementation.
 */
public class ConfigBaseImpl implements ConfigExecutor {

    @Override
    public void init(ConfigBase base) {
        try {
            XtraCorePluginContainer container = CoreImpl.instance.getConfigRegistry().getEntry(base.getClass()).get().getValue();
            RegisterConfig rc = base.getClass().getAnnotation(RegisterConfig.class);

            container.getLogger().info("Initializing configuration for '" + rc.configName() + ".conf'.");

            HoconConfigurationLoader.Builder loaderBuilder = HoconConfigurationLoader.builder();
            loaderBuilder.setDefaultOptions(base.setOptions());
            Path dir;
            // The file is created automatically, however we need to know if we
            // need
            // to populate it or not
            boolean exists;
            if (rc.sharedRoot()) {
                dir = Paths.get(System.getProperty("user.dir"), "/config/");
                this.checkExists(dir);
            } else {
                dir = Paths.get(System.getProperty("user.dir"), "/config/" + container.getPluginContainer().getId());
                this.checkExists(dir);
            }
            Path configPath = dir.resolve(rc.configName() + ".conf");
            exists = Files.exists(configPath);
            loaderBuilder.setPath(configPath);
            ConfigurationLoader<CommentedConfigurationNode> loader = loaderBuilder.build();
            CommentedConfigurationNode rootNode;
            if (!exists) {
                container.getLogger().info("Configuration file '" + rc.configName() + "' currently does not exist. Creating...");
                Files.createFile(configPath);
                rootNode = loader.createEmptyNode();
                // Here we add the new, empty node and store it
                ConfigStore store = new ConfigStore(container, loader, rootNode, base);
                FieldUtils.writeField(base, "store", store, true);

                base.populate();
                this.save(store);
            } else {
                rootNode = loader.load();
                // Here we load the root node and store it
                ConfigStore store = new ConfigStore(container, loader, rootNode, base);
                FieldUtils.writeField(base, "store", store, true);
            }
        } catch (Exception e) {
            Internals.globalLogger.error("An exception has occurred while attempting to initialize a configuration base!", e);
        }
    }

    private void checkExists(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }

    @Override
    public void load(ConfigStore store) {
        try {
            store.rootNode = store.loader.load();
        } catch (IOException e) {
            store.entry.getLogger().error("An exception has occurred while attempting to load a configuration file!", e);
        }
    }

    @Override
    public void save(ConfigStore store) {
        try {
            store.loader.save(store.rootNode);
        } catch (IOException e) {
            store.entry.getLogger().error("An exception has occurred while attempting to save a configuration file!", e);
        }
    }

    @Override
    public ConfigurationLoader<CommentedConfigurationNode> loader(ConfigStore store) {
        return store.loader;
    }

    @Override
    public CommentedConfigurationNode rootNode(ConfigStore store) {
        return store.rootNode;
    }
}
