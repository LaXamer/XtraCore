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

import java.nio.file.Path;
import java.nio.file.Paths;

import com.xtra.core.config.Config;
import com.xtra.core.config.annotation.RegisterConfig;
import com.xtra.core.internal.Internals;
import com.xtra.core.internal.config.InternalConfig;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

/**
 * A base class for a config implementation.
 */
public abstract class ConfigBase implements Config, InternalConfig {

    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode rootNode;

    @Override
    public void init() {
        RegisterConfig rc = this.getClass().getAnnotation(RegisterConfig.class);
        HoconConfigurationLoader.Builder loaderBuilder = HoconConfigurationLoader.builder();
        Path path;
        if (rc.sharedRoot()) {
            path = Paths.get("config/" + rc.configName() + ".conf");
            loaderBuilder.setPath(path);
        } else {
            path = Paths.get("config/" + Internals.pluginContainer.getId() + "/" + rc.configName() + ".conf");
            loaderBuilder.setPath(path);
        }
        loader = loaderBuilder.build();
        rootNode = loader.createEmptyNode();
    }

    @Override
    public ConfigurationLoader<CommentedConfigurationNode> loader() {
        return loader;
    }

    @Override
    public CommentedConfigurationNode rootNode() {
        return rootNode;
    }
}
