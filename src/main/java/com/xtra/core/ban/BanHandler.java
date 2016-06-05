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

package com.xtra.core.ban;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanTypes;

/**
 * Convenience class for banning players.
 */
public class BanHandler {

    /**
     * Bans a specified profile.
     * 
     * @param profile The profile to ban
     */
    public static void banProfile(GameProfile profile) {
        BanService service = Sponge.getServiceManager().provide(BanService.class).get();
        service.addBan(Ban.of(profile));
    }

    /**
     * Bans a specified profile with an inputed reason. The reason is
     * automatically deserialized.
     * 
     * @param profile The profile to ban
     * @param reason The raw reason for banning
     */
    public static void banProfile(GameProfile profile, String reason) {
        banProfile(profile, TextSerializers.FORMATTING_CODE.deserialize(reason));
    }

    /**
     * Bans a specified profile with a text reason.
     * 
     * @param profile The profile to ban
     * @param reason The reason for banning
     */
    public static void banProfile(GameProfile profile, Text reason) {
        BanService service = Sponge.getServiceManager().provide(BanService.class).get();
        service.addBan(Ban.of(profile, reason));
    }

    /**
     * Ip bans the specified player.
     * 
     * @param player The player to ip ban
     */
    public static void banPlayerIp(Player player) {
        BanService service = Sponge.getServiceManager().provide(BanService.class).get();
        service.addBan(Ban.builder().type(BanTypes.IP).address(player.getConnection().getAddress().getAddress()).build());
    }

    /**
     * Ip bans a specified player with an inputed reason. The reason is
     * automatically deserialized.
     * 
     * @param player The player to ip ban
     * @param reason The raw reason for banning
     */
    public static void banPlayerIp(Player player, String reason) {
        banPlayerIp(player, TextSerializers.FORMATTING_CODE.deserialize(reason));
    }

    /**
     * Ip bans a specified player with a text reason.
     * 
     * @param player The player to ip ban
     * @param reason The reason for banning
     */
    public static void banPlayerIp(Player player, Text reason) {
        BanService service = Sponge.getServiceManager().provide(BanService.class).get();
        service.addBan(Ban.builder().type(BanTypes.IP).address(player.getConnection().getAddress().getAddress()).reason(reason).build());
    }

    /**
     * Gets the ban reason for the specified profile. Will return null if one
     * does not exist.
     * 
     * @param profile The profile to get the ban reason from
     * @return The reason, null if one is not available
     */
    public static Optional<Text> getBanReason(GameProfile profile) {
        BanService service = Sponge.getServiceManager().provide(BanService.class).get();
        Optional<Ban.Profile> optionalBan = service.getBanFor(profile);
        if (optionalBan.isPresent()) {
            return optionalBan.get().getReason();
        }
        return Optional.empty();
    }
}
