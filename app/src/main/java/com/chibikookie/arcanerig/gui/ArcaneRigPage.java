package com.chibikookie.arcanerig.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.chibikookie.arcanerig.api.SlotDescriptor;
import com.chibikookie.arcanerig.components.ArcaneRigData;
import com.chibikookie.arcanerig.containers.ArcaneRigContainer;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.ui.ItemGridSlot;
import com.hypixel.hytale.server.core.ui.PatchStyle;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class ArcaneRigPage extends InteractiveCustomUIPage<ArcaneRigPage.Data> {
    private final ArcaneRigContainer rigContainer;

    public ArcaneRigPage(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime, ArcaneRigData rigData) {
        super(playerRef, lifetime, Data.CODEC);
        this.rigContainer = new ArcaneRigContainer(rigData);
    }

    @Override
    public void build(Ref<EntityStore> ref, UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder, Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/ArcaneRig/ArcaneRigPage.ui");

        Player player = store.getComponent(ref, Player.getComponentType());
        this.buildGrids(player, uiCommandBuilder);

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Dropped, "#ArcanerigGrid", EventData.of("Action", "Dropped").append("Target", "10"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Dropped, "#StorageGrid", EventData.of("Action", "Dropped").append("Target", "20"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Dropped, "#HotbarGrid", EventData.of("Action", "Dropped").append("Target", "30"), false);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull Data data) {
        super.handleDataEvent(ref, store, data);

        if (data.sourceInventorySectionId == null || data.sourceSlotId == null || data.slotIndex == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }
        
        ItemContainer targetContainer = getItemContainer(data.target, player);
        if (targetContainer == null) {
            return;
        }

        ItemContainer sourceContainer = getItemContainer(data.sourceInventorySectionId, player);
        if (sourceContainer == null) {
            return;
        }

        short sourceSlotId = data.sourceSlotId.shortValue();
        short targetSlotId = data.slotIndex.shortValue();

        ItemStack sourceStack = sourceContainer.getItemStack(sourceSlotId);
        ItemStack targetStack = targetContainer.getItemStack(targetSlotId);

        if (sourceStack != null) {
            boolean isTransactionPossible = 
                targetContainer.canAddItemStackToSlot(targetSlotId, sourceStack, true, true) &&
                (targetStack == null || sourceContainer.canAddItemStackToSlot(sourceSlotId, targetStack, true, true));

            if (!isTransactionPossible) {
                return;
            }

            targetContainer.setItemStackForSlot(targetSlotId, sourceStack, true);
            sourceContainer.setItemStackForSlot(sourceSlotId, targetStack, true);

            UICommandBuilder uiCommandBuilder = new UICommandBuilder();
            this.buildGrids(player, uiCommandBuilder);

            this.sendUpdate(uiCommandBuilder);
        }
    }

    @Override
    public void onDismiss(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        this.rigContainer.transferToComponent();
    }

    private void buildGrids(Player player, UICommandBuilder uiCommandBuilder) {
        // Arcane rig Grid
        List<ItemGridSlot> rigSlots = new ArrayList<>();
        for (short i = 0; i < this.rigContainer.getCapacity(); ++i) {
            ItemStack stack = this.rigContainer.getItemStack(i);
            SlotDescriptor slotDescriptor = this.rigContainer.getSlotDescriptor(i);

            ItemGridSlot slot = new ItemGridSlot(stack);
            slot.setActivatable(true);
            if (slotDescriptor.iconPath != null) {
                slot.setBackground(Value.of(new PatchStyle(Value.of(slotDescriptor.iconPath))));
            }
            rigSlots.add(slot);
        }
        uiCommandBuilder.set("#ArcanerigGrid.Slots", rigSlots);

        // Storage
        ItemContainer storage = player.getInventory().getStorage();
        ItemContainer hotbar = player.getInventory().getHotbar();

        List<ItemGridSlot> storageSlots = new ArrayList<>();
        for (short i = 0; i < storage.getCapacity(); ++i) {
            ItemStack stack = storage.getItemStack(i);

            ItemGridSlot slot = new ItemGridSlot(stack);
            slot.setActivatable(true);
            storageSlots.add(slot);
        }
        uiCommandBuilder.set("#StorageGrid.Slots", storageSlots);


        List<ItemGridSlot> hotbarSlots = new ArrayList<>();
        for (short i = 0; i < hotbar.getCapacity(); ++i) {
            ItemStack stack = hotbar.getItemStack(i);

            ItemGridSlot slot = new ItemGridSlot(stack);
            slot.setActivatable(true);
            hotbarSlots.add(slot);
        }
        uiCommandBuilder.set("#HotbarGrid.Slots", hotbarSlots);
    }

    private ItemContainer getItemContainer(Integer sectionId, Player player) {
        if (sectionId == null)
            return null;

        return switch (sectionId) {
            case 10 -> this.rigContainer;
            case 20 -> player.getInventory().getStorage();
            case 30 -> player.getInventory().getHotbar();
            default -> null;
        };
    }
    
    public static class Data {
        public static BuilderCodec<Data> CODEC;

        private String action;
        private Integer target;
        private Integer sourceItemGridIndex;
        private Integer sourceSlotId;
        private Integer itemStackQuantity;
        private Integer pressedMouseButton;
        private String itemStackId;
        private Integer sourceInventorySectionId;
        private Integer slotIndex;

        static {
            CODEC = BuilderCodec.builder(Data.class, Data::new)
                    .append(new KeyedCodec<>("Action", Codec.STRING), (d, v) -> d.action = v, d -> d.action).add()
                    .append(new KeyedCodec<>("Target", Codec.STRING), (d, v) -> d.target = Integer.valueOf(v, 10), d -> d.target.toString()).add()
                    .append(new KeyedCodec<>("SourceItemGridIndex", Codec.INTEGER), (d, v) -> d.sourceItemGridIndex = v, d -> d.sourceItemGridIndex).add()
                    .append(new KeyedCodec<>("SourceSlotId", Codec.INTEGER), (d, v) -> d.sourceSlotId = v, d -> d.sourceSlotId).add()
                    .append(new KeyedCodec<>("ItemStackQuantity", Codec.INTEGER), (d, v) -> d.itemStackQuantity = v, d -> d.itemStackQuantity).add()
                    .append(new KeyedCodec<>("PressedMouseButton", Codec.INTEGER), (d, v) -> d.pressedMouseButton = v, d -> d.pressedMouseButton).add()
                    .append(new KeyedCodec<>("ItemStackId", Codec.STRING), (d, v) -> d.itemStackId = v, d -> d.itemStackId).add()
                    .append(new KeyedCodec<>("SourceInventorySectionId", Codec.INTEGER), (d, v) -> d.sourceInventorySectionId = v, d -> d.sourceInventorySectionId).add()
                    .append(new KeyedCodec<>("SlotIndex", Codec.INTEGER), (d, v) -> d.slotIndex = v, d -> d.slotIndex).add()
                    .build();
        }

        @Override
        public String toString() {
            String tpl = "ArcaneRigPage.Data{"
                + "target=%s, sourceItemGridIndex=%d, "
                + "sourceSlotId=%d, itemStackQuantity=%d, "
                + "pressedMouseButton=%d, itemStackId=%s, "
                + "sourceInventorySectionId=%d, slotIndex=%d"
                + "}";
            return String.format(tpl,
                target, sourceItemGridIndex,
                sourceSlotId, itemStackQuantity,
                pressedMouseButton, itemStackId,
                sourceInventorySectionId, slotIndex
            );
        }
    }
}
