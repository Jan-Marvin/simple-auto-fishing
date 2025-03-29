package simpleautofishing;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class simpleautofishingMode {
    static int attackHelper, mode;
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void ModeChanger() {


        if (Minecraft.getInstance().player.isCrouching() && (Minecraft.getInstance().player.getMainHandItem().getItem() == Items.FISHING_ROD || Minecraft.getInstance().player.getOffhandItem().getItem() == Items.FISHING_ROD) && AttackReleased()) {
            if (mode == 2) {
                mode = 0;
            } else {
                mode++;
            }

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
            if (Minecraft.getInstance().player.getMainHandItem().getItem() == Items.FISHING_ROD && Minecraft.getInstance().player.getMainHandItem().getDamageValue() <= Minecraft.getInstance().player.getMainHandItem().getMaxDamage() - 3) {
                return true;
            }
            return false;
        }

        if (mode == 2 && Minecraft.getInstance().player.getMainHandItem().getItem() != Items.FISHING_ROD) {
            for (int i = 0; i < 9; i++) {
                Minecraft.getInstance().player.getInventory().setSelectedSlot(i);
                if (Minecraft.getInstance().player.getMainHandItem().getItem() == Items.FISHING_ROD) {
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