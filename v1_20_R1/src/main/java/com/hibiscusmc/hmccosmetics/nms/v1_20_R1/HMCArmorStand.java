package com.hibiscusmc.hmccosmetics.nms.v1_20_R1;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import com.hibiscusmc.hmccosmetics.HMCCosmeticsPlugin;
import com.hibiscusmc.hmccosmetics.nms.PacketArmorStand;
import com.hibiscusmc.hmccosmetics.nms.PacketEquipment;
import com.hibiscusmc.hmccosmetics.util.InventoryUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HMCArmorStand extends PacketArmorStand {

    public HMCArmorStand(Set<Player> viewers, int entityId, Location location, PacketEquipment equipment, UUID uuid) {
        super(viewers, entityId, location, equipment, uuid);
    }

    @Override
    public void sendToViewers(Collection<? extends Player> viewers) {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        final PacketContainer spawnPacket = manager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        spawnPacket.getIntegers().write(0, this.getEntityId());
        spawnPacket.getUUIDs().write(0, this.getUuid());
        spawnPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        spawnPacket.getDoubles().write(0, this.getLocation().getX());
        spawnPacket.getDoubles().write(1, this.getLocation().getY());
        spawnPacket.getDoubles().write(2, this.getLocation().getZ());
        spawnPacket.getBytes().write(0, (byte) (this.getLocation().getYaw() * 256.0F / 360.0F));
        spawnPacket.getBytes().write(1, (byte) (this.getLocation().getPitch() * 256.0F / 360.0F));

        final PacketContainer metaContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        metaContainer.getIntegers().write(0, this.getEntityId());

        final List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
        wrappedDataValueList.add(new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) 0x20));
        metaContainer.getDataValueCollectionModifier().write(0, wrappedDataValueList);

        for (final Player viewer : viewers) {
            manager.sendServerPacket(viewer, spawnPacket);
            manager.sendServerPacket(viewer, metaContainer);
        }
        //Bukkit.getScheduler().runTaskLaterAsynchronously(HMCCosmeticsPlugin.getInstance(), this::sendEquipment, 1);
    }

    @Override
    public void sendEquipment() {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        final PacketContainer equipmentPacket = manager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipmentPacket.getIntegers().write(0, this.getEntityId());
        final List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairs = new ArrayList<>();
        for (final var entry : this.getEquipment().getEquipment().entrySet()) {
            pairs.add(new Pair<>(InventoryUtils.itemBukkitSlot(entry.getKey()), entry.getValue()));
        }
        equipmentPacket.getSlotStackPairLists().write(0, pairs);
        for (final Player viewer : this.getViewers()) {
            manager.sendServerPacket(viewer, equipmentPacket);
        }
    }

    @Override
    public void despawn(Collection<? extends Player> viewers) {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        final PacketContainer despawnPacket = manager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        final int[] toRemove = new int[this.getPassengers().size() + 1];
        int i = 0;
        for (final int passenger : this.getPassengers()) {
            toRemove[i++] = passenger;
        }
        toRemove[i] = this.getEntityId();
        despawnPacket.getModifier().write(0, new IntArrayList(toRemove));
        for (final Player viewer : viewers) {
            manager.sendServerPacket(viewer, despawnPacket);
        }
    }

    @Override
    public void teleport(Location location) {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        final PacketContainer teleportPacket = manager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportPacket.getIntegers().write(0, this.getEntityId());
        teleportPacket.getDoubles().write(0, location.getX());
        teleportPacket.getDoubles().write(1, location.getY());
        teleportPacket.getDoubles().write(2, location.getZ());
        teleportPacket.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
        teleportPacket.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
        teleportPacket.getBooleans().write(0, false);
        for (final Player viewer : this.getViewers()) {
            manager.sendServerPacket(viewer, teleportPacket);
        }
    }

    @Override
    public void sendRotation() {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        final PacketContainer rotationPacket = manager.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        rotationPacket.getIntegers().write(0, this.getEntityId());
        rotationPacket.getBytes().write(0, (byte) (this.getLocation().getYaw() * 256.0F / 360.0F));
        for (final Player viewer : this.getViewers()) {
            manager.sendServerPacket(viewer, rotationPacket);
        }
    }

    @Override
    public void sendRiding() {
        final Integer riding = this.getRiding();
        if (riding == null) return;
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        final PacketContainer mountPacket = manager.createPacket(PacketType.Play.Server.MOUNT);
        mountPacket.getIntegers().write(0, riding);
        final int[] toMount = new int[this.getPassengers().size() + 1];
        int i = 0;
        for (final int passenger : this.getPassengers()) {
            toMount[i++] = passenger;
        }
        toMount[i] = this.getEntityId();
        mountPacket.getIntegerArrays().write(0, toMount);
        for (final Player viewer : this.getViewers()) {
            manager.sendServerPacket(viewer, mountPacket);
        }
    }

}
