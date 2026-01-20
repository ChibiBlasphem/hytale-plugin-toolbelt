package com.chibikookie.arcanerig.commands;

import javax.annotation.Nonnull;

import com.chibikookie.arcanerig.components.ArcaneRigData;
import com.chibikookie.arcanerig.containers.ArcaneRigContainer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ContainerWindow;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class ArcaneRigCommand extends AbstractPlayerCommand {
    public ArcaneRigCommand() {
        super("arcanerig", "Display GUI for the arcane rig");
        setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void execute(@Nonnull CommandContext cmdContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        assert player != null;

        ArcaneRigData data = store.ensureAndGetComponent(ref, ArcaneRigData.getComponentType());

        ArcaneRigContainer container = new ArcaneRigContainer(data);
        ContainerWindow window = new ContainerWindow(container);

        window.registerCloseEvent(event -> {
            container.transferToComponent();
        });

        PageManager manager = player.getPageManager();
        manager.setPageWithWindows(ref, store, Page.Bench, true, window);
    }
}
