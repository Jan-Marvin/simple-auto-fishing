package simpleautofishing;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import org.slf4j.Logger;

import simpleautofishing.mixin.FishingBobberEntityAccessorMixin;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(simpleautofishing.MODID)
public class simpleautofishing {

    public static final String MODID = "simpleautofishing";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static Minecraft client;
    boolean reeledIn, stateAttackKeyReleased = false;
    FishingRodModes FishingRodMode = FishingRodModes.fishingRodUnprotected;
    private int delay = 0;
    public static int recastDelayTicks = 17;
    enum FishingRodModes {
        fishingRodUnprotected,
        fishingRodProtected,
        allInHotbar;

        public FishingRodModes next() {
            return values()[(ordinal() + 1) % values().length];
        }
    };

    public simpleautofishing() {
        LOGGER.info("Register simpleautofishing");
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void RegisterClientCommandsEvent(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(net.minecraft.commands.Commands.literal("saf")
                .then(net.minecraft.commands.Commands.literal("set")
                        .then(net.minecraft.commands.Commands.argument("delay", IntegerArgumentType.integer())
                                .executes(context -> {
                                    recastDelayTicks = IntegerArgumentType.getInteger(context, "delay");
                                    context.getSource().sendSuccess(
                                            () -> Component.translatable(
                                                    "text.simpleautofishing.cmd.recastDelayTicks",
                                                    recastDelayTicks
                                            ),
                                            false
                                    );
                                    return 1;
                                })
                        )
                )
        );
    }

    @SubscribeEvent
    public void ClientTickEvent(ClientTickEvent.Pre event)  {
        if (Minecraft.getInstance() == null) {
            return;
        } else if (Minecraft.getInstance() != null && client == null) {
            client = Minecraft.getInstance();
        }

        if (client.player == null) {
            return;
        }

        if (!isFishingRodEquipped()) {
            delay = 0;
            reeledIn = false;
            return;
        }

        if (client.player.isCrouching() && attackKeyReleased(client.options.keyAttack.isDown())) {
            FishingRodMode = FishingRodMode.next();
            if (FishingRodMode == FishingRodModes.fishingRodUnprotected) {
                client.player.sendOverlayMessage(Component.translatable("text.simpleautofishing.safMode.fishing_rod_unprotected"));
            } else if (FishingRodMode == FishingRodModes.fishingRodProtected) {
                client.player.sendOverlayMessage(Component.translatable("text.simpleautofishing.safMode.fishing_rod_protected"));
            } else if (FishingRodMode == FishingRodModes.allInHotbar) {
                client.player.sendOverlayMessage(Component.translatable("text.simpleautofishing.safMode.all_in_hotbar"));
            }
        }
        if (client.player.fishing != null && caughtFish(((FishingBobberEntityAccessorMixin) client.player.fishing).getBiting())) {
            useRod();
            reeledIn = true;
            delay = 0;
        }

        if (!reeledIn) {
            return;
        }

        if (delay > recastDelayTicks) {
            useRod();
            reeledIn = false;
            delay = 0;
        } else {
            delay++;
        }
    }

    public void useRod() {
        switch (FishingRodMode) {
            case FishingRodModes.fishingRodUnprotected:
                client.player.swing(InteractionHand.MAIN_HAND);
                client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
                break;
            case FishingRodModes.fishingRodProtected:
                if (client.player.getMainHandItem().getDamageValue() <= client.player.getMainHandItem().getMaxDamage() - 4) {
                    client.player.swing(InteractionHand.MAIN_HAND);
                    client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
                }
                break;
            case FishingRodModes.allInHotbar:
                client.player.swing(InteractionHand.MAIN_HAND);
                client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
                if (reeledIn) {
                    break;
                }
                if (isFishingRodEquipped() && client.player.getMainHandItem().getDamageValue() + 1 != client.player.getMainHandItem().getMaxDamage()) {
                    break;
                }
                int currentSlot = client.player.getInventory().getSelectedSlot();
                for (int i = 0; i < 9; i++) {
                    if (isFishingRodEquipped(client.player.getInventory().getItem(i)) && currentSlot != i) {
                        client.player.getInventory().setSelectedSlot(i);
                        break;
                    }
                }
        }
    }

    public static boolean isFishingRodEquipped() {
        if (client.getInstance().player.getMainHandItem().is(TagKey.create(Registries.ITEM, Identifier.parse("c:tools/fishing_rod")))) {
            return true;
        } else if (client.getInstance().player.getMainHandItem().getItem() == Items.FISHING_ROD) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isFishingRodEquipped(ItemStack stack) {
        if (stack.is(TagKey.create(Registries.ITEM, Identifier.parse("c:tools/fishing_rod")))) {
            return true;
        } else if (stack.getItem() == Items.FISHING_ROD) {
            return true;
        } else {
            return false;
        }
    }

    public boolean attackKeyReleased(boolean currentState) {
        boolean fallingEdge = stateAttackKeyReleased && !currentState;
        stateAttackKeyReleased = currentState;
        return fallingEdge;
    }
    public boolean caughtFish(boolean currentState) {
        boolean risingEdge = !stateAttackKeyReleased && currentState;
        stateAttackKeyReleased = currentState;
        return risingEdge;
    }
}
