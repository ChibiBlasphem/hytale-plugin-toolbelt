package com.chibikookie.arcanerig.components;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.chibikookie.arcanerig.ArcaneRigPlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class ArcaneRigData implements Component<EntityStore> {
    public static final String COMPONENT_ID = "ChibiKookie_ArcaneRig_Data";
    public static final int CURRENT_VERSION = 1;

    public static final Codec<SlotEntry[]> ENTRIES_CODEC = new ArrayCodec<>(SlotEntry.CODEC, SlotEntry[]::new);
    public static final BuilderCodec<ArcaneRigData> CODEC = BuilderCodec.builder(ArcaneRigData.class, ArcaneRigData::new)
            .append(new KeyedCodec<>("Version", Codec.INTEGER), ArcaneRigData::setVersion, ArcaneRigData::getVersion).add()
            .append(new KeyedCodec<>("Entries", ENTRIES_CODEC), ArcaneRigData::setEntries, ArcaneRigData::getEntries).add()
            .build();

    private int version;
    private SlotEntry[] entries;
    private transient Map<String, SlotEntry> byId;

    public ArcaneRigData() {
        version = CURRENT_VERSION;
        entries = new SlotEntry[0];
    }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public SlotEntry[] getEntries() { return entries; }
    public void setEntries(SlotEntry[] entries) { this.entries = entries; };

    @Nonnull
    public static ComponentType<EntityStore, ArcaneRigData> getComponentType() {
        return ArcaneRigPlugin.getArcaneRigDataComponentType();
    }

    @Nullable
    public SlotEntry getEntry(String slotId) {
        ensureIndex();
        return byId.get(slotId);
    }

    public boolean putEntry(SlotEntry entry) {
        ensureIndex();
        if (byId.containsKey(entry.getSlotId())) {
            return false;
        }

        byId.put(entry.getSlotId(), entry);
        rebuildEntriesFromMap();
        return true;
    }

    public boolean removeEntry(String slotId) {
        ensureIndex();
        if (!byId.containsKey(slotId)) {
            return false;
        }

        byId.remove(slotId);
        rebuildEntriesFromMap();
        return true;
    }

    @Override
    public ArcaneRigData clone() {
        ArcaneRigData clone = new ArcaneRigData();
        clone.version = this.version;
        clone.entries = this.entries.clone();
        clone.byId = null;
        return clone;
    }

    private void ensureIndex() {
        if (byId != null) return;

        byId = new HashMap<>();
        for (SlotEntry entry : entries) {
            byId.put(entry.getSlotId(), entry);
        }
    }

    private void rebuildEntriesFromMap() {
        entries = byId.values().toArray(new SlotEntry[0]);
    }

    public static final class SlotEntry implements Cloneable {
        public static final BuilderCodec<SlotEntry> CODEC = BuilderCodec.builder(SlotEntry.class, SlotEntry::new)
                .append(new KeyedCodec<>("SlotId", Codec.STRING), SlotEntry::setSlotId, SlotEntry::getSlotId).add()
                .append(new KeyedCodec<>("Stack", ItemStack.CODEC), SlotEntry::setStack, SlotEntry::getStack).add()
                .build();

        private String slotId;
        private @Nullable ItemStack stack;

        public SlotEntry() {}
        public SlotEntry(String slotId, @Nullable ItemStack stack) {
            this.slotId = slotId;
            this.stack = stack;
        }

        public String getSlotId() { return slotId; }
        public void setSlotId(String slotId) { this.slotId = slotId; }

        @Nullable
        public ItemStack getStack() { return stack; }
        public void setStack(@Nullable ItemStack stack) { this.stack = stack; }

        @Override
        public SlotEntry clone() {
            return new SlotEntry(slotId, stack);
        }
    }
}
