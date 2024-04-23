package simpleautofishing;

import simpleautofishing.mixin.FishingBobberEntityAccessorMixin;
import net.minecraft.util.Hand;
import net.minecraft.item.Items;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class simpleautofishing implements ClientModInitializer {
	private MinecraftClient client = MinecraftClient.getInstance();
    public static final Logger LOGGER = LoggerFactory.getLogger("simpleautofishing");
	int delay = 15;
	boolean extract = true;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Registering simpleautofishing!");
		ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
	}

	private void onTick(MinecraftClient client) {
		if (client.player == null) {
			return;
		}

		simpleautofishingMode.ModeChanger();

		if (client.player.fishHook == null && !extract && delay == 15) {
			UseRod();
			extract = true;
		} else if (client.player.fishHook == null && !extract && delay != 15) {
			delay++;
		}

		if (client.player.fishHook != null && ((FishingBobberEntityAccessorMixin) client.player.fishHook).getCaughtFish()) {
			UseRod();
			delay = 0;
			extract = false;
		}
	}

	public void UseRod() {
		if (!simpleautofishingMode.modeCheck()) {
			return;
		}
		if (client.player.getMainHandStack().getItem() == Items.FISHING_ROD) {
			client.player.swingHand(Hand.MAIN_HAND);
			client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
		} else {
			client.player.swingHand(Hand.OFF_HAND);
			client.interactionManager.interactItem(client.player, Hand.OFF_HAND);
		}
	}
}