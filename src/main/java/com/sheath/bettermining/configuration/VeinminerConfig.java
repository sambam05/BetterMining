package com.sheath.bettermining.configuration;


public class VeinminerConfig {
    public static GeneralConfig general = GeneralConfig.load();
    public static MiningConfig mining = MiningConfig.load();
    public static EnchantmentConfig enchantments = EnchantmentConfig.load();

    public static void saveAll() {
        general.save();
        mining.save();
        enchantments.save();
    }
}
