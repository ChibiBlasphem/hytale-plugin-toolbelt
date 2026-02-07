package com.chibikookie.arcanerig.interactions;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import com.chibikookie.arcanerig.ArcaneRigPlugin;
import com.chibikookie.arcanerig.api.ArcaneRigApi;
import com.chibikookie.arcanerig.components.ArcaneRigData;
import com.chibikookie.arcanerig.gui.ArcaneRigPage;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

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
        
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        PageManager manager = player.getPageManager();

        manager.openCustomPage(ref, store, new ArcaneRigPage(playerRef, CustomPageLifetime.CanDismiss, data));
    }
}
