package com.chaospharmacy;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public final class ChaosEvents {
	private ChaosEvents() {}

	/** 8 минут в тиках. */
	private static final int EVENT_PERIOD = 9600;

	public static void register() {
		// Нестабильный предохранитель + любой блок с "upgrad" в id (мод Upgrader и подобные) или кузнечный стол.
		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			ItemStack held = player.getStackInHand(hand);
			if (!held.isOf(ModItems.UNSTABLE_FUSE)) {
				return ActionResult.PASS;
			}
			Identifier id = Registries.BLOCK.getId(world.getBlockState(hit.getBlockPos()).getBlock());
			if (!isUpgradeBlock(id)) {
				return ActionResult.PASS;
			}
			if (!world.isClient && player instanceof ServerPlayerEntity sp) {
				MoreEffects.fuse(sp);
				if (!sp.getAbilities().creativeMode) {
					held.decrement(1);
				}
			}
			return ActionResult.SUCCESS;
		});

		// Случайное событие для каждого игрока раз в 8 минут.
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			int t = server.getTicks();
			if (t <= 0 || t % EVENT_PERIOD != 0) {
				return;
			}
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				MoreEffects.worldEvent(player);
			}
		});
	}

	private static boolean isUpgradeBlock(Identifier id) {
		return id.getNamespace().contains("upgrad")
				|| id.getPath().contains("upgrad")
				|| id.equals(new Identifier("minecraft", "smithing_table"));
	}
}
