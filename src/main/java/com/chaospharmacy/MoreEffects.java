package com.chaospharmacy;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Эффекты предметов из «Chaos Expansion» (идеи ChatGPT) и «Нестабильного предохранителя» (идея Gemini). */
public final class MoreEffects {
	private MoreEffects() {}

	/** Глитч-печенье: перемешивает хотбар. */
	public static void glitchCookie(ServerPlayerEntity p) {
		List<ItemStack> hotbar = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			hotbar.add(p.getInventory().getStack(i));
		}
		Collections.shuffle(hotbar, new java.util.Random());
		for (int i = 0; i < 9; i++) {
			p.getInventory().setStack(i, hotbar.get(i));
		}
		p.getServerWorld().playSound(null, p.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(),
				SoundCategory.PLAYERS, 1.0f, 0.5f);
		Msg.say(p, "cookie.glitch", Formatting.GREEN);
	}

	/** Проклятые кости: 6 исходов, на «5» ещё и случайная болезнь. */
	public static void dice(ServerPlayerEntity p) {
		int roll = p.getRandom().nextInt(6);
		switch (roll) {
			case 0 -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 100, 1));
			case 1 -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 1200, 2));
			case 2 -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 160, 0));
			case 3 -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 500, 2));
			case 4 -> {
				p.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 300, 3));
				ChaosEffects.giveDisability(p, "random");
			}
			default -> p.getHungerManager().add(6, 1.0f);
		}
		p.getServerWorld().playSound(null, p.getBlockPos(), SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON,
				SoundCategory.PLAYERS, 1.0f, 1.4f);
		Msg.chat(p, "dice." + roll, roll == 1 || roll == 3 ? Formatting.GREEN : Formatting.LIGHT_PURPLE);
	}

	/** Подозрительное яблоко: польза, вред, а иногда и лечение. */
	public static void chaosApple(ServerPlayerEntity p) {
		switch (p.getRandom().nextInt(5)) {
			case 0 -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 300, 1));
			case 1 -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 180, 1));
			case 2 -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 600, 1));
			case 3 -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 300, 0));
			default -> ChaosEffects.removeDisability(p, "random");
		}
		Msg.say(p, "apple.msg", Formatting.YELLOW);
	}

	/** Резиновая курица. */
	public static void chicken(ServerPlayerEntity p) {
		ServerWorld w = p.getServerWorld();
		w.playSound(null, p.getBlockPos(), SoundEvents.ENTITY_CHICKEN_AMBIENT, SoundCategory.PLAYERS,
				1.8f, 0.45f + p.getRandom().nextFloat() * 0.3f);
		Msg.say(p, "chicken.honk", Formatting.WHITE);
	}

	/** Нестабильный предохранитель, использованный на блоке-апгрейдере. */
	public static void fuse(ServerPlayerEntity p) {
		ServerWorld w = p.getServerWorld();
		int roll = p.getRandom().nextInt(4);
		switch (roll) {
			case 0 -> {
				p.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200, 0));
				p.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 600, 2));
			}
			case 1 -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 100, 1));
			case 2 -> {
				w.createExplosion(null, p.getX(), p.getY(), p.getZ(), 0.0f, World.ExplosionSourceType.NONE);
				w.spawnParticles(ParticleTypes.EXPLOSION, p.getX(), p.getY() + 1, p.getZ(), 3, 0.5, 0.5, 0.5, 0.0);
				p.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 1200, 3));
			}
			default -> ChaosEffects.giveDisability(p, "random");
		}
		Msg.chat(p, "fuse." + roll, Formatting.YELLOW);
	}

	/** Мировое событие раз в ~8 минут. */
	public static void worldEvent(ServerPlayerEntity p) {
		int event = p.getRandom().nextInt(5);
		if (event == 4 && !DisabilityBridge.isPresent()) {
			event = 3;
		}
		switch (event) {
			case 0 -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 500, 2));
			case 1 -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 300, 0));
			case 2 -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 160, 0));
			case 3 -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 400, 0));
			default -> DisabilityBridge.remove(p, "random");
		}
		Msg.chat(p, "event." + event, Formatting.LIGHT_PURPLE);
	}
}
