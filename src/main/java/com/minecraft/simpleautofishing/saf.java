package com.minecraft.simpleautofishing;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class saf implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("simpleautofishing");

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}
}
