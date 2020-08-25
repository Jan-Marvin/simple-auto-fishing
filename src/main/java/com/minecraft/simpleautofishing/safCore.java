package com.minecraft.simpleautofishing;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
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
    	if (FishingBobberExist() && Inst.player.fishingBobber.isInWater()) {
    		if (timer == 0) {
    			timer = 1;
    		} else if (timer == 25) {
    			//check if bobber is pulled down
    			if (Inst.player.fishingBobber.getMotion().y < -0.1D) {
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
    					Inst.player.fishingBobber.remove(false);
    				}
    				timer2=0;
    				retract = false;
    				extract = true;
    				lock = 1;
    			}
    		}
    		if(timer2 == 30 && extract) {
    			if (safMode.Modes()) {
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
    public boolean PyerExist() {
    	if (Inst.player != null) {
    		return true;
    	}
    	return false;
    }
    
    //check if bobber exist
    public boolean FishingBobberExist() {
    	if (PyerExist() && Inst.player.fishingBobber != null) {
    		return true;
    	}
    	return false;
    }
    
    //hand selector
    public static Hand getHand() {
    	if (Inst.player.getHeldItemMainhand().getItem() == Items.FISHING_ROD) {
    		return Hand.MAIN_HAND;
    	} else {
    		return Hand.OFF_HAND;
    	}
    }
    
    //use road
    public static ActionResultType UseRod() {
        Inst.player.swingArm(getHand());
        return Inst.playerController.processRightClick(Inst.player, Inst.world, getHand());
    }
}
