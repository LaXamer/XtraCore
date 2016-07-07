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

package com.xtra.core.world.direction;

import org.spongepowered.api.util.Direction;

import com.xtra.api.world.direction.DirectionHandler;

public class DirectionHandlerImpl implements DirectionHandler {

    @Override
    public Direction getCardinalDirectionFromYaw(double yaw) {
        if ((yaw >= 135 && yaw <= 225) || (yaw <= -135 && yaw >= -225)) {
            return Direction.NORTH;
        } else if ((yaw >= -135 && yaw <= -45) || (yaw >= 225 && yaw <= 315)) {
            return Direction.EAST;
        } else if ((yaw >= -45 && yaw <= 45) || yaw >= 315 || yaw <= -315) {
            return Direction.SOUTH;
        } else if ((yaw >= 45 && yaw <= 135) || (yaw <= -225 && yaw >= -315)) {
            return Direction.WEST;
        }
        return Direction.NONE;
    }

    @Override
    public Direction getCardinalLeft(Direction direction) {
        if (direction.equals(Direction.NORTH)) {
            return Direction.WEST;
        } else if (direction.equals(Direction.EAST)) {
            return Direction.NORTH;
        } else if (direction.equals(Direction.SOUTH)) {
            return Direction.EAST;
        } else if (direction.equals(Direction.WEST)) {
            return Direction.SOUTH;
        }
        return Direction.NONE;
    }

    @Override
    public Direction getCardinalRight(Direction direction) {
        if (direction.equals(Direction.NORTH)) {
            return Direction.EAST;
        } else if (direction.equals(Direction.EAST)) {
            return Direction.SOUTH;
        } else if (direction.equals(Direction.SOUTH)) {
            return Direction.WEST;
        } else if (direction.equals(Direction.WEST)) {
            return Direction.NORTH;
        }
        return Direction.NONE;
    }
}
