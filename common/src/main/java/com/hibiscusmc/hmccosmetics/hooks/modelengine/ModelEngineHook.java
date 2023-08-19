package com.hibiscusmc.hmccosmetics.hooks.modelengine;

import com.hibiscusmc.hmccosmetics.cosmetic.types.CosmeticBackpackType;
import com.hibiscusmc.hmccosmetics.nms.PacketArmorStand;
import com.hibiscusmc.hmccosmetics.util.MessagesUtil;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;

import java.util.logging.Level;

public class ModelEngineHook {

    public static void spawnNormalBackpack(PacketArmorStand armorStand, CosmeticBackpackType cosmeticBackpackType) {
        if (ModelEngineAPI.api.getModelRegistry().getBlueprint(cosmeticBackpackType.getModelName()) == null) {
            MessagesUtil.sendDebugMessages("Invalid Model Engine Blueprint " + cosmeticBackpackType.getModelName(), Level.SEVERE);
            return;
        }
        final ModeledEntity modeledEntity;
        final MegEntityWrapper<PacketArmorStand> wrapper = armorStand.getMegEntityWrapper();
        if (ModelEngineAPI.isModeledEntity(wrapper.entity().getUniqueId())) {
            modeledEntity = ModelEngineAPI.getModeledEntity(wrapper.entity().getUniqueId());
        } else {
            modeledEntity = ModelEngineAPI.createModeledEntity(wrapper.entity());
        }
        ActiveModel model = ModelEngineAPI.createActiveModel(ModelEngineAPI.getBlueprint(cosmeticBackpackType.getModelName()));
        model.setCanHurt(false);
        modeledEntity.addModel(model, false);
    }

    public static void spawnFirstPersonBackpack(PacketArmorStand armorStand, CosmeticBackpackType cosmeticBackpackType) {
        if (ModelEngineAPI.api.getModelRegistry().getBlueprint(cosmeticBackpackType.getModelName()) == null) {
            MessagesUtil.sendDebugMessages("Invalid Model Engine Blueprint " + cosmeticBackpackType.getModelName(), Level.SEVERE);
            return;
        }
        final ModeledEntity modeledEntity;
        final MegEntityWrapper<PacketArmorStand> wrapper = armorStand.getMegEntityWrapper();
        if (ModelEngineAPI.isModeledEntity(wrapper.entity().getUniqueId())) {
            modeledEntity = ModelEngineAPI.getModeledEntity(wrapper.entity().getUniqueId());
        } else {
            modeledEntity = ModelEngineAPI.createModeledEntity(wrapper.entity());
        }            ActiveModel model = ModelEngineAPI.createActiveModel(ModelEngineAPI.getBlueprint(cosmeticBackpackType.getModelName()));
        model.setCanHurt(false);
        modeledEntity.addModel(model, false);
    }

}
