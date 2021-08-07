package me.roundaround.pickupnotifications.mixin;

import me.roundaround.pickupnotifications.event.ItemPickupCallback;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    private ItemStack cachedItemStack = ItemStack.EMPTY;

    @Shadow
    public abstract ItemStack getStack();

    @Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getStack()Lnet/minecraft/item/ItemStack;"))
    private void onPlayerCollision(PlayerEntity player, CallbackInfo info) {
        cachedItemStack = this.getStack().copy();
    }

    @Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;sendPickup(Lnet/minecraft/entity/Entity;I)V"))
    private void onItemPickup(PlayerEntity player, CallbackInfo info) {
        ItemPickupCallback.EVENT.invoker().interact(player, cachedItemStack);
    }
}
