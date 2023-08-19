package com.hibiscusmc.hmccosmetics.nms;

import com.hibiscusmc.hmccosmetics.hooks.modelengine.MegEntityWrapper;
import com.hibiscusmc.hmccosmetics.hooks.modelengine.PacketBaseEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public abstract class PacketArmorStand extends PacketEntity {

    private final UUID uuid;
    private MegEntityWrapper<PacketArmorStand> megEntityWrapper;

    public PacketArmorStand(Set<Player> viewers, int entityId, Location location, PacketEquipment equipment, UUID uuid) {
        super(viewers, entityId, location, equipment);
        this.uuid = uuid;
    }

    public void setHelmet(@Nullable ItemStack helmet) {
        this.getEquipment().set(EquipmentSlot.HEAD, helmet);
        //this.sendEquipment();
    }

    public @Nullable ItemStack getHelmetStack() {
        return this.getEquipment().get(EquipmentSlot.HEAD);
    }

    public void setRotation(float yaw, float pitch) {
        this.getLocation().setYaw(yaw);
        this.getLocation().setPitch(pitch);
        this.sendRotation();
    }

    public MegEntityWrapper<PacketArmorStand> getMegEntityWrapper() {
        if (megEntityWrapper == null) {
            this.megEntityWrapper = new MegEntityWrapper<>(
                    new PacketBaseEntity<>(
                            this,
                            this.getEntityId(),
                            this.uuid
                    )
            );
        }
        return this.megEntityWrapper;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public abstract void sendRotation();

}
