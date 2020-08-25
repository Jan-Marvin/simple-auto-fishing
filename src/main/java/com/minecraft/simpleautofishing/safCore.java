package com.minecraft.simpleautofishing;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

//simple auto fishing core
public class safCore implements ModInitializer {
    //global vars
    boolean retract = false;
    boolean extract = false;
    int timer;
    int timer2;
    int lock;
    static MinecraftClient Inst = MinecraftClient.getInstance();

    public void onInitialize() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public void onTick(MinecraftClient client) {
        //reset helper when bobber not exist
        if (!FishingBobberExist()) {
            timer = 0;
        }
        //check if bobber is on water
        if (FishingBobberExist() && Inst.player.fishHook.isTouchingWater()) {
            if (timer == 0) {
                timer = 1;
            } else if (timer == 25) {
                //check if bobber is pulled down
                if (Inst.player.fishHook.getVelocity().getY() < -0.1D) {
                    retract = true;
                }
            } else if (timer < 25) {
                timer++;
            }
        }
        if (retract || extract) {
            //retract bobber
            if (timer2 == 5 && retract) {
                if (lock == 0) {
                    UseRod();
    				//remove bobber
    				if (FishingBobberExist()) {
    					Inst.player.fishHook.remove();
    				}
                    timer2 = 0;
                    retract = false;
                    extract = true;
                    lock = 1;
                }
            }
            if (timer2 == 25 && extract) {
				//extract bobber
                if (safMode.Modes()) {
                    UseRod();
                }
                timer2 = 0;
                extract = false;
                lock = 0;
            }
            timer2++;
        }
    }

    //check if player exist
    public boolean PyerExist() {
        if (Inst.player != null) {
            return true;
        }
        return false;
    }

    //check if bobber exist
    public boolean FishingBobberExist() {
        if (PyerExist() && Inst.player.fishHook != null) {
            return true;
        }
        return false;
    }
    
    //hand selector
    public static Hand getHand() {
    	if (Inst.player.getMainHandStack().getItem() == Items.FISHING_ROD) {
    		return Hand.MAIN_HAND;
    	} else {
    		return Hand.OFF_HAND;
    	}
    }

    //use road
    public static ActionResult UseRod() {
        Inst.player.swingHand(getHand());
        return Inst.interactionManager.interactItem(Inst.player, Inst.world, getHand());
    }
}
