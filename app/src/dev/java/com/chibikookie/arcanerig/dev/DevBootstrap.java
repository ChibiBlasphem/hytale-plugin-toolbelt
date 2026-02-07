package com.chibikookie.arcanerig.dev;

import com.chibikookie.arcanerig.ArcaneRigPlugin;
import com.chibikookie.arcanerig.api.SlotDescriptor;

public final class DevBootstrap {
    public static void install(ArcaneRigPlugin plugin) {
        plugin.getLogger().atInfo().log("[DEV] Bootstrapping dev features");

        ArcaneRigPlugin.onApiReady(api -> {
            for (int i = 0; i < 40; ++i) {
                var pickaxeSlot = SlotDescriptor.builder(i == 0 ? "t_pickaxe" : "t_pickaxe_"+ i)
                        .displayName("Pickaxe")
                        .iconPath("ChibiKookie_ItemSlotBackground_Pickaxe.png")
                        .canEquip(s -> {
                            return s.getItemId().contains("Pickaxe");
                        })
                        .build();
                api.registerSlot(pickaxeSlot);
            }
        });
    }
}
