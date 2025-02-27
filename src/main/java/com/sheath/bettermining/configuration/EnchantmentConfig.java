package com.sheath.bettermining.configuration;

import net.fabricmc.loader.api.FabricLoader;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class EnchantmentConfig {
    private static final File ENCHANTMENTS_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "Veinminer/Enchantments.yml");

    public Map<String, Boolean> registeredEnchantments = new HashMap<>();

    public static EnchantmentConfig load() {
        EnchantmentConfig config = new EnchantmentConfig();

        config.registeredEnchantments = loadMapFromFile(getDefaultEnchantments());

        // Save the config if it's missing
        config.save();

        return config;
    }


    public void save() {
        if (!ENCHANTMENTS_FILE.getParentFile().exists()) {
            ENCHANTMENTS_FILE.getParentFile().mkdirs();
        }

        // Convert Map<String, Boolean> to List<Map<String, Boolean>>
        List<Map<String, Boolean>> formattedEnchantments = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : registeredEnchantments.entrySet()) {
            formattedEnchantments.add(Collections.singletonMap(entry.getKey(), entry.getValue()));
        }

        Map<String, List<Map<String, Boolean>>> output = new LinkedHashMap<>();
        output.put("Enchantments", formattedEnchantments);

        try (FileWriter writer = new FileWriter(ENCHANTMENTS_FILE)) {
            getYaml().dump(output, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static Map<String, Boolean> getDefaultEnchantments() {
        Map<String, Boolean> defaultEnchantments = new HashMap<>();
        defaultEnchantments.put("vein_miner", true);
        return defaultEnchantments;
    }

    private static Map<String, Boolean> loadMapFromFile(Map<String, Boolean> defaultMap) {
        if (!EnchantmentConfig.ENCHANTMENTS_FILE.exists()) return defaultMap;
        try (FileReader reader = new FileReader(EnchantmentConfig.ENCHANTMENTS_FILE)) {
            return new Yaml().load(reader);
        } catch (IOException e) {
            return defaultMap;
        }
    }

    private static void saveMapToFile(Map<String, Boolean> data) {
        try (FileWriter writer = new FileWriter(EnchantmentConfig.ENCHANTMENTS_FILE)) {
            new Yaml().dump(Collections.singletonMap("Enchantments", data), writer);
        } catch (IOException ignored) {}
    }

    private static Yaml getYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Forces block-style lists
        options.setPrettyFlow(true);
        options.setIndent(2);
        return new Yaml(options);
    }

}
