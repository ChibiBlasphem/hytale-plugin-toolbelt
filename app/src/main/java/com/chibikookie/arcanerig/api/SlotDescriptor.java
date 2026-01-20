package com.chibikookie.arcanerig.api;

import java.util.Objects;
import java.util.function.Predicate;

import com.hypixel.hytale.server.core.inventory.ItemStack;

public final class SlotDescriptor {
    public final String slotId;
    public final String displayName;
    public final Predicate<ItemStack> canEquip;

    private SlotDescriptor(Builder b) {
        slotId = b.slotId;
        displayName = b.displayName;
        canEquip = b.canEquip;
    }

    public static Builder builder(String slotId) {
        return new Builder(slotId);
    }

    public static final class Builder {
        private final String slotId;
        private String displayName;
        private Predicate<ItemStack> canEquip = s -> true;

        private Builder(String slotId) { this.slotId = slotId; }

        public Builder displayName(String name) {
            displayName = name;
            return this;
        }

        public Builder canEquip(Predicate<ItemStack> predicate) {
            canEquip = predicate;
            return this;
        }

        public SlotDescriptor build() {
            Objects.requireNonNull(displayName, "displayName");
            Objects.requireNonNull(canEquip, "canEquip");
            return new SlotDescriptor(this);
        }
    }
}
