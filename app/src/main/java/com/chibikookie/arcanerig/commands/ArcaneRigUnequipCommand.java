package com.chibikookie.arcanerig.commands;

import javax.annotation.Nonnull;

import com.chibikookie.arcanerig.components.ArcaneRigData;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class ArcaneRigUnequipCommand extends AbstractPlayerCommand {
    RequiredArg<String> slotIdArg = withRequiredArg("slotId", "The slot id to unequip", ArgTypes.STRING);

    public ArcaneRigUnequipCommand() {
        super("unequip", "Unequip a given slot and put it in your inventory");
        setPermissionGroup(GameMode.Creative);
    }

    @Override
    protected void execute(
            @Nonnull CommandContext cmdContext,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world) {

        Player player = store.getComponent(ref, Player.getComponentType());
        assert player != null;

        ArcaneRigData data = store.ensureAndGetComponent(ref, ArcaneRigData.getComponentType());

        String slotId = slotIdArg.get(cmdContext);
        if (!data.removeEntry(slotId)) {
            player.sendMessage(Message.raw("Slot is empty"));
            return;
        }

        player.sendMessage(Message.raw("Unequipping item at \"" + slotId + "\""));
    }
}
