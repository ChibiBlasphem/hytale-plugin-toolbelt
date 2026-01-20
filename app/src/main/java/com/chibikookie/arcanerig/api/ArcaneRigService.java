package com.chibikookie.arcanerig.api;

import java.util.ArrayList;
import java.util.List;

import com.hypixel.hytale.server.core.inventory.ItemStack;

public class ArcaneRigService {
    private List<SlotDescriptor> slots = new ArrayList<SlotDescriptor>();

    private boolean doesSlotIdExist(String slotId) {
        return slots.stream().anyMatch(slot -> slot.slotId.equals(slotId));
    }

    public void registerSlot(SlotDescriptor slot) {
        if (doesSlotIdExist(slot.slotId)) {
            throw new IllegalArgumentException("SlotID already exists");
        }
        slots.add(slot);
    }

    public boolean canAddItemToSlot(String slotId, ItemStack stack) {
        if (!doesSlotIdExist(slotId)) {
            throw new IllegalArgumentException("SlotID does not exist");
        }
        SlotDescriptor slot = slots.stream().filter(s -> s.slotId.equals(slotId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("SlotID does not exist"));

        return slot.canEquip.test(stack);
    }

    public List<SlotDescriptor> getSlots() {
        return slots;
    }
}
