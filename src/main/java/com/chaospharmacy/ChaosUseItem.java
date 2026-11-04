package com.chaospharmacy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.function.Consumer;

/** Предмет, который срабатывает по правому клику (без поедания). */
public class ChaosUseItem extends Item {
	public enum Wear { CONSUME, DAMAGE, NONE }

	private final Consumer<ServerPlayerEntity> action;
	private final Wear wear;
	private final int cooldownTicks;

	public ChaosUseItem(Settings settings, Wear wear, int cooldownTicks, Consumer<ServerPlayerEntity> action) {
		super(settings);
		this.action = action;
		this.wear = wear;
		this.cooldownTicks = cooldownTicks;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (!world.isClient && user instanceof ServerPlayerEntity player) {
			action.accept(player);
			if (cooldownTicks > 0) {
				player.getItemCooldownManager().set(this, cooldownTicks);
			}
			if (!player.getAbilities().creativeMode) {
				switch (wear) {
					case CONSUME -> stack.decrement(1);
					case DAMAGE -> stack.damage(1, player, p -> p.sendToolBreakStatus(hand));
					case NONE -> { }
				}
			}
		}
		return TypedActionResult.success(stack, world.isClient());
	}
}
