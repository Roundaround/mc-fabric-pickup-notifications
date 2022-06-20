package me.roundaround.pickupnotifications.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import me.roundaround.pickupnotifications.util.SlotUpdate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
  @Shadow
  boolean disableSync;

  @Shadow
  DefaultedList<Slot> slots;

  @Shadow
  DefaultedList<ItemStack> previousTrackedStacks;

  @Shadow
  ScreenHandlerSyncHandler syncHandler;

  @Shadow
  abstract Slot getSlot(int index);

  @Inject(method = "sendContentUpdates", at = @At(value = "HEAD"))
  public void sendContentUpdates(CallbackInfo info) {
    if (disableSync || syncHandler == null) {
      return;
    }

    ArrayList<SlotUpdate> updates = new ArrayList<>();
    for (int i = 0; i < slots.size(); ++i) {
      ItemStack itemStack = getSlot(i).getStack();
      Supplier<ItemStack> supplier = Suppliers.memoize(itemStack::copy);

      ItemStack prevStack = previousTrackedStacks.get(i);
      if (!ItemStack.areEqual(prevStack, itemStack)) {
        updates.add(new SlotUpdate(i, prevStack, supplier.get()));
      }
    }

    // Check if result of swapping with off hand
    if (updates.size() == 2) {
      boolean aToB = ItemStack.areEqual(
          updates.get(0).getPrevious(),
          updates.get(1).getCurrent());
      boolean bToA = ItemStack.areEqual(
          updates.get(1).getPrevious(),
          updates.get(0).getCurrent());

      if (aToB && bToA) {
        // This was a swap, don't send update
        return;
      }
    }

    for (SlotUpdate update : updates) {
      // Send update using a dummy screen handler that has a unique sync ID
      syncHandler.updateSlot(new ScreenHandler(null, -10) {
        @Override
        public ItemStack transferSlot(PlayerEntity var1, int var2) {
          return null;
        }

        @Override
        public boolean canUse(PlayerEntity var1) {
          return false;
        }
      }, update.getSlot(), update.getCurrent());
    }
  }
}
