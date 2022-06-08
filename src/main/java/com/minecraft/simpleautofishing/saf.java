package com.minecraft.simpleautofishing;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("simpleautofishing")
public class saf
{
	// Directly reference a log4j logger.
	public static final Logger LOGGER = LoggerFactory.getLogger("simpleautofishing");

	public static safCore safCore = new safCore();
	public static safMode safMode = new safMode();

	public saf() {
		
		MinecraftForge.EVENT_BUS.register(saf.safCore);
		MinecraftForge.EVENT_BUS.register(saf.safMode);
	}
}
