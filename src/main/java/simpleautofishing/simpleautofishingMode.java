package simpleautofishing;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class simpleautofishingMode {
    private static Minecraft client = Minecraft.getInstance();
    static int attackHelper, mode;
    public static final Logger LOGGER = LogUtils.getLogger();
    static TagKey<Item> fishingRod = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:tools/fishing_rod"));

    public static void ModeChanger() {

        
        if (client.player.isCrouching() && (client.player.getMainHandItem().is(fishingRod) || client.player.getOffhandItem().is(fishingRod)) && AttackReleased()) {
            mode = (mode + 1) % 3;
			
            if (mode == 0) {
                client.player.displayClientMessage(Component.translatable("text.simpleautofishing.safMode.fishing_rod_unprotected"), true);
            } else if (mode == 1) {
                client.player.displayClientMessage(Component.translatable("text.simpleautofishing.safMode.fishing_rod_protected"), true);
            } else if (mode == 2) {
                client.player.displayClientMessage(Component.translatable("text.simpleautofishing.safMode.all_in_hotbar"), true);
            }
        }
    }

    public static boolean modeCheck() {
        if (mode == 0) {
            return true;
        }

        if (mode == 1) {
            if (client.player.getMainHandItem().is(fishingRod) && client.player.getMainHandItem().getDamageValue() <= client.player.getMainHandItem().getMaxDamage() - 3) {
                return true;
            }
            return false;
        }

        if (mode == 2 && (client.player.getMainHandItem().is(fishingRod))) {
            for (int i = 0; i < 9; i++) {
                client.player.getInventory().setSelectedSlot(i);
                if (client.player.getMainHandItem().is(fishingRod)) {
                    return true;
                }

            }
            return false;
        }
        return true;
    }

	public static boolean AttackReleased() {
		if (client.options.keyAttack.isDown()) {
			attackHelper = 1;
			return false;
		}
		if (attackHelper == 1) {
			attackHelper = 0;
			return true;
		}
		return false;
	}
}
