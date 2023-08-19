package com.hibiscusmc.hmccosmetics.nms.v1_18_R2;

import com.hibiscusmc.hmccosmetics.config.Settings;
import com.hibiscusmc.hmccosmetics.cosmetic.CosmeticSlot;
import com.hibiscusmc.hmccosmetics.cosmetic.types.CosmeticArmorType;
import com.hibiscusmc.hmccosmetics.cosmetic.types.CosmeticBackpackType;
import com.hibiscusmc.hmccosmetics.cosmetic.types.CosmeticBalloonType;
import com.hibiscusmc.hmccosmetics.cosmetic.types.CosmeticMainhandType;
import com.hibiscusmc.hmccosmetics.hooks.modelengine.MegEntityWrapper;
import com.hibiscusmc.hmccosmetics.nms.EntityManager;
import com.hibiscusmc.hmccosmetics.nms.PacketEntity;
import com.hibiscusmc.hmccosmetics.nms.PacketEquipment;
import com.hibiscusmc.hmccosmetics.user.CosmeticUser;
import com.hibiscusmc.hmccosmetics.user.manager.UserBalloonManager;
import com.hibiscusmc.hmccosmetics.util.InventoryUtils;
import com.hibiscusmc.hmccosmetics.util.MessagesUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R2.scoreboard.CraftScoreboard;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class NMSHandler implements com.hibiscusmc.hmccosmetics.nms.NMSHandler {
    @Override
    public int getNextEntityId() {
        return net.minecraft.world.entity.Entity.nextEntityId();
    }

    @Override
    public PacketEntity getEntity(int entityId) {
        /*
        net.minecraft.world.entity.Entity entity = getNMSEntity(entityId);
        if (entity == null) return null;
        return entity.getBukkitEntity();
         */
        return EntityManager.getInstance().getPacketEntity(entityId);
    }
    /*
    private net.minecraft.world.entity.Entity getNMSEntity(int entityId) {
        for (ServerLevel world : ((CraftServer) Bukkit.getServer()).getHandle().getServer().getAllLevels()) {
            net.minecraft.world.entity.Entity entity = world.getEntity(entityId);
            if (entity == null) return null;
            return entity;
        }
        return null;
    }
     */

    @Override
    public HMCArmorStand getHMCArmorStand(Location loc) {
        return new HMCArmorStand(new HashSet<>(), this.getNextEntityId(), loc, PacketEquipment.create(), UUID.randomUUID());
    }

    @Override
    public <T extends PacketEntity> MegEntityWrapper<T> getMEGEntity(Location loc) {
        return (MegEntityWrapper<T>) new HMCArmorStand(
                new HashSet<>(),
                this.getNextEntityId(),
                loc,
                PacketEquipment.create(),
                UUID.randomUUID()
        ).getMegEntityWrapper();
    }

    @Override
    public HMCArmorStand spawnBackpack(
            CosmeticUser user,
            CosmeticBackpackType cosmeticBackpackType,
            Set<Player> viewers
    ) {
        final HMCArmorStand invisibleArmorStand = new HMCArmorStand(
                viewers,
                this.getNextEntityId(),
                user.getEntity().getLocation(),
                PacketEquipment.create(),
                UUID.randomUUID()
        );

        final ItemStack item = user.getUserCosmeticItem(cosmeticBackpackType);

        invisibleArmorStand.setHelmet(item);
        invisibleArmorStand.sendToAll();
//        ((CraftWorld) user.getEntity().getWorld()).getHandle().addFreshEntity(invisibleArmorstand, CreatureSpawnEvent.SpawnReason.CUSTOM);

        MessagesUtil.sendDebugMessages("spawnBackpack NMS");

        return invisibleArmorStand;
//        return invisibleArmorstand.getBukkitLivingEntity();
    }

    @Override
    public org.bukkit.entity.Entity spawnDisplayEntity(Location location, String text) {
        return null;
    }


    @Override
    public UserBalloonManager spawnBalloon(CosmeticUser user, CosmeticBalloonType cosmeticBalloonType) {
        org.bukkit.entity.Entity entity = user.getEntity();

        UserBalloonManager userBalloonManager1 = new UserBalloonManager(entity.getLocation());
        userBalloonManager1.getModelEntity().entity().setLocation(entity.getLocation().add(cosmeticBalloonType.getBalloonOffset()));

        userBalloonManager1.spawnModel(cosmeticBalloonType, user.getCosmeticColor(cosmeticBalloonType.getSlot()));
        userBalloonManager1.addPlayerToModel(user, cosmeticBalloonType, user.getCosmeticColor(cosmeticBalloonType.getSlot()));

        return userBalloonManager1;
    }

    @Override
    public void equipmentSlotUpdate(
            int entityId,
            CosmeticUser user,
            CosmeticSlot cosmeticSlot,
            List<Player> sendTo
    ) {

        EquipmentSlot nmsSlot = null;
        net.minecraft.world.item.ItemStack nmsItem = null;

        if (!(user.getCosmetic(cosmeticSlot) instanceof CosmeticArmorType)) {

            if (user.getCosmetic(cosmeticSlot) instanceof CosmeticMainhandType) {
                CosmeticMainhandType cosmeticMainhandType = (CosmeticMainhandType) user.getCosmetic(CosmeticSlot.MAINHAND);
                nmsItem = CraftItemStack.asNMSCopy(user.getUserCosmeticItem(cosmeticMainhandType));
            } else {
                nmsItem = CraftItemStack.asNMSCopy(user.getPlayer().getInventory().getItem(InventoryUtils.getEquipmentSlot(cosmeticSlot)));
            }

            nmsSlot = CraftEquipmentSlot.getNMS(InventoryUtils.getEquipmentSlot(cosmeticSlot));

            if (nmsSlot == null) return;

            Pair<EquipmentSlot, net.minecraft.world.item.ItemStack> pair = new Pair<>(nmsSlot, nmsItem);

            List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> pairs = Collections.singletonList(pair);

            ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(entityId, pairs);
            for (Player p : sendTo) sendPacket(p, packet);
            return;
        }
        CosmeticArmorType cosmeticArmor = (CosmeticArmorType) user.getCosmetic(cosmeticSlot);

        // Converting EquipmentSlot and ItemStack to NMS ones.
        nmsSlot = CraftEquipmentSlot.getNMS(cosmeticArmor.getEquipSlot());
        nmsItem = CraftItemStack.asNMSCopy(user.getUserCosmeticItem(cosmeticArmor));

        if (nmsSlot == null) return;

        Pair<EquipmentSlot, net.minecraft.world.item.ItemStack> pair = new Pair<>(nmsSlot, nmsItem);

        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> pairs = Collections.singletonList(pair);

        ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(entityId, pairs);
        for (Player p : sendTo) sendPacket(p, packet);
    }


    @Override
    public void equipmentSlotUpdate(
            int entityId,
            org.bukkit.inventory.EquipmentSlot slot,
            ItemStack item,
            List<Player> sendTo
    ) {

        EquipmentSlot nmsSlot = null;
        net.minecraft.world.item.ItemStack nmsItem = null;

        // Converting EquipmentSlot and ItemStack to NMS ones.
        nmsSlot = CraftEquipmentSlot.getNMS(slot);
        nmsItem = CraftItemStack.asNMSCopy(item);

        if (nmsSlot == null) return;

        Pair<EquipmentSlot, net.minecraft.world.item.ItemStack> pair = new Pair<>(nmsSlot, nmsItem);

        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> pairs = Collections.singletonList(pair);

        ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(entityId, pairs);
        for (Player p : sendTo) sendPacket(p, packet);
    }

    public void hideNPCName(Player player, String NPCName) {
        //Creating the team
        PlayerTeam team = new PlayerTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), NPCName);

        //Setting name visibility
        team.setNameTagVisibility(Team.Visibility.NEVER);

        //Remove the Team (i assume so if it exists)
        ClientboundSetPlayerTeamPacket removeTeamPacket = ClientboundSetPlayerTeamPacket.createRemovePacket(team);
        sendPacket(player, removeTeamPacket);
        //Creating the Team
        ClientboundSetPlayerTeamPacket createTeamPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);
        sendPacket(player, createTeamPacket);
        //Adding players to the team (You have to use the NPC's name, and add it to a list)
        ClientboundSetPlayerTeamPacket createPlayerTeamPacket = ClientboundSetPlayerTeamPacket.createMultiplePlayerPacket(team, new ArrayList<String>() {{
            add(NPCName);
        }}, ClientboundSetPlayerTeamPacket.Action.ADD);
        sendPacket(player, createPlayerTeamPacket);
    }

    public void sendPacket(Player player, Packet packet) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        ServerPlayerConnection connection = serverPlayer.connection;
        connection.send(packet);
    }

    @Override
    public boolean getSupported() {
        return true;
    }
}
