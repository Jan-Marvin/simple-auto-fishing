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

        if (Minecraft.getInstance().player.fishing == null && !hookCastOut && delay == 15) {
            UseRod();
            hookCastOut = true;
        } else if (Minecraft.getInstance().player.fishing == null && !hookCastOut && delay != 15) {
            delay++;
        }

        if (Minecraft.getInstance().player.fishing != null && ((FishingBobberEntityAccessorMixin) Minecraft.getInstance().player.fishing).getBiting()) {
            UseRod();
            delay = 0;
            hookCastOut = false;
        }
    }

    public void UseRod() {

        if (!simpleautofishingMode.modeCheck()) {
            return;
        }
        if (Minecraft.getInstance().player.getMainHandItem().getItem() == Items.FISHING_ROD) {
            Minecraft.getInstance().player.swing(InteractionHand.MAIN_HAND);
            Minecraft.getInstance().gameMode.useItem(Minecraft.getInstance().player, InteractionHand.MAIN_HAND);
        } else {
            Minecraft.getInstance().player.swing(InteractionHand.OFF_HAND);
            Minecraft.getInstance().gameMode.useItem(Minecraft.getInstance().player, InteractionHand.OFF_HAND);
        }
    }

}
