package com.hibiscusmc.hmccosmetics.nms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class PacketPlayer extends PacketEntity {

    private final UUID uuid;

    public PacketPlayer(Set<Player> viewers, int entityId, Location location, PacketEquipment equipment, UUID uuid) {
        super(viewers, entityId, location, equipment);
        this.uuid = uuid;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public void sendToViewers(Collection<? extends Player> viewers) {

    }

    @Override
    public void despawn(Collection<? extends Player> viewers) {

    }

    @Override
    public void sendEquipment() {

    }

    @Override
    public void teleport(Location location) {

    }

    @Override
    public void sendRiding() {

    }

}
