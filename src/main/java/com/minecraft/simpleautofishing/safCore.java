package com.minecraft.simpleautofishing;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//simple auto fishing core
public class safCore {
	//global vars
	boolean retract = false;
	boolean extract = false;
	int timer2; 
	int timer;
	int lock;
	static Minecraft Inst = Minecraft.getInstance();

	//stuff for retract and extract timing
	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent event) {
		//reset helper when bobber not exist
		if (!FishingBobberExist()) {
			timer = 0;
		}
		//check if bobber is on water
		if (FishingBobberExist() && Inst.player.fishing.isInWater()) {
			if (timer == 0) {
				timer = 1;
			} else if (timer == 25) {
				//check if bobber is pulled down
				if (Inst.player.fishing.getDeltaMovement().y() < -0.1D) {
					retract = true;
				}
			} else if (timer < 25) {
				timer++;
			}
		}
		if (retract || extract) {
			if(timer2 == 10 && retract){
				if (lock == 0) {
					UseRod();
					//remove bobber
					if (FishingBobberExist()) {
						Inst.player.fishing.remove(Entity.RemovalReason.DISCARDED);
					}
					timer2=0;
					retract = false;
					extract = true;
					lock = 1;
				}
			}
			if(timer2 == 30 && extract) {
				if (safMode.Modes() && isrod()) {
					UseRod();
				}
				timer2=0;
				extract = false;
				lock = 0;
			}
		timer2++;
		}
	} 

	//check if player exist
	public boolean PlayerExist() {
		if (Inst.player != null) {
			return true;
		}
		return false;
	}
	
	//check if fishing rod is in hand
	public boolean isrod() {
		if (PlayerExist() && Inst.player.getMainHandItem().getItem() == Items.FISHING_ROD) {
			return true;
		}
		return false;
	}

	//check if bobber exist
	public boolean FishingBobberExist() {
		if (PlayerExist() && Inst.player.fishing != null) {
			return true;
		}
		return false;
	}

	//hand selector
	public static InteractionHand getHand() {
		if (Inst.player.getMainHandItem().getItem() == Items.FISHING_ROD) {
			return InteractionHand.MAIN_HAND;
		} else {
			return InteractionHand.OFF_HAND;
		}
	}

	//use road
	public static InteractionResult UseRod() {
		saf.LOGGER.info("Use Road");
		Inst.player.swing(getHand());
		return Inst.gameMode.useItem(Inst.player, getHand());
	}
}