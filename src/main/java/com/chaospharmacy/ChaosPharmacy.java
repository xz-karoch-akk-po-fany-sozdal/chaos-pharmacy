package com.chaospharmacy;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaosPharmacy implements ModInitializer {
	public static final String MOD_ID = "chaospharmacy";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.register();
		ModItems.register();
		ChaosEvents.register();
		LOGGER.info("Chaos Pharmacy открыта. Disabilities найден: {}", DisabilityBridge.isPresent());
	}
}
