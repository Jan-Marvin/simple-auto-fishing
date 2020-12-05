package com.minecraft.simpleautofishing;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("simpleautofishing")
public class saf
{
	// Directly reference a log4j logger.
	static final Logger LOGGER = LogManager.getLogger();

	public static safCore safCore = new safCore();
	public static safMode safMode = new safMode();

	public saf() {
		
		MinecraftForge.EVENT_BUS.register(saf.safCore);
		MinecraftForge.EVENT_BUS.register(saf.safMode);
	}
}
