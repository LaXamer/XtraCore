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

package com.xtra.core.entity;

import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import com.xtra.api.entity.EntityHandler;

/**
 * A class for convenient entity handling methods.
 */
public class EntityHandlerImpl implements EntityHandler {

    @Override
    public boolean spawnEntity(Location<World> loc, EntityType type, SpawnType spawnType) {
        Extent extent = loc.getExtent();
        Optional<Entity> optional = extent.createEntity(type, loc.getPosition());
        if (optional.isPresent()) {
            return extent.spawnEntity(optional.get(),
                    Cause.source(EntitySpawnCause.builder().entity(optional.get()).type(spawnType).build()).build());
        }
        return false;
    }

    @Override
    public boolean spawnItem(Location<World> loc, ItemType type, SpawnType spawnType, int quantity) {
        Extent extent = loc.getExtent();
        Optional<Entity> optional = extent.createEntity(EntityTypes.ITEM, loc.getPosition());
        if (optional.isPresent()) {
            Entity entity = optional.get();
            entity.offer(Keys.REPRESENTED_ITEM, ItemStack.of(type, quantity).createSnapshot());
            return extent.spawnEntity(entity, Cause.source(EntitySpawnCause.builder().entity(entity).type(spawnType).build()).build());
        }
        return false;
    }
}
