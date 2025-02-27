package com.sheath.bettermining.dataGen.generator;

import com.sheath.bettermining.init.EnchantmentInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class EnchantmentGenerator extends FabricDynamicRegistryProvider {
    public EnchantmentGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
        System.out.println("Generating Vein Miner enchantment data...");
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registeries, Entries entries) {
        RegistryWrapper<Item> itemLookup = registeries.getOrThrow(RegistryKeys.ITEM);

        // Register the enchantment "Vein Miner"
        entries.add(EnchantmentInit.VEIN_MINER, Enchantment.builder(
                        Enchantment.definition(
                                itemLookup.getOrThrow(ItemTags.PICKAXES),
                                5, // Weight
                                1, // Max level
                                Enchantment.leveledCost(15, 5), // Min cost
                                Enchantment.leveledCost(30, 10), // Max cost
                                5, // Anvil cost (removed Operation)
                                AttributeModifierSlot.HAND
                        )).build(EnchantmentInit.VEIN_MINER.getValue())
        );
    }


    @Override
    public String getName() {
        return "BetterMiningEnchantmentGenerator";
    }
}
