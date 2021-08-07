package me.roundaround.pickupnotifications.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ItemPickupCallback {
    Event<ItemPickupCallback> EVENT = EventFactory.createArrayBacked(ItemPickupCallback.class, (listeners) -> (player, itemStack) -> {
        for (ItemPickupCallback listener : listeners) {
            listener.interact(player, itemStack);
        }
    });

    void interact(PlayerEntity player, ItemStack itemStack);
}
