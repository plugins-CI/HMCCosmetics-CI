package com.hibiscusmc.hmccosmetics.hooks.modelengine;

import com.hibiscusmc.hmccosmetics.nms.PacketEntity;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.generator.Hitbox;
import com.ticxo.modelengine.api.model.IModel;
import com.ticxo.modelengine.api.nms.entity.impl.DefaultBodyRotationController;
import com.ticxo.modelengine.api.nms.entity.impl.EmptyLookController;
import com.ticxo.modelengine.api.nms.entity.impl.EmptyMoveController;
import com.ticxo.modelengine.api.nms.entity.impl.ManualRangeManager;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.nms.entity.wrapper.RangeManager;
import com.ticxo.modelengine.api.nms.world.IDamageSource;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class PacketBaseEntity<T extends PacketEntity> implements BaseEntity<T> {

    private final T original;
    private ManualRangeManager rangeManager;
    private final List<Entity> passengers = new ArrayList();
    private final int entityId;
    private final UUID uniqueId;
    private Location location;
    private boolean isDead;
    private boolean isGlowing;
    private boolean isOnGround;
    private boolean isMoving;
    private float yHeadRot;
    private float xHeadRot;
    private float yBodyRot;
    private BiPredicate<IDamageSource, Float> onHurtCallback;
    private BiConsumer<Player, EquipmentSlot> onInteractCallback;

    public PacketBaseEntity(T original, int entityId, UUID uuid) {
        this.original = original;
        this.entityId = entityId;
        this.uniqueId = uuid;
    }

    @Override
    public T getOriginal() {
        return this.original;
    }

    public MoveController wrapMoveControl() {
        return new EmptyMoveController();
    }

    public LookController wrapLookControl() {
        return new EmptyLookController();
    }

    public BodyRotationController wrapBodyRotationControl() {
        return new DefaultBodyRotationController(this);
    }

    public void wrapNavigation() {
    }

    public RangeManager wrapRangeManager(IModel model) {
        if (this.rangeManager != null && this.rangeManager.getModel() == model) {
            return this.rangeManager;
        } else {
            this.rangeManager = new ManualRangeManager(this, model);
            return this.rangeManager;
        }
    }

    public boolean onHurt(IDamageSource damageSource, float damage) {
        return this.onHurtCallback != null && this.onHurtCallback.test(damageSource, damage);
    }

    public void onInteract(Player player, EquipmentSlot hand) {
        if (this.onInteractCallback != null) {
            this.onInteractCallback.accept(player, hand);
        }

    }

    public void setHitbox(Hitbox hitbox) {
    }

    public Hitbox getHitbox() {
        return null;
    }

    public void setStepHeight(double height) {
    }

    public Double getStepHeight() {
        return null;
    }

    public void setCollidableToLiving(LivingEntity living, boolean flag) {
    }

    public void broadcastSpawnPacket() {
    }

    public void broadcastDespawnPacket() {
    }

    public World getWorld() {
        return this.location.getWorld();
    }

    public ManualRangeManager getRangeManager() {
        return this.rangeManager;
    }

    public List<Entity> getPassengers() {
        return this.passengers;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public boolean isGlowing() {
        return this.isGlowing;
    }

    public boolean isOnGround() {
        return this.isOnGround;
    }

    public boolean isMoving() {
        return this.isMoving;
    }

    public float getYHeadRot() {
        return this.yHeadRot;
    }

    public float getXHeadRot() {
        return this.xHeadRot;
    }

    public float getYBodyRot() {
        return this.yBodyRot;
    }

    public BiPredicate<IDamageSource, Float> getOnHurtCallback() {
        return this.onHurtCallback;
    }

    public BiConsumer<Player, EquipmentSlot> getOnInteractCallback() {
        return this.onInteractCallback;
    }

    public void setRangeManager(ManualRangeManager rangeManager) {
        this.rangeManager = rangeManager;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }

    public void setGlowing(boolean isGlowing) {
        this.isGlowing = isGlowing;
    }

    public void setOnGround(boolean isOnGround) {
        this.isOnGround = isOnGround;
    }

    public void setMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }

    public void setYHeadRot(float yHeadRot) {
        this.yHeadRot = yHeadRot;
    }

    public void setXHeadRot(float xHeadRot) {
        this.xHeadRot = xHeadRot;
    }

    public void setYBodyRot(float yBodyRot) {
        this.yBodyRot = yBodyRot;
    }

    public void setOnHurtCallback(BiPredicate<IDamageSource, Float> onHurtCallback) {
        this.onHurtCallback = onHurtCallback;
    }

    public void setOnInteractCallback(BiConsumer<Player, EquipmentSlot> onInteractCallback) {
        this.onInteractCallback = onInteractCallback;
    }
}
