package simpleautofishing;


import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import simpleautofishing.mixin.FishingBobberEntityAccessorMixin;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class simpleautofishing implements ClientModInitializer {
	private static Minecraft client;
	public static boolean enabled = true;
	public static final Logger LOGGER = LoggerFactory.getLogger("simpleautofishing");
	FishingRodModes FishingRodMode = FishingRodModes.fishingRodUnprotected;
	int delay = 0;
	public static int recastDelayTicks = 17;
	boolean reeledIn, stateAttackKeyReleased = false;
	enum FishingRodModes {
		fishingRodUnprotected,
		fishingRodProtected,
		allInHotbar,
		allInHotbarProtected;

		public FishingRodModes next() {
			return values()[(ordinal() + 1) % values().length];
		}
	};

	@Override
	public void onInitializeClient() {
		LOGGER.info("Registering simpleautofishing!");
		ClientTickEvents.START_CLIENT_TICK.register(this::onTick);

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommands.literal("saf")
					.then(ClientCommands.literal("toggle")
							.executes(context -> {
								enabled = !enabled;
								if (enabled) {
									context.getSource().sendFeedback(Component.translatable("text.simpleautofishing.cmd.enabled"));
								} else {
									context.getSource().sendFeedback(Component.translatable("text.simpleautofishing.cmd.disabled"));
								}
								return 1;
							})
					)
					.then(ClientCommands.literal("set")
							.then(ClientCommands.argument("delay", IntegerArgumentType.integer())
									.executes(context -> {
										recastDelayTicks = IntegerArgumentType.getInteger(context, "delay");
										context.getSource().sendFeedback(Component.translatable("text.simpleautofishing.cmd.recastDelayTicks", recastDelayTicks));
										return 1;
									})
							)
					)
			);
		});
	}

	private void onTick(Minecraft _client) {
		if (!enabled) {
			return;
		}

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
				if (client.player.getItemInHand(InteractionHand.MAIN_HAND).getDamageValue() <= client.player.getItemInHand(InteractionHand.MAIN_HAND).getMaxDamage() - 4) {
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
				if (isFishingRodEquipped() && client.player.getItemInHand(InteractionHand.MAIN_HAND).getDamageValue() + 1 != client.player.getItemInHand(InteractionHand.MAIN_HAND).getMaxDamage()) {
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
		if (client.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).is(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "tools/fishing_rod")))) {
			return true;
		} else if (client.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.FISHING_ROD) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isFishingRodEquipped(ItemStack stack) {
		if (stack.is(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "tools/fishing_rod")))) {
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
