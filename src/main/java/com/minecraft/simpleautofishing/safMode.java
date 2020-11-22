package com.minecraft.simpleautofishing;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class safMode {
	//global vars
	static int mode; 
	static int attack_helper;
	static Minecraft Inst = Minecraft.getInstance();

	//check for FishingRod, sneaking and attack
	@SubscribeEvent
	public void ModeChanger(ClientTickEvent event) {
		if (Inst.player != null && Inst.player.isCrouching() && Inst.player.getHeldItemMainhand().getItem() == Items.FISHING_ROD && AttackReleased()) {
			if (mode == 2) {
				mode = 0;
			} else {
				mode++;
			}
			if (mode == 0) {
					Inst.player.sendMessage(new TranslationTextComponent("text.simpleautofishing.safMode.fishing_rod_unprotected"), Inst.player.getUniqueID());
			} else if (mode == 1) {
				Inst.player.sendMessage(new TranslationTextComponent("text.simpleautofishing.safMode.fishing_rod_protected"), Inst.player.getUniqueID());
			} else if (mode == 2) {
				Inst.player.sendMessage(new TranslationTextComponent("text.simpleautofishing.safMode.all_in_hotbar"), Inst.player.getUniqueID());
			}
		}
	}
	
	public boolean AttackReleased() {
		if (Inst.gameSettings.keyBindAttack.isPressed()) {
			attack_helper = 1;
			return false;
		}
		if (attack_helper == 1) {
			attack_helper = 0;
			return true;
		}
		return false;
	}
	
	//Modes
	public static boolean Modes() {
		if (mode == 1) {
			if (Inst.player.getHeldItemMainhand().getDamage() >= Inst.player.getHeldItemMainhand().getMaxDamage() - 3) {
				return false;
			} else if (Inst.player.getHeldItemMainhand().getItem() != Items.FISHING_ROD && Inst.player.getHeldItemOffhand().getDamage() >= Inst.player.getHeldItemOffhand().getMaxDamage() - 3) {
				return false;
			}
		}
		if (mode == 2 && Inst.player.getHeldItemMainhand().getItem() != Items.FISHING_ROD) {
			for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
				if (Inst.player.getHeldItemMainhand().getItem() == Items.FISHING_ROD) {
					return true;
				}
				Inst.player.inventory.changeCurrentItem(1D);
			}
			return false;
		}
		return true;
	}
}
