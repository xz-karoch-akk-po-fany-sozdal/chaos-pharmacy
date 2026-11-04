package com.chaospharmacy;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class ModBlocks {
	private ModBlocks() {}

	public static final Block CHAOS_MACHINE = new ChaosMachineBlock(
			FabricBlockSettings.create().strength(3.0f, 6.0f).sounds(BlockSoundGroup.METAL).requiresTool());

	public static BlockItem CHAOS_MACHINE_ITEM;

	public static void register() {
		Identifier id = new Identifier(ChaosPharmacy.MOD_ID, "chaos_machine");
		Registry.register(Registries.BLOCK, id, CHAOS_MACHINE);
		CHAOS_MACHINE_ITEM = Registry.register(Registries.ITEM, id,
				new BlockItem(CHAOS_MACHINE, new Item.Settings()));
	}
}
