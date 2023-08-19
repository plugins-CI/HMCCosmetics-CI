package com.hibiscusmc.hmccosmetics.hooks.modelengine;

import com.hibiscusmc.hmccosmetics.nms.PacketEntity;

public record MegEntityWrapper<T extends PacketEntity>(PacketBaseEntity<T> entity) {

}
