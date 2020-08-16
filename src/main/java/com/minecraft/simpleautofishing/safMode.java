package com.minecraft.simpleautofishing;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class safMode implements ModInitializer {
    //global vars
    static int mode;
    static int helper;
    static MinecraftClient Inst = MinecraftClient.getInstance();

    public void onInitialize() {
        ClientTickEvents.END_CLIENT_TICK.register(this::ModeChanger);
    }

    //Mode Messages
    public void ModeChanger(MinecraftClient client) {
        if (Inst.player != null && Inst.player.isSneaking() && Inst.player.getMainHandStack().getItem() == Items.FISHING_ROD && AttackReleased()) {
            if (mode == 2) {
                mode = 0;
            } else {
                mode++;
            }
            if (mode == 0) {
                Inst.player.sendSystemMessage(new TranslatableText("text.simpleautofishing.safMode.fishing_rod_unprotected"), Inst.player.getUuid());
            } else if (mode == 1) {
                Inst.player.sendSystemMessage(new TranslatableText("text.simpleautofishing.safMode.fishing_rod_protected"), Inst.player.getUuid());
            } else if (mode == 2) {
                Inst.player.sendSystemMessage(new TranslatableText("text.simpleautofishing.safMode.all_in_hotbar"), Inst.player.getUuid());
            }
        }
    }

    public boolean AttackReleased() {
        if (Inst.options.keyAttack.isPressed()) {
            helper = 1;
            return false;
        }
        if (helper == 1) {
            helper = 0;
            return true;
        }
        return false;
    }

    //Modes
    public static boolean Modes() {
        ItemStack is = Inst.player.getMainHandStack();
        if (mode == 1 && (is.getDamage() >= is.getMaxDamage() - 3)) {
            return false;
        }
        if (mode == 2 && is.getItem() != Items.FISHING_ROD) {
            for (int i = 0; i < 9; i++) {
                if (Inst.player.getMainHandStack().getItem() == Items.FISHING_ROD) {
                    return true;
                }
                Inst.player.inventory.scrollInHotbar(1D);
            }
            return false;
        }
        return true;
    }
}
