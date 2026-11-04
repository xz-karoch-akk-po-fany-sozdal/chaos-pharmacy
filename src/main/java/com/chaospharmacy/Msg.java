package com.chaospharmacy;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class Msg {
	private Msg() {}

	/** Короткое сообщение над хотбаром. */
	public static void say(ServerPlayerEntity p, String key, Formatting color) {
		p.sendMessage(Text.translatable("message." + ChaosPharmacy.MOD_ID + "." + key).formatted(color), true);
	}

	/** Сообщение в чат. */
	public static void chat(ServerPlayerEntity p, String key, Formatting color) {
		p.sendMessage(Text.translatable("message." + ChaosPharmacy.MOD_ID + "." + key).formatted(color), false);
	}

	/** Случайная реплика base.0 ... base.(count-1). */
	public static void pick(ServerPlayerEntity p, String base, int count, Formatting color, boolean overlay) {
		String key = "message." + ChaosPharmacy.MOD_ID + "." + base + "." + p.getRandom().nextInt(count);
		p.sendMessage(Text.translatable(key).formatted(color), overlay);
	}
}
