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
import java.nio.file.Path;
import java.nio.file.Paths;

import com.xtra.core.config.Config;
import com.xtra.core.config.annotation.RegisterConfig;
import com.xtra.core.internal.Internals;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

/**
 * A base class for a config implementation.
 */
public abstract class ConfigBase implements Config {

    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode rootNode;

    @Override
    public void init() {
        RegisterConfig rc = this.getClass().getAnnotation(RegisterConfig.class);
        HoconConfigurationLoader.Builder loaderBuilder = HoconConfigurationLoader.builder();
        Path path;
        // The file is created automatically, however we need to know if we need
        // to populate it or not
        boolean exists;
        if (rc.sharedRoot()) {
            path = Paths.get("config/" + rc.configName() + ".conf");
        } else {
            path = Paths.get("config/" + Internals.pluginContainer.getId() + "/" + rc.configName() + ".conf");
        }
        exists = path.toFile().exists();
        loaderBuilder.setPath(path);
        this.loader = loaderBuilder.build();
        this.rootNode = loader.createEmptyNode();
        if (!exists) {
            this.populate();
        }
        this.save();
        this.load();
    }

    @Override
    public void load() {
        try {
            rootNode = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ConfigurationLoader<CommentedConfigurationNode> loader() {
        return this.loader;
    }

    @Override
    public CommentedConfigurationNode rootNode() {
        return this.rootNode;
    }
}
