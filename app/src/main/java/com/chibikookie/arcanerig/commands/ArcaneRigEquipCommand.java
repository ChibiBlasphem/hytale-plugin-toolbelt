package com.chibikookie.arcanerig.commands;

import javax.annotation.Nonnull;

import com.chibikookie.arcanerig.ArcaneRigPlugin;
import com.chibikookie.arcanerig.api.ArcaneRigApi;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class ArcaneRigEquipCommand extends AbstractPlayerCommand {
    RequiredArg<String> slotIdArg = withRequiredArg("slotId", "The slot id to equip the currently held item in", ArgTypes.STRING);

    public ArcaneRigEquipCommand() {
        super("equip", "Equip the currently held item in a given slot");
        setPermissionGroup(GameMode.Creative);
    }

    @Override
    protected void execute(@Nonnull CommandContext cmdContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        assert player != null;

        String slotId = slotIdArg.get(cmdContext);
        ArcaneRigApi api = ArcaneRigPlugin.api();

        ItemStack stack = player.getInventory().getActiveHotbarItem();
        if (stack == null) {
            player.sendMessage(Message.raw("Your hand is empty"));
            return;
        }

        if (api.trySetEquipped(ref, store, slotId, stack)) {
            player.sendMessage(Message.raw("Item "+ stack.getItemId() +" successfully added to '"+ slotId +"'"));
            return;
        }
        player.sendMessage(Message.raw("Failed to add "+ stack.getItemId() +" to '"+ slotId +"'"));
    }
}
