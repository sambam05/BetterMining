package com.sheath.bettermining.configuration;

import net.fabricmc.loader.api.FabricLoader;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MiningConfig {
    private static final File TOOLS_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "Veinminer/AllowedTools.yml");
    private static final File BLOCKS_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "Veinminer/AllowedBlocks.yml");
    private static final File BLOCKS_PER_TOOL_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "Veinminer/AllowedBlocksPerTool.yml");

    public Set<String> allowedTools;
    public Set<String> allowedBlocks;
    public Map<String, Set<String>> allowedBlocksPerTool;

    public static MiningConfig load() {
        MiningConfig config = new MiningConfig();

        config.allowedTools = loadSetFromFile(TOOLS_FILE, getDefaultTools());
        config.allowedBlocks = loadSetFromFile(BLOCKS_FILE, getDefaultBlocks());
        config.allowedBlocksPerTool = loadMapFromFile(getDefaultBlocksPerTool());

        // Save the config if it's missing
        config.save();

        return config;
    }

    public void save() {
        if (!TOOLS_FILE.getParentFile().exists()) {
            TOOLS_FILE.getParentFile().mkdirs();
        }

        saveSetToFile(TOOLS_FILE, allowedTools);
        saveSetToFile(BLOCKS_FILE, allowedBlocks);
        saveMapToFile(allowedBlocksPerTool);
    }


    private static Set<String> getDefaultTools() {
        return new HashSet<>(Arrays.asList(
                "minecraft:wooden_pickaxe",
                "minecraft:stone_pickaxe",
                "minecraft:iron_pickaxe",
                "minecraft:golden_pickaxe",
                "minecraft:diamond_pickaxe",
                "minecraft:netherite_pickaxe"
        ));
    }

    private static Set<String> getDefaultBlocks() {
        return new HashSet<>(Arrays.asList(
                "minecraft:coal_ore",
                "minecraft:iron_ore",
                "minecraft:gold_ore",
                "minecraft:diamond_ore",
                "minecraft:emerald_ore",
                "minecraft:redstone_ore",
                "minecraft:lapis_ore",
                "minecraft:copper_ore"
        ));
    }

    private static Map<String, Set<String>> getDefaultBlocksPerTool() {
        Map<String, Set<String>> map = new HashMap<>();
        for (String tool : getDefaultTools()) {
            map.put(tool, new HashSet<>(getDefaultBlocks()));
        }
        return map;
    }

    private static Set<String> loadSetFromFile(File file, Set<String> defaultSet) {
        if (!file.exists()) return defaultSet;
        try (FileReader reader = new FileReader(file)) {
            return new HashSet<>(new Yaml().load(reader));
        } catch (IOException e) {
            return defaultSet;
        }
    }

    private static void saveSetToFile(File file, Set<String> data) {
        if (data == null || data.isEmpty()) {
            data = new HashSet<>();
        }

        Map<String, List<String>> formattedData = new LinkedHashMap<>();
        formattedData.put("AllowedBlocks", new ArrayList<>(data)); // Converts Set to List

        try (FileWriter writer = new FileWriter(file)) {
            getYaml().dump(formattedData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Map<String, Set<String>> loadMapFromFile(Map<String, Set<String>> defaultMap) {
        if (!MiningConfig.BLOCKS_PER_TOOL_FILE.exists()) return defaultMap;
        try (FileReader reader = new FileReader(MiningConfig.BLOCKS_PER_TOOL_FILE)) {
            return new Yaml().load(reader);
        } catch (IOException e) {
            return defaultMap;
        }
    }

    private static void saveMapToFile(Map<String, Set<String>> data) {
        if (data == null || data.isEmpty()) {
            data = new HashMap<>();
        }

        Map<String, List<String>> formattedData = new LinkedHashMap<>();
        for (Map.Entry<String, Set<String>> entry : data.entrySet()) {
            formattedData.put(entry.getKey(), new ArrayList<>(entry.getValue())); // Convert Set to List
        }

        try (FileWriter writer = new FileWriter(MiningConfig.BLOCKS_PER_TOOL_FILE)) {
            getYaml().dump(formattedData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Yaml getYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Forces block-style lists
        options.setPrettyFlow(true);
        options.setIndent(2);
        return new Yaml(options);
    }

}
