package com.hibiscusmc.hmccosmetics.nms;

import com.hibiscusmc.hmccosmetics.util.PlayerUtils;
import com.hibiscusmc.hmccosmetics.util.packets.PacketManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public abstract class PacketEntity {

    private final Set<Player> viewers;
    private final int entityId;
    private Location location;
    private final PacketEquipment equipment;
    @Nullable
    private Integer riding;
    private final Set<Integer> passengers;
    @Nullable
    private Vector velocity;
    private Long lastUpdate = 0L;

    public PacketEntity(Set<Player> viewers, int entityId, Location location, PacketEquipment equipment) {
        this.viewers = viewers;
        this.entityId = entityId;
        this.location = location;
        this.equipment = equipment;
        this.passengers = new HashSet<>();
    }

    public void addViewers(Collection<? extends Player> viewers) {
        this.viewers.addAll(viewers);
        this.sendToViewers(viewers);
    }

    public void removeViewers(Collection<? extends Player> viewers) {
        this.viewers.removeAll(viewers);
        this.despawn(viewers);
    }

    public void sendToAll() {
        this.sendToViewers(this.viewers);
    }

    public boolean isViewing(Player player) {
        return this.viewers.contains(player);
    }

    @Unmodifiable
    public Set<Player> getViewers() {
        return Collections.unmodifiableSet(this.viewers);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public @Nullable Integer getRiding() {
        return this.riding;
    }

    public void setRiding(@Nullable Integer riding) {
        this.riding = riding;
        this.sendRiding();
    }

    public void addPassengers(Collection<Integer> passengers) {
        this.passengers.addAll(passengers);
    }

    public void removePassengers(Collection<Integer> passengers) {
        this.passengers.removeAll(passengers);
    }

    @Unmodifiable
    public Set<Integer> getPassengers() {
        return Collections.unmodifiableSet(this.passengers);
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public PacketEquipment getEquipment() {
        return this.equipment;
    }

    public void despawn() {
        this.despawn(this.viewers);
        this.viewers.clear();
    }

    @Nullable
    public Vector getVelocity() {
        return this.velocity;
    }

    public void setVelocity(@Nullable Vector velocity) {
        this.velocity = velocity;
    }

    public abstract void sendToViewers(Collection<? extends Player> viewers);

    public void despawn(Collection<? extends Player> viewers) {

    }

    public abstract void sendEquipment();

    public abstract void sendRiding();

    public abstract void teleport(Location location);

    public ArrayList<Player> refreshViewers() {
        return refreshViewers(getLocation());
    }

    public ArrayList<Player> refreshViewers(Location location) {
        if (System.currentTimeMillis() - lastUpdate <= 1000) return new ArrayList<>(); //Prevents mass refreshes
        ArrayList<Player> newPlayers = new ArrayList<>();
        ArrayList<Player> removePlayers = new ArrayList<>();
        List<Player> players = PlayerUtils.getNearbyPlayers(location);

        for (Player player : players) {
            if (!viewers.contains(player)) {
                viewers.add(player);
                newPlayers.add(player);
                continue;
            }
            // bad loopdy loops
            for (Player viewerPlayer : viewers) {
                if (!players.contains(viewerPlayer)) {
                    removePlayers.add(viewerPlayer);
                    PacketManager.sendEntityDestroyPacket(getEntityId(), List.of(viewerPlayer)); // prevents random leashes
                }
            }
        }
        addViewers(newPlayers);
        removeViewers(removePlayers);
        //viewers.removeAll(removePlayers);
        lastUpdate = System.currentTimeMillis();
        return newPlayers;
    }

}
