package com.chaospharmacy;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.function.Consumer;

/** Съедобный предмет, который после поедания запускает эффект. */
public class ChaosFoodItem extends Item {
	private final Consumer<ServerPlayerEntity> onEaten;

	public ChaosFoodItem(Settings settings, Consumer<ServerPlayerEntity> onEaten) {
		super(settings);
		this.onEaten = onEaten;
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (!world.isClient && user instanceof ServerPlayerEntity player) {
			onEaten.accept(player);
		}
		return super.finishUsing(stack, world, user);
	}
}
