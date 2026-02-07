package com.chibikookie.arcanerig.interactions;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import com.chibikookie.arcanerig.ArcaneRigPlugin;
import com.chibikookie.arcanerig.api.ArcaneRigApi;
import com.chibikookie.arcanerig.components.ArcaneRigData;
import com.chibikookie.arcanerig.gui.ArcaneRigGui;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import au.ellie.hyui.builders.ItemGridBuilder;
import au.ellie.hyui.events.UIContext;

public class RingInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<RingInteraction> CODEC = BuilderCodec
            .builder(RingInteraction.class, RingInteraction::new, SimpleInstantInteraction.CODEC)
            .build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType,
            @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        assert commandBuffer != null;

        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        assert player != null;

        ArcaneRigData data = commandBuffer.getComponent(ref, ArcaneRigData.getComponentType());
        if (data == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        Store<EntityStore> store = commandBuffer.getExternalData().getStore();
        ArcaneRigApi api = ArcaneRigPlugin.api();
        if (api.getRegisteredSlots().size() == 0) {
            ArcaneRigPlugin.getInstance().getLogger().atInfo().log("No slot has been registered for the arcane rig");
            return;
        }

        // ArcaneRigContainer container = new ArcaneRigContainer(data);
        // ContainerWindow window = new ContainerWindow(container);

        // window.registerCloseEvent(event -> {
        //     container.transferToComponent();
        // });

        ArcaneRigGui.open(player, store, data);

        // builder.addEventListener("arcanerig-grid", CustomUIEventBindingType.Dropped, DroppedEventData.class,
        //         (drop, ctx) -> {
        //             ItemGridBuilder sourceGrid = getItemGrid(drop.getSourceInventorySectionId(), ctx);
        //             ItemGridBuilder targetGrid = ctx.getById("arcanerig-grid", ItemGridBuilder.class).get();

        //             String message = String.format("Dropped from %s", sourceGrid.getId());
        //             player.sendMessage(Message.raw(message));

        //             Integer sourceIndex = drop.getSourceSlotId();
        //             Integer targetIndex = drop.getSlotIndex();

        //             if (sourceIndex == null || targetIndex == null) {
        //                 return;
        //             }

        //             ItemGridSlot sourceSlot = sourceGrid.getSlot(sourceIndex);
        //             ItemGridSlot targetSlot = targetGrid.getSlot(targetIndex);

        //             if (sourceSlot == null || targetSlot == null) {
        //                 return;
        //             }

        //             ItemContainer sourceContainer = getItemContainer(drop.getSourceInventorySectionId(), player);
        //             String slotId = api.getRegisteredSlots().get(targetIndex).slotId;
        //             ItemStack sourceStack = sourceContainer.getItemStack(sourceIndex.shortValue());

        //             player.sendMessage(Message.raw(String.format("Target slot id: %s", slotId)));
        //             player.sendMessage(Message.raw(String.format("Source item: %s", sourceStack.getItemId())));

        //             boolean result = api.trySetEquipped(player.getReference(), store, slotId, sourceStack);

        //             player.sendMessage(Message.raw(String.format("Transaction ok: %b", result)));

        //             ctx.updatePage(false);
        //         });

        // // Drag'n'drop
        // builder.addEventListener("storage-grid", CustomUIEventBindingType.Dropped,
        // DroppedEventData.class,
        // (drop, ctx) -> {
        // ctx.getById("storage-grid", ItemGridBuilder.class).ifPresent(grid -> {
        // Integer sourceIndex = drop.getSourceSlotId();
        // Integer targetIndex = drop.getSlotIndex();
        //
        // if (sourceIndex == null || targetIndex == null) {
        // return;
        // }
        //
        // ItemGridSlot sourceSlot = grid.getSlot(sourceIndex);
        // if (sourceSlot == null) {
        // return;
        // }
        //
        // grid.updateSlot(sourceSlot, targetIndex);
        // grid.updateSlot(new ItemGridSlot(), sourceIndex);
        //
        // // ItemGridSlot updatedTarget = new ItemGridSlot(new ItemStack(
        // // drop.getItemStackId(),
        // // Math.max(1, drop.getItemStackQuantity() - 1)
        // // ));
        // // grid.updateSlot(updatedTarget, targetIndex);
        // });
        //
        // ctx.updatePage(false);
        // });

        // builder.open(player.getPlayerRef(), store);
    }

    static ItemGridBuilder getItemGrid(Integer sectionId, UIContext ctx) {
        if (sectionId == null)
            return null;

        String inventoryId = switch (sectionId) {
            case 10 -> "arcanerig-grid";
            case 20 -> "inventory-grid";
            case 30 -> "hotbar-grid";
            default -> null;
        };

        return ctx.getById(inventoryId, ItemGridBuilder.class).get();
    }

    static ItemContainer getItemContainer(Integer sectionId, Player player) {
        if (sectionId == null)
            return null;

        return switch (sectionId) {
            case 20 -> player.getInventory().getStorage();
            case 30 -> player.getInventory().getHotbar();
            default -> null;
        };
    }
}
