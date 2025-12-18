package simpleautofishing;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class simpleautofishingMode {
    static int attackHelper, mode;
    public static final Logger LOGGER = LogUtils.getLogger();
    static TagKey<Item> fishingRod = TagKey.create(Registries.ITEM, Identifier.parse("c:tools/fishing_rod"));

    public static void ModeChanger() {

        if (Minecraft.getInstance().player.isCrouching() && ((Minecraft.getInstance().player.getMainHandItem().is(fishingRod) || Minecraft.getInstance().player.getOffhandItem().is(fishingRod)) && AttackReleased())) {
            mode = (mode + 1) % 3;

            if (mode == 0) {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("text.simpleautofishing.safMode.fishing_rod_unprotected"), true);
            } else if (mode == 1) {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("text.simpleautofishing.safMode.fishing_rod_protected"), true);
            } else if (mode == 2) {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("text.simpleautofishing.safMode.all_in_hotbar"), true);
            }
        }
    }

    public static boolean modeCheck() {
        if (mode == 0) {
            return true;
        }

        if (mode == 1) {
            if (Minecraft.getInstance().player.getMainHandItem().is(fishingRod) && Minecraft.getInstance().player.getMainHandItem().getDamageValue() <= Minecraft.getInstance().player.getMainHandItem().getMaxDamage() - 3) {
                return true;
            }
            return false;
        }

        if (mode == 2 && !Minecraft.getInstance().player.getMainHandItem().is(fishingRod)) {
            for (int i = 0; i < 9; i++) {
                Minecraft.getInstance().player.getInventory().setSelectedSlot(i);
                if (Minecraft.getInstance().player.getMainHandItem().is(fishingRod)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public static boolean AttackReleased() {
        if (Minecraft.getInstance().options.keyAttack.isDown()) {
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
