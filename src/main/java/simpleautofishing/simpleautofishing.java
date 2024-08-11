package simpleautofishing;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import simpleautofishing.mixin.FishingBobberEntityAccessorMixin;
// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(simpleautofishing.MODID)
public class simpleautofishing
{

    public static final String MODID = "simpleautofishing";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static Minecraft client = Minecraft.getInstance();
    private int delay = 20;
    private boolean hookCastOut = true;

    public simpleautofishing() {
        LOGGER.info("Register simpleautofishing");
        NeoForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null) {
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
