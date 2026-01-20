package com.chibikookie.arcanerig.api;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public interface ArcaneRigApi {
    void registerSlot(SlotDescriptor slot);
    List<SlotDescriptor> getRegisteredSlots();

    @Nullable
    ItemStack getEquipped(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, String slotId);

    boolean trySetEquipped(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, String slotId,
            @Nullable ItemStack stack);
}
