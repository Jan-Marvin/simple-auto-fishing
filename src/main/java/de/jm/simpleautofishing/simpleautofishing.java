package de.jm.simpleautofishing;

import de.jm.simpleautofishing.mixin.FishingBobberEntityAccessorMixin;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(simpleautofishing.MODID)
public class simpleautofishing {

    public static final String MODID = "simpleautofishing";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static Minecraft client = Minecraft.getInstance();
    private int delay = 20;
    private boolean hookCastOut = true;

    public simpleautofishing() {
        LOGGER.info("Register simpleautofishing");
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (client.player == null) {
			return;
		}

        if (event.phase == TickEvent.Phase.START) {
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
		if (client.player.getMainHandItem().getItem() == Items.FISHING_ROD) {
			client.player.swing(InteractionHand.MAIN_HAND);
			client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
		} else {
			client.player.swing(InteractionHand.OFF_HAND);
			client.gameMode.useItem(client.player, InteractionHand.OFF_HAND);
		}
	}
}
