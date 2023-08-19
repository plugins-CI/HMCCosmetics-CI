package com.hibiscusmc.hmccosmetics.nms;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManager {

    private static EntityManager instance;
    private static final SplittableRandom RANDOM = new SplittableRandom();

    public static EntityManager getInstance() {
        if (instance == null) {
            instance = new EntityManager(new ConcurrentHashMap<>());
        }
        return instance;
    }

    private final Map<Integer, PacketEntity> entityMap;

    public EntityManager(Map<Integer, PacketEntity> entityMap) {
        this.entityMap = entityMap;
    }

    public void addPlayer(Player player) {
        this.entityMap.put(player.getEntityId(), new PacketPlayer(
                Collections.emptySet(),
                player.getEntityId(),
                player.getLocation(),
                PacketEquipment.create(),
                player.getUniqueId()
        ));
    }

    public void removePlayer(Player player) {
        this.entityMap.remove(player.getEntityId());
    }

    @Nullable
    public PacketEntity getPacketEntity(int entityId) {
        return this.entityMap.get(entityId);
    }

    public void addPacketEntity(PacketEntity packetEntity) {
        this.entityMap.put(packetEntity.getEntityId(), packetEntity);
    }

}