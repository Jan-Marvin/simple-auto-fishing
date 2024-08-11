package simpleautofishing;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class simpleautofishingMode {
    private static Minecraft client = Minecraft.getInstance();
    static int attackHelper, mode;
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void ModeChanger() {


        if (client.player.isCrouching() && (client.player.getMainHandItem().getItem() == Items.FISHING_ROD || client.player.getOffhandItem().getItem() == Items.FISHING_ROD) && AttackReleased()) {
            if (mode == 2) {
                mode = 0;
            } else {
                mode++;
            }

            if (mode == 0) {
                client.player.sendSystemMessage(Component.translatable("text.simpleautofishing.safMode.fishing_rod_unprotected"));
            } else if (mode == 1) {
                client.player.sendSystemMessage(Component.translatable("text.simpleautofishing.safMode.fishing_rod_protected"));
            } else if (mode == 2) {
                client.player.sendSystemMessage(Component.translatable("text.simpleautofishing.safMode.all_in_hotbar"));
            }
        }
    }

    public static boolean modeCheck() {
        if (mode == 0) {
            return true;
        }

        if (mode == 1) {
            if (client.player.getMainHandItem().getItem() == Items.FISHING_ROD && client.player.getMainHandItem().getDamageValue() <= client.player.getMainHandItem().getMaxDamage() - 3) {
                return true;
            }
            return false;
        }

        if (mode == 2 && client.player.getMainHandItem().getItem() != Items.FISHING_ROD) {
            for (int i = 0; i < 9; i++) {
                if (client.player.getMainHandItem().getItem() == Items.FISHING_ROD) {
                    return true;
                }
                client.player.getInventory().swapPaint(1D);
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