package simpleautofishing;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import com.mojang.logging.LogUtils;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;

import org.slf4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
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
		allInHotbar,
		allInHotbarProtected;

		public FishingRodModes next() {
			return values()[(ordinal() + 1) % values().length];
		}
	};
	private static java.lang.reflect.Field bitingField = null;

	public simpleautofishing(FMLJavaModLoadingContext context) {
		LOGGER.info("Register simpleautofishing");
		TickEvent.ClientTickEvent.Pre.BUS.addListener(this::onTickEvent);
		RegisterClientCommandsEvent.BUS.addListener(this::RegisterClientCommandsEvent);
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
	public void onTickEvent(TickEvent.ClientTickEvent.Pre event) {
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
			} else if (FishingRodMode == FishingRodModes.allInHotbarProtected) {
				client.player.sendOverlayMessage(Component.translatable("text.simpleautofishing.safMode.all_in_hotbar_protected"));
			}
		}

		if (client.player.fishing != null && caughtFish(isBiting((net.minecraft.world.entity.projectile.FishingHook) client.player.fishing))) {
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
			case FishingRodModes.allInHotbarProtected:
				ItemStack currentRod = client.player.getItemInHand(InteractionHand.MAIN_HAND);
				if (currentRod.getDamageValue() > currentRod.getMaxDamage() - 4) {
					int activeSlot = client.player.getInventory().getSelectedSlot();
					boolean switched = false;
					for (int i = 0; i < 9; i++) {
						if (i == activeSlot) continue;
						ItemStack candidate = client.player.getInventory().getItem(i);
						if (isFishingRodEquipped(candidate) && candidate.getDamageValue() <= candidate.getMaxDamage() - 4) {
							client.player.getInventory().setSelectedSlot(i);
							switched = true;
							break;
						}
					}
					if (!switched) break;
				}
				client.player.swing(InteractionHand.MAIN_HAND);
				client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
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

	private static boolean isBiting(net.minecraft.world.entity.projectile.FishingHook hook) {
		try {
			if (bitingField == null) {
				bitingField = net.minecraft.world.entity.projectile.FishingHook.class.getDeclaredField("biting");
				bitingField.setAccessible(true);
			}
			return (boolean) bitingField.get(hook);
		} catch (Exception e) {
			return false;
		}
	}

}
