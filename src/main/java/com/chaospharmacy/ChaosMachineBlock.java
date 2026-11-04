package com.chaospharmacy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Автомат Хаоса: кидаешь изумруд - получаешь... что-нибудь. */
public class ChaosMachineBlock extends Block {
	private static final Item[] LOOT = {
			Items.DIAMOND, Items.GOLDEN_APPLE, Items.COOKED_BEEF, Items.EXPERIENCE_BOTTLE,
			Items.ENDER_PEARL, Items.IRON_INGOT, Items.BREAD, Items.GOLD_INGOT, Items.COOKIE
	};
	private static final Item[] TRASH = {
			Items.ROTTEN_FLESH, Items.STICK, Items.DIRT, Items.DEAD_BUSH,
			Items.POISONOUS_POTATO, Items.BOWL, Items.SPIDER_EYE, Items.PUFFERFISH
	};

	public ChaosMachineBlock(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		}
		if (!(player instanceof ServerPlayerEntity p)) {
			return ActionResult.PASS;
		}
		ServerWorld sw = (ServerWorld) world;
		ItemStack held = p.getStackInHand(hand);

		if (!held.isOf(Items.EMERALD)) {
			sw.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.BLOCKS, 1.0f, 0.6f);
			Msg.say(p, "machine.need", Formatting.RED);
			return ActionResult.CONSUME;
		}
		if (!p.getAbilities().creativeMode) {
			held.decrement(1);
		}

		sw.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0f, 0.8f);
		double x = pos.getX() + 0.5, y = pos.getY() + 1.1, z = pos.getZ() + 0.5;
		int r = p.getRandom().nextInt(100);

		if (r < 4) {
			// ДЖЕКПОТ
			p.getInventory().offerOrDrop(new ItemStack(Items.DIAMOND, 3));
			ChaosEffects.removeDisability(p, "all");
			sw.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, x, y, z, 40, 0.4, 0.4, 0.4, 0.3);
			sw.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0f, 1.0f);
			Msg.chat(p, "machine.jackpot", Formatting.GOLD);
		} else if (r < 28) {
			p.getInventory().offerOrDrop(new ItemStack(LOOT[p.getRandom().nextInt(LOOT.length)]));
			sw.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 10, 0.3, 0.3, 0.3, 0.0);
			Msg.say(p, "machine.loot", Formatting.GREEN);
		} else if (r < 48) {
			p.getInventory().offerOrDrop(new ItemStack(TRASH[p.getRandom().nextInt(TRASH.length)]));
			sw.spawnParticles(ParticleTypes.SMOKE, x, y, z, 10, 0.2, 0.2, 0.2, 0.02);
			Msg.say(p, "machine.trash", Formatting.GRAY);
		} else if (r < 66) {
			Item pill = switch (p.getRandom().nextInt(5)) {
				case 0, 1 -> ModItems.CURE_PILL;
				case 2, 3 -> ModItems.OOPS_PILL;
				default -> ModItems.BANANA;
			};
			p.getInventory().offerOrDrop(new ItemStack(pill));
			Msg.say(p, "machine.pill", Formatting.LIGHT_PURPLE);
		} else if (r < 78) {
			ChaosEffects.giveDisability(p, "random");
			sw.spawnParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 6, 0.3, 0.3, 0.3, 0.0);
			Msg.say(p, "machine.bad", Formatting.RED);
		} else if (r < 90) {
			ChaosEffects.removeDisability(p, "random");
			sw.spawnParticles(ParticleTypes.HEART, x, y, z, 6, 0.3, 0.3, 0.3, 0.0);
			Msg.say(p, "machine.good", Formatting.GREEN);
		} else {
			ChaosEffects.funEffect(p);
			sw.spawnParticles(ParticleTypes.NOTE, x, y, z, 8, 0.3, 0.3, 0.3, 1.0);
		}
		return ActionResult.CONSUME;
	}
}
