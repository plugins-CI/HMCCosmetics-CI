package com.hibiscusmc.hmccosmetics.util;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.hibiscusmc.hmccosmetics.config.Settings;
import me.lojosho.hibiscuscommons.util.packets.PacketManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HMCCPlayerUtils {

    @Nullable
    public static WrappedSignedProperty getSkin(Player player) {
        WrappedSignedProperty skinData = WrappedGameProfile.fromPlayer(player).getProperties()
                .get("textures").stream().findAny().orElse(null);

        if (skinData == null) {
            return null;
        }
        return new WrappedSignedProperty("textures", skinData.getValue(), skinData.getSignature());
    }

    @NotNull
    public static List<Player> getNearbyPlayers(@NotNull Player player) {
        return getNearbyPlayers(player.getLocation());
    }

    @NotNull
    public static List<Player> getNearbyPlayers(@NotNull Location location) {
        return PacketManager.getViewers(location, Settings.getViewDistance());
    }
}
