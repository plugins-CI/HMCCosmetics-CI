package com.hibiscusmc.hmccosmetics.cosmetic.types;

import com.hibiscusmc.hmccosmetics.cosmetic.Cosmetic;
import com.hibiscusmc.hmccosmetics.nms.NMSHandlers;
import com.hibiscusmc.hmccosmetics.user.CosmeticUser;
import com.hibiscusmc.hmccosmetics.user.manager.UserBackpackManager;
import com.hibiscusmc.hmccosmetics.util.MessagesUtil;
import com.hibiscusmc.hmccosmetics.util.packets.PacketManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class CosmeticBackpackType extends Cosmetic {

    @Getter
    private final String modelName;
    @Getter
    private int height = -1;
    private ItemStack firstPersonBackpack;

    public CosmeticBackpackType(String id, ConfigurationNode config) {
        super(id, config);

        modelName = config.node("model").getString();

        if (!config.node("firstperson-item").virtual()) {
            this.firstPersonBackpack = generateItemStack(config.node("firstperson-item"));
            this.height = config.node("height").getInt(5);
        }
    }

    @Override
    public void update(@NotNull CosmeticUser user) {
        Entity entity = Bukkit.getEntity(user.getUniqueId());
        if (entity == null) return;

        Location loc = entity.getLocation().clone().add(0, 2, 0);

        if (user.isInWardrobe() || !user.isBackpackSpawned()) return;
        // This needs to be moved to purely packet based, there are far to many plugin doing dumb stuff that prevents spawning armorstands ignoring our spawn reason.
        if (!user.getUserBackpackManager().IsValidBackpackEntity()) {
            MessagesUtil.sendDebugMessages("Invalid Backpack Entity[owner=" + user.getUniqueId() + ",player_location=" + loc + "]!");
            user.respawnBackpack();
            return;
        }
        boolean firstPerson = false;
        ArrayList<Player> p = user.getUserBackpackManager().getArmorStand().refreshViewers();
        user.getUserBackpackManager().getArmorStand().sendToViewers(p);

        user.getUserBackpackManager().getArmorStand().teleport(loc);
        user.getUserBackpackManager().getArmorStand().setRotation(loc.getYaw(), loc.getPitch());

        List<Player> outsideViewers = user.getUserBackpackManager().getCloudManager().refreshViewers(loc);
        if (!user.isInWardrobe() && isFirstPersonCompadible() && user.getPlayer() != null) {
            firstPerson = true;
            List<Player> owner = List.of(user.getPlayer());

            ArrayList<Integer> particleCloud = user.getUserBackpackManager().getAreaEffectEntityId();
            for (int i = 0; i < particleCloud.size(); i++) {
                if (i == 0) {
                    PacketManager.sendRidingPacket(entity.getEntityId(), particleCloud.get(i), owner);
                } else {
                    PacketManager.sendRidingPacket(particleCloud.get(i - 1), particleCloud.get(i) , owner);
                }
            }
            PacketManager.sendRidingPacket(particleCloud.get(particleCloud.size() - 1), user.getUserBackpackManager().getFirstArmorStandId(), owner);
            MessagesUtil.sendDebugMessages("First Person Backpack Update[owner=" + user.getUniqueId() + ",player_location=" + loc + "]!", Level.INFO);
        }

        if (!user.getHidden()) {
            if (firstPerson) {
                if (user.getPlayer() != null) NMSHandlers.getHandler().equipmentSlotUpdate(user.getUserBackpackManager().getFirstArmorStandId(), EquipmentSlot.HEAD, user.getUserCosmeticItem(this, getFirstPersonBackpack()), List.of(user.getPlayer()));
                NMSHandlers.getHandler().equipmentSlotUpdate(user.getUserBackpackManager().getFirstArmorStandId(), EquipmentSlot.HEAD, user.getUserCosmeticItem(this), outsideViewers);
            } else {
                NMSHandlers.getHandler().equipmentSlotUpdate(user.getUserBackpackManager().getFirstArmorStandId(), EquipmentSlot.HEAD, user.getUserCosmeticItem(this), outsideViewers);
            }
        } else {
            NMSHandlers.getHandler().equipmentSlotUpdate(user.getUserBackpackManager().getFirstArmorStandId(), EquipmentSlot.HEAD, new ItemStack(Material.AIR), outsideViewers);
        }
        PacketManager.sendRidingPacket(entity.getEntityId(), user.getUserBackpackManager().getFirstArmorStandId(), outsideViewers);

        user.getUserBackpackManager().showBackpack();
    }

    public boolean isFirstPersonCompadible() {
        return firstPersonBackpack != null;
    }

    public ItemStack getFirstPersonBackpack() {
        return firstPersonBackpack;
    }
}
