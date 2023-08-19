package com.hibiscusmc.hmccosmetics.nms;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PacketEquipment {

    private final Map<EquipmentSlot, ItemStack> equipment;

    private PacketEquipment(Map<EquipmentSlot, ItemStack> equipment) {
        this.equipment = equipment;
    }

    public static PacketEquipment create() {
        return new PacketEquipment(new HashMap<>());
    }

    @Unmodifiable
    public Map<EquipmentSlot, ItemStack> getEquipment() {
        return Collections.unmodifiableMap(this.equipment);
    }

    public @Nullable ItemStack get(EquipmentSlot slot) {
        return this.equipment.get(slot);
    }

    public void set(EquipmentSlot slot, @Nullable ItemStack item) {
        this.equipment.put(slot, item);
    }

    public boolean has(EquipmentSlot slot) {
        return this.equipment.containsKey(slot);
    }

    public void clear() {
        this.equipment.clear();
    }

}
