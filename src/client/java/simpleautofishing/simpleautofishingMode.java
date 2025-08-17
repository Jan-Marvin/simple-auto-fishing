package simpleautofishing;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class simpleautofishingMode {
    private static MinecraftClient client = MinecraftClient.getInstance();
    static int mode = 0;
    static int helper;
    static TagKey<Item> fishingRod = TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "tools/fishing_rod"));

    public static void ModeChanger() {

        if (client.player.isSneaking() && client.player.getMainHandStack().isIn(fishingRod) && AttackReleased()) {
            mode = (mode + 1) % 3;

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
                return true;
            } else if (client.player.getMainHandStack().isIn(fishingRod) && client.player.getOffHandStack().getDamage() >= client.player.getOffHandStack().getMaxDamage() - 3) {
                return true;
            }
            return false;
        }
        if (mode == 2 && !(client.player.getMainHandStack().isIn(fishingRod))) {
            for (int i = 0; i < 9; i++) {
                client.player.getInventory().setSelectedSlot(i);
                if (client.player.getMainHandStack().isIn(fishingRod)) {
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
