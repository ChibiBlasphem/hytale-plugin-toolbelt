package com.chibikookie.arcanerig.dev;

import com.chibikookie.arcanerig.ArcaneRigPlugin;
import com.chibikookie.arcanerig.api.SlotDescriptor;

public final class DevBootstrap {
    public static void install(ArcaneRigPlugin plugin) {
        plugin.getLogger().atInfo().log("[DEV] Bootstrapping dev features");

        ArcaneRigPlugin.onApiReady(api -> {
            var pickaxeSlot = SlotDescriptor.builder("t_pickaxe")
                    .displayName("Pickaxe")
                    .iconPath("ChibiKookie_ItemSlotBackground_Pickaxe.png")
                    .canEquip(s -> {
                        return s.getItemId().contains("Pickaxe");
                    })
                    .build();
            api.registerSlot(pickaxeSlot);
        });
    }
}
