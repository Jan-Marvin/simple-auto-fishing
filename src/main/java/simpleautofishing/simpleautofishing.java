package simpleautofishing;

import simpleautofishing.mixin.FishingBobberEntityAccessorMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import com.mojang.logging.LogUtils;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

import static simpleautofishing.simpleautofishingMode.fishingRod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(simpleautofishing.MODID)
public class simpleautofishing {

	public static final String MODID = "simpleautofishing";
	public static final Logger LOGGER = LogUtils.getLogger();
	private static Minecraft client = Minecraft.getInstance();
	private int delay = 20;
	private boolean hookCastOut = true;

	public simpleautofishing(FMLJavaModLoadingContext context) {
		LOGGER.info("Register simpleautofishing");
		TickEvent.ClientTickEvent.Post.BUS.addListener(this::onTickEvent);
	}

	@SubscribeEvent
	public void onTickEvent(TickEvent.ClientTickEvent.Post event) {
		if (client.player == null) {
			return;
		}

		simpleautofishingMode.ModeChanger();

		if (client.player.fishing == null && !hookCastOut && delay == 15) {
			UseRod();
			hookCastOut = true;
		} else if (client.player.fishing == null && !hookCastOut && delay != 15) {
			delay++;
		}

		if (client.player.fishing != null && ((FishingBobberEntityAccessorMixin) client.player.fishing).getBiting()) {
			UseRod();
			delay = 0;
			hookCastOut = false;
		}
	}

	public void UseRod() {

		if (!simpleautofishingMode.modeCheck()) {
			return;
		}
		if (client.player.getMainHandItem().is(fishingRod)) {
			client.player.swing(InteractionHand.MAIN_HAND);
			client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
		} else {
			client.player.swing(InteractionHand.OFF_HAND);
			client.gameMode.useItem(client.player, InteractionHand.OFF_HAND);
		}
	}
}