package com.chaospharmacy;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

import java.util.List;

/** Вся логика «что происходит, когда ты это съел / нажал». */
public final class ChaosEffects {
	private ChaosEffects() {}

	private record Fx(StatusEffect effect, int duration, int amplifier, String key) {}

	private static final List<Fx> FUN = List.of(
			new Fx(StatusEffects.LEVITATION, 100, 0, "fx.levitation"),
			new Fx(StatusEffects.JUMP_BOOST, 400, 4, "fx.jump"),
			new Fx(StatusEffects.GLOWING, 600, 0, "fx.glow"),
			new Fx(StatusEffects.SLOW_FALLING, 1200, 0, "fx.feather"),
			new Fx(StatusEffects.SPEED, 400, 2, "fx.speed"),
			new Fx(StatusEffects.NIGHT_VISION, 1200, 0, "fx.night"),
			new Fx(StatusEffects.DOLPHINS_GRACE, 1200, 0, "fx.dolphin")
	);

	private static final List<Fx> BAD_FALLBACK = List.of(
			new Fx(StatusEffects.NAUSEA, 300, 0, "fallback.nausea"),
			new Fx(StatusEffects.SLOWNESS, 300, 1, "fallback.slow"),
			new Fx(StatusEffects.BLINDNESS, 120, 0, "fallback.blind"),
			new Fx(StatusEffects.MINING_FATIGUE, 400, 1, "fallback.tired")
	);

	// ---------- работа с Disabilities (с запасным вариантом без него) ----------

	public static void giveDisability(ServerPlayerEntity p, String what) {
		if (DisabilityBridge.give(p, what)) {
			return;
		}
		Fx fx = BAD_FALLBACK.get(p.getRandom().nextInt(BAD_FALLBACK.size()));
		p.addStatusEffect(new StatusEffectInstance(fx.effect, fx.duration, fx.amplifier));
		Msg.say(p, fx.key, Formatting.RED);
	}

	public static void removeDisability(ServerPlayerEntity p, String what) {
		if (DisabilityBridge.remove(p, what)) {
			return;
		}
		p.clearStatusEffects();
		Msg.say(p, "fallback.cleared", Formatting.GREEN);
	}

	// ---------- предметы ----------

	public static void curePill(ServerPlayerEntity p) {
		removeDisability(p, "random");
		Msg.say(p, "cure.ok", Formatting.GREEN);
	}

	public static void oopsPill(ServerPlayerEntity p) {
		giveDisability(p, "random");
		// компенсация за риск
		p.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 1));
		p.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0));
		Msg.pick(p, "oops", 6, Formatting.LIGHT_PURPLE, true);
	}

	public static void panacea(ServerPlayerEntity p) {
		removeDisability(p, "all");
		p.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 600, 0));
		p.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
		p.getServerWorld().sendEntityStatus(p, EntityStatuses.USE_TOTEM_OF_UNDYING);
		Msg.say(p, "panacea.ok", Formatting.GOLD);
	}

	public static void hourglass(ServerPlayerEntity p) {
		if (!DisabilityBridge.next(p)) {
			p.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 400, 1));
			Msg.say(p, "hourglass.fallback", Formatting.YELLOW);
		} else {
			Msg.say(p, "hourglass.ok", Formatting.YELLOW);
		}
	}

	public static void banana(ServerPlayerEntity p) {
		int r = p.getRandom().nextInt(100);
		if (r < 40) {
			p.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0));
			p.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
			Msg.say(p, "banana.slip", Formatting.YELLOW);
		} else if (r < 70) {
			p.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 300, 1));
			Msg.say(p, "banana.speed", Formatting.GREEN);
		} else if (r < 90) {
			p.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
			Msg.say(p, "banana.regen", Formatting.GREEN);
		} else {
			funEffect(p);
		}
	}

	public static void ticket(ServerPlayerEntity p) {
		int r = p.getRandom().nextInt(100);
		if (r < 3) {
			giveDisability(p, "all");
			p.getServerWorld().playSound(null, p.getBlockPos(), SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 0.5f, 1.5f);
			Msg.chat(p, "ticket.cursed", Formatting.DARK_RED);
		} else if (r < 8) {
			removeDisability(p, "all");
			p.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 6000, 2));
			p.getInventory().offerOrDrop(new ItemStack(Items.EMERALD, 3));
			p.getServerWorld().sendEntityStatus(p, EntityStatuses.USE_TOTEM_OF_UNDYING);
			Msg.chat(p, "ticket.jackpot", Formatting.GOLD);
		} else if (r < 35) {
			removeDisability(p, "random");
			Msg.say(p, "ticket.win", Formatting.GREEN);
		} else if (r < 65) {
			giveDisability(p, "random");
			Msg.say(p, "ticket.lose", Formatting.RED);
		} else if (r < 85) {
			Msg.say(p, "ticket.nothing", Formatting.GRAY);
		} else {
			funEffect(p);
		}
	}

	public static void funEffect(ServerPlayerEntity p) {
		Fx fx = FUN.get(p.getRandom().nextInt(FUN.size()));
		p.addStatusEffect(new StatusEffectInstance(fx.effect, fx.duration, fx.amplifier));
		Msg.say(p, fx.key, Formatting.AQUA);
	}

	/** Резиновая утка: квак, цитата, подсветка всех вокруг. С Shift - показывает список болезней. */
	public static void duck(ServerPlayerEntity p) {
		ServerWorld world = p.getServerWorld();
		world.playSound(null, p.getBlockPos(), SoundEvents.ENTITY_CHICKEN_HURT, SoundCategory.PLAYERS, 1.0f, 1.8f);
		if (p.isSneaking()) {
			if (!DisabilityBridge.list(p)) {
				Msg.say(p, "duck.nolist", Formatting.GRAY);
			}
			return;
		}
		Msg.pick(p, "duck", 12, Formatting.YELLOW, false);
		var near = world.getEntitiesByClass(LivingEntity.class, p.getBoundingBox().expand(12), e -> e != p);
		for (LivingEntity e : near) {
			e.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100, 0));
		}
	}
}
