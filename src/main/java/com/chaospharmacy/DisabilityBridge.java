package com.chaospharmacy;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Мост к моду Disabilities. Работает только через его публичные команды
 * (/disability give|remove|next|list), поэтому не зависит от внутренностей мода
 * и не ломается при его обновлениях. Если мода нет - все методы возвращают false.
 */
public final class DisabilityBridge {
	public static final String MOD_ID = "disabilities";

	private DisabilityBridge() {}

	public static boolean isPresent() {
		return FabricLoader.getInstance().isModLoaded(MOD_ID);
	}

	/** what: id болезни, "random" или "all" */
	public static boolean give(ServerPlayerEntity p, String what) {
		return run(p, "disability give " + p.getGameProfile().getName() + " " + what);
	}

	public static boolean remove(ServerPlayerEntity p, String what) {
		return run(p, "disability remove " + p.getGameProfile().getName() + " " + what);
	}

	/** Пропустить таймер и сразу получить следующую болезнь. */
	public static boolean next(ServerPlayerEntity p) {
		return run(p, "disability next " + p.getGameProfile().getName());
	}

	public static boolean list(ServerPlayerEntity p) {
		return run(p, "disability list " + p.getGameProfile().getName());
	}

	private static boolean run(ServerPlayerEntity p, String command) {
		if (!isPresent()) {
			return false;
		}
		MinecraftServer server = p.getServer();
		if (server == null) {
			return false;
		}
		// Права 4, чтобы предметы работали и у обычных игроков, не только у операторов.
		ServerCommandSource source = p.getCommandSource().withLevel(4);
		server.getCommandManager().executeWithPrefix(source, command);
		return true;
	}
}
