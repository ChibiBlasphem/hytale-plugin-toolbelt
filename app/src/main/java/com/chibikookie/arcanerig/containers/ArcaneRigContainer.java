package com.chibikookie.arcanerig.containers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.chibikookie.arcanerig.ArcaneRigPlugin;
import com.chibikookie.arcanerig.api.ArcaneRigApi;
import com.chibikookie.arcanerig.api.SlotDescriptor;
import com.chibikookie.arcanerig.components.ArcaneRigData;
import com.chibikookie.arcanerig.components.ArcaneRigData.SlotEntry;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;

public class ArcaneRigContainer extends SimpleItemContainer {
    private ArcaneRigData data;
    private Map<Short, String> slotsToSlotIds = new HashMap<Short, String>();

    public ArcaneRigContainer(ArcaneRigData data) {
        this.data = data;

        ArcaneRigApi api = ArcaneRigPlugin.api();
        var slots = api.getRegisteredSlots();

        short capacity = Integer.valueOf(slots.size()).shortValue();

        super(capacity);

        for (short i = 0; i < capacity; i++) {
            SlotDescriptor slot = slots.get(i);
            String slotId = slot.slotId;

            slotsToSlotIds.put(i, slotId);
            SlotEntry entry = data.getEntry(slotId);
            this.setSlotFilter(FilterActionType.ADD, i, new SlotFilter(slot.canEquip));

            if (entry == null) {
                continue;
            }

            super.setItemStackForSlot(i, entry.getStack());
        }
    }

    public void transferToComponent() {
        for (short i = 0; i < getCapacity(); i++) {
            String slotId = slotsToSlotIds.get(i);
            if (slotId == null) {
                continue;
            }

            ItemStack stack = getItemStack(i);
            if (stack == null) {
                data.removeEntry(slotId);
                continue;
            }

            data.putEntry(new SlotEntry(slotId, stack));
        }
    }

    @Override
    public @Nonnull ItemStackSlotTransaction setItemStackForSlot(short slot, ItemStack stack) {
        return super.setItemStackForSlot(slot, stack);
    }

    private class SlotFilter implements com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter {
        private Predicate<ItemStack> predicate;

        public SlotFilter(Predicate<ItemStack> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(FilterActionType actionType, ItemContainer container, short slot,
                @Nullable ItemStack stack) {
            if (actionType == FilterActionType.ADD && stack != null) {
                return predicate.test(stack);
            }
            return true;
        }
    }
}
