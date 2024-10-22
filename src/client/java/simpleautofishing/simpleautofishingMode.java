package simpleautofishing;

import simpleautofishing.simpleautofishing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class simpleautofishingMode {
    private static MinecraftClient client = MinecraftClient.getInstance();
    static int mode = 0;
    static int helper;
    public static void ModeChanger() {

        if (client.player.isSneaking() && client.player.getMainHandStack().getItem() == Items.FISHING_ROD && AttackReleased()) {
            if (mode == 2) {
                mode = 0;
            } else {
                mode++;
            }
            if (mode == 0) {
                client.player.sendMessage(Text.translatable("text.simpleautofishing.safMode.fishing_rod_unprotected"), true);
            } else if (mode == 1) {
                client.player.sendMessage(Text.translatable("text.simpleautofishing.safMode.fishing_rod_protected"), true);
            } else if (mode == 2) {
                client.player.sendMessage(Text.translatable("text.simpleautofishing.safMode.all_in_hotbar"), true);
            }
        }
    }

    public static boolean modeCheck() {
        if (mode == 1) {
            if (client.player.getMainHandStack().getDamage() >= client.player.getMainHandStack().getMaxDamage() - 3) {
                return false;
            } else if (client.player.getMainHandStack().getItem() != Items.FISHING_ROD && client.player.getOffHandStack().getDamage() >= client.player.getOffHandStack().getMaxDamage() - 3) {
                return false;
            }
        }
        if (mode == 2 && client.player.getMainHandStack().getItem() != Items.FISHING_ROD) {
            for (int i = 0; i < 9; i++) {
                client.player.getInventory().setSelectedSlot(i);
                if (client.player.getMainHandStack().getItem() == Items.FISHING_ROD) {
                    return true;
                }

            }
            return false;
        }
        return true;
    }

    public static boolean AttackReleased() {
        if (client.options.attackKey.isPressed()) {
            helper = 1;
            return false;
        }
        if (helper == 1) {
            helper = 0;
            return true;
        }
        return false;
    }
}
