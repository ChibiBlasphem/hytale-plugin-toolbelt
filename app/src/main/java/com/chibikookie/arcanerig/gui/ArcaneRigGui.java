package com.chibikookie.arcanerig.gui;

import java.util.ArrayList;

import com.chibikookie.arcanerig.ArcaneRigPlugin;
import com.chibikookie.arcanerig.api.ArcaneRigApi;
import com.chibikookie.arcanerig.api.SlotDescriptor;
import com.chibikookie.arcanerig.components.ArcaneRigData;
import com.chibikookie.arcanerig.containers.ArcaneRigContainer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.ui.ItemGridSlot;
import com.hypixel.hytale.server.core.ui.PatchStyle;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import au.ellie.hyui.builders.GroupBuilder;
import au.ellie.hyui.builders.HyUIPage;
import au.ellie.hyui.builders.ItemGridBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.events.DroppedEventData;

public class ArcaneRigGui {
    private final PageBuilder page;
    private final Player player;
    private final Store<EntityStore> store;
    private final ArcaneRigData rigData;
    private final ArcaneRigContainer rigContainer;

    public static HyUIPage open(Player player, Store<EntityStore> store, ArcaneRigData rigData) {
        return new ArcaneRigGui(player, store, rigData)
            .build()
            .openPage(store.getComponent(player.getReference(), PlayerRef.getComponentType()), store);
    }

    private ArcaneRigGui(Player player, Store<EntityStore> store, ArcaneRigData rigData) {
        this.player = player;
        this.store = store;
        this.rigData = rigData;
        this.rigContainer = new ArcaneRigContainer(rigData);

        this.page = PageBuilder.detachedPage()
                .loadHtml("Pages/ArcaneRig/ArcaneRigPage.html")
                .withLifetime(CustomPageLifetime.CanDismiss);
    }

    private HyUIPage openPage(PlayerRef playerRef, Store<EntityStore> store) {
        return this.page.open(playerRef, store);
    }

    private ArcaneRigGui build() {
        this.buildItemGrids();
        this.setupDroppedEvents();

        this.page.onDismiss((_page, _isDismissed) -> {
            this.rigContainer.transferToComponent();
        });

        return this;
    }

    private void buildItemGrids() {
        ArcaneRigApi api = ArcaneRigPlugin.api();

        this.page.getById("grid-container", GroupBuilder.class).ifPresent(group -> {
            group.withScrollbarStyle("Common.ui", "DefaultScrollbarStyle");
        });

        this.page.getById("arcanerig-grid", ItemGridBuilder.class).ifPresent(grid -> {
            grid.withSlots(new ArrayList<>());
                // .withContentHeight(300)
                // .withScrollbarStyle("Common.ui", "DefaultExtraSpacingScrollbarStyle")
                // .withMouseWheelScrollBehaviour(MouseWheelScrollBehaviourType.VerticalOnly);

            ItemContainer storage = this.rigContainer;
            for (short i = 0; i < storage.getCapacity(); ++i) {
                SlotDescriptor slotDescriptor = api.getRegisteredSlots().get(i);
                ItemGridSlot slot = new ItemGridSlot(storage.getItemStack(i));
                if (slotDescriptor.iconPath != null) {
                    slot.setBackground(Value.of(new PatchStyle(Value.of(slotDescriptor.iconPath))));
                }
                grid.addSlot(slot);
            }
        });
        this.page.getById("storage-grid", ItemGridBuilder.class).ifPresent(grid -> {
            Value<PatchStyle> style = Value.of(new PatchStyle(Value.of("ChibiKookie_ItemSlotBackground.png")));
            grid.withSlots(new ArrayList<>());


            ItemContainer storage = player.getInventory().getStorage();
            for (short i = 0; i < storage.getCapacity(); ++i) {
                grid.addSlot(new ItemGridSlot(storage.getItemStack(i)).setBackground(style));
            }
        });
        this.page.getById("hotbar-grid", ItemGridBuilder.class).ifPresent(grid -> {
            Value<PatchStyle> style = Value.of(new PatchStyle(Value.of("ChibiKookie_ItemSlotBackground.png")));
            grid.withSlots(new ArrayList<>());

            ItemContainer hotbar = player.getInventory().getHotbar();
            for (short i = 0; i < hotbar.getCapacity(); ++i) {
                grid.addSlot(new ItemGridSlot(hotbar.getItemStack(i)).setBackground(style));
            }
        });
    }

    private void setupDroppedEvents() {
        this.setupOnContainerDropped(10);
        this.setupOnContainerDropped(20);
        this.setupOnContainerDropped(30);
    }

    private void setupOnContainerDropped(Integer sectionId) {
        String targetItemGridId = getItemGridId(sectionId);
        ItemContainer targetContainer = getItemContainer(sectionId, this.player);
        
        if (targetItemGridId == null || targetContainer == null) return;

        this.page.addEventListener(targetItemGridId, CustomUIEventBindingType.Dropped, DroppedEventData.class, (drop, ctx) -> {
            Integer sourceItemGridId = drop.getSourceInventorySectionId();
            ItemContainer sourceContainer = getItemContainer(sourceItemGridId, this.player);
            if (sourceItemGridId == null || sourceContainer == null) {
                return;
            }

            Integer sourceSlotIndex = drop.getSourceSlotId();
            Integer targetSlotIndex = drop.getSlotIndex();
            if (sourceSlotIndex == null || targetSlotIndex == null) {
                return;
            }

            short sourceSlotId = sourceSlotIndex.shortValue();
            short targetSlotId = targetSlotIndex.shortValue();

            ItemStack sourceItemStack = sourceContainer.getItemStack(sourceSlotId);
            ItemStack maybeNullTargetItemStack = targetContainer.getItemStack(targetSlotId);

            if (sourceItemStack != null) {
                boolean canAddToTarget = targetContainer.canAddItemStackToSlot(targetSlotId, sourceItemStack, true, true);
                boolean canAddToSource = maybeNullTargetItemStack == null ? true : sourceContainer.canAddItemStackToSlot(sourceSlotId, maybeNullTargetItemStack, true, true);
                
                if (!canAddToSource || !canAddToTarget) {
                    return;
                }

                targetContainer.setItemStackForSlot(targetSlotId, sourceItemStack, true);
                sourceContainer.setItemStackForSlot(sourceSlotId, maybeNullTargetItemStack, true);

                this.buildItemGrids();
            }

            ctx.updatePage(true);
        });
    }

    private String getItemGridId(Integer sectionId) {
        if (sectionId == null)
            return null;

        return switch (sectionId) {
            case 10 -> "arcanerig-grid";
            case 20 -> "storage-grid";
            case 30 -> "hotbar-grid";
            default -> null;
        };
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
}
