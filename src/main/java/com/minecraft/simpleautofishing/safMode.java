package com.minecraft.simpleautofishing;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
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
		if (Inst.player != null && Inst.player.isCrouching() && Inst.player.getMainHandItem().getItem() == Items.FISHING_ROD && AttackReleased()) {
			if (mode == 2) {
				mode = 0;
			} else {
				mode++;
			}
			if (mode == 0) {
					Inst.player.sendSystemMessage(Component.translatable("text.simpleautofishing.safMode.fishing_rod_unprotected"));
			} else if (mode == 1) {
				Inst.player.sendSystemMessage(Component.translatable("text.simpleautofishing.safMode.fishing_rod_protected"));
			} else if (mode == 2) {
				Inst.player.sendSystemMessage(Component.translatable("text.simpleautofishing.safMode.all_in_hotbar"));
			}
		}
	}
	
	public boolean AttackReleased() {
		if (Inst.options.keyAttack.isDown()) {
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
			if (Inst.player.getMainHandItem().getDamageValue() >= Inst.player.getMainHandItem().getMaxDamage() - 3) {
				return false;
			} else if (Inst.player.getMainHandItem().getItem() != Items.FISHING_ROD && Inst.player.getMainHandItem().getDamageValue() >= Inst.player.getMainHandItem().getMaxDamage() - 3) {
				return false;
			}
		}
		if (mode == 2 && Inst.player.getMainHandItem().getItem() != Items.FISHING_ROD) {
			for (int i = 0; i < Inventory.getSelectionSize(); i++) {
				if (Inst.player.getMainHandItem().getItem() == Items.FISHING_ROD) {
					return true;
				}
				Inst.player.getInventory().swapPaint(1D);
			}
			return false;
		}
		return true;
	}
}
