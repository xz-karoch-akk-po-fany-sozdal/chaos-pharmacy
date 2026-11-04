package com.chaospharmacy;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public final class ModItems {
	private ModItems() {}

	private static FoodComponent pillFood() {
		return new FoodComponent.Builder().hunger(0).saturationModifier(0f).alwaysEdible().snack().build();
	}

	// ---------- Аптека ----------
	public static final Item CURE_PILL = new ChaosFoodItem(
			new FabricItemSettings().maxCount(16).food(pillFood()), ChaosEffects::curePill);

	public static final Item OOPS_PILL = new ChaosFoodItem(
			new FabricItemSettings().maxCount(16).food(pillFood()), ChaosEffects::oopsPill);

	public static final Item BANANA = new ChaosFoodItem(
			new FabricItemSettings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.4f).alwaysEdible().build()),
			ChaosEffects::banana);

	public static final Item PANACEA = new ChaosUseItem(
			new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC),
			ChaosUseItem.Wear.CONSUME, 40, ChaosEffects::panacea);

	public static final Item HOURGLASS = new ChaosUseItem(
			new FabricItemSettings().maxDamage(5).rarity(Rarity.RARE),
			ChaosUseItem.Wear.DAMAGE, 100, ChaosEffects::hourglass);

	public static final Item LOTTERY_TICKET = new ChaosUseItem(
			new FabricItemSettings().maxCount(16).rarity(Rarity.UNCOMMON),
			ChaosUseItem.Wear.CONSUME, 20, ChaosEffects::ticket);

	public static final Item RUBBER_DUCK = new ChaosUseItem(
			new FabricItemSettings().maxCount(1),
			ChaosUseItem.Wear.NONE, 30, ChaosEffects::duck);

	// ---------- Chaos Expansion (идеи ChatGPT) ----------
	public static final Item CHAOS_SHARD = new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON));

	public static final Item GLITCH_COOKIE = new ChaosFoodItem(
			new FabricItemSettings().food(new FoodComponent.Builder().hunger(5).saturationModifier(0.4f).alwaysEdible().build()),
			MoreEffects::glitchCookie);

	public static final Item CURSED_DICE = new ChaosUseItem(
			new FabricItemSettings().maxCount(16),
			ChaosUseItem.Wear.CONSUME, 10, MoreEffects::dice);

	public static final Item RUBBER_CHICKEN = new ChaosUseItem(
			new FabricItemSettings().maxCount(1),
			ChaosUseItem.Wear.NONE, 30, MoreEffects::chicken);

	public static final Item CHAOS_APPLE = new ChaosFoodItem(
			new FabricItemSettings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.3f).alwaysEdible().build()),
			MoreEffects::chaosApple);

	// ---------- Мост к Upgrader (идея Gemini) ----------
	public static final Item UNSTABLE_FUSE = new Item(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1));

	private static void item(String name, Item item) {
		Registry.register(Registries.ITEM, new Identifier(ChaosPharmacy.MOD_ID, name), item);
	}

	public static void register() {
		item("cure_pill", CURE_PILL);
		item("oops_pill", OOPS_PILL);
		item("banana", BANANA);
		item("panacea", PANACEA);
		item("hourglass", HOURGLASS);
		item("lottery_ticket", LOTTERY_TICKET);
		item("rubber_duck", RUBBER_DUCK);
		item("chaos_shard", CHAOS_SHARD);
		item("glitch_cookie", GLITCH_COOKIE);
		item("cursed_dice", CURSED_DICE);
		item("rubber_chicken", RUBBER_CHICKEN);
		item("chaos_apple", CHAOS_APPLE);
		item("unstable_fuse", UNSTABLE_FUSE);

		ItemGroup group = FabricItemGroup.builder()
				.icon(() -> new ItemStack(CURE_PILL))
				.displayName(Text.translatable("itemGroup." + ChaosPharmacy.MOD_ID))
				.entries((context, entries) -> {
					entries.add(CURE_PILL);
					entries.add(OOPS_PILL);
					entries.add(PANACEA);
					entries.add(HOURGLASS);
					entries.add(LOTTERY_TICKET);
					entries.add(BANANA);
					entries.add(RUBBER_DUCK);
					entries.add(CHAOS_SHARD);
					entries.add(GLITCH_COOKIE);
					entries.add(CURSED_DICE);
					entries.add(RUBBER_CHICKEN);
					entries.add(CHAOS_APPLE);
					entries.add(UNSTABLE_FUSE);
					entries.add(ModBlocks.CHAOS_MACHINE_ITEM);
				})
				.build();
		Registry.register(Registries.ITEM_GROUP, new Identifier(ChaosPharmacy.MOD_ID, "main"), group);
	}
}
