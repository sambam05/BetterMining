package com.sheath.bettermining.init;

import com.mojang.serialization.MapCodec;
import com.sheath.bettermining.BetterMining;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class EnchantmentInit {
    public static final RegistryKey<Enchantment> VEIN_MINER = RegistryKey.of(RegistryKeys.ENCHANTMENT, BetterMining.id("veinminer"));

    private static RegistryKey<net.minecraft.enchantment.Enchantment> of(String path) {
        Identifier id = Identifier.of("bettermining", path);
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, id);
    }

    private static <T extends EnchantmentEntityEffect> MapCodec<T> register(String name, MapCodec<T> codec) {
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, BetterMining.id(name), codec);
    }

    public static void register() {

        System.out.println("Registering Vein Miner Effect!");
    }
}

