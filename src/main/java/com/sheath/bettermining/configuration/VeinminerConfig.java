package com.sheath.bettermining.configuration;

import com.sheath.bettermining.BetterMining;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class VeinminerConfig {

    private static final File CONFIG_DIR = new File(FabricLoader.getInstance().getConfigDir().toFile(), "Veinminer");
    private static final File GENERAL_CONFIG_FILE = new File(CONFIG_DIR, "GeneralConfig.yml");
    private static final File TOOLS_FILE = new File(CONFIG_DIR, "AllowedTools.yml");
    private static final File BLOCKS_FILE = new File(CONFIG_DIR, "AllowedBlocks.yml");
    private static final File BLOCKS_PER_TOOL_FILE = new File(CONFIG_DIR, "AllowedBlocksPerTool.yml");

    public boolean veinminerEnabled = true;
    public boolean requireCrouch = true;
    public boolean checkToolDurability = true;

    public boolean cooldownEnabled = false;
    public int cooldownSeconds = 5;


    public int maxBlocks = 64;
    public int minBlocks = 16;

    public boolean dynamicMaxBlocks = false;
    public int maxDynamicBlocks = 64;

    public boolean enableParticleOutline = true;
    public int particleDurationTicks = 60;
    public int particleRed = 255;
    public int particleGreen = 0;
    public int particleBlue = 0;

    public boolean blocksPerTool = false;

    public transient Set<String> allowedTools;
    public transient Set<String> allowedBlocks;
    public transient Map<String, Set<String>> allowedBlocksPerTool;


    public static VeinminerConfig load() {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }

        VeinminerConfig config = loadGeneralConfig();
        config.loadToolBlockSettings();
        config.save(); // Ensure all files are created if missing
        return config;
    }

    private static VeinminerConfig loadGeneralConfig() {
        if (!GENERAL_CONFIG_FILE.exists()) {
            VeinminerConfig defaultConfig = new VeinminerConfig();
            defaultConfig.save();
            return defaultConfig;
        }

        try (FileReader reader = new FileReader(GENERAL_CONFIG_FILE)) {
            Map<String, List<Map<String, Object>>> loadedData = getYaml().load(reader);
            if (loadedData == null) {
                return new VeinminerConfig();
            }

            VeinminerConfig config = new VeinminerConfig();

            config.veinminerEnabled = getValue(loadedData, "MainSettings", "veinminerEnabled", config.veinminerEnabled);
            config.blocksPerTool = getValue(loadedData, "MainSettings", "blocksPerTool", config.blocksPerTool);
            config.requireCrouch = getValue(loadedData, "MainSettings", "requireCrouch", config.requireCrouch);
            config.checkToolDurability = getValue(loadedData, "MainSettings", "checkToolDurability", config.checkToolDurability);

            config.cooldownEnabled = getValue(loadedData, "Cooldown", "cooldownEnabled", config.cooldownEnabled);
            config.cooldownSeconds = getValue(loadedData, "Cooldown", "cooldownSeconds", config.cooldownSeconds);

            config.maxBlocks = getValue(loadedData, "Blocks", "maxBlocks", config.maxBlocks);
            config.minBlocks = getValue(loadedData, "Blocks", "minBlocks", config.minBlocks);
            config.dynamicMaxBlocks = getValue(loadedData, "Blocks", "dynamicMaxBlocks", config.dynamicMaxBlocks);
            config.maxDynamicBlocks = getValue(loadedData, "Blocks", "maxDynamicBlocks", config.maxDynamicBlocks);

            config.enableParticleOutline = getValue(loadedData, "Particles", "enableParticleOutline", config.enableParticleOutline);
            config.particleDurationTicks = getValue(loadedData, "Particles", "particleDurationTicks", config.particleDurationTicks);
            config.particleBlue = getValue(loadedData, "Particles", "particleBlue", config.particleBlue);
            config.particleGreen = getValue(loadedData, "Particles", "particleGreen", config.particleGreen);
            config.particleRed = getValue(loadedData, "Particles", "particleRed", config.particleRed);

            return config;
        } catch (IOException e) {
            BetterMining.LOGGER.error("Error loading general config: " + e);
            return new VeinminerConfig();
        }
    }


    private void loadToolBlockSettings() {
        this.allowedTools = ensureSetFileExists(TOOLS_FILE, getDefaultTools());
        this.allowedBlocks = ensureSetFileExists(BLOCKS_FILE, getDefaultBlocks());
        this.allowedBlocksPerTool = ensureMapFileExists(getDefaultBlocksPerTool());
    }

    private static Set<String> ensureSetFileExists(File file, Set<String> defaultSet) {
        if (!file.exists()) {
            saveSetToFile(file, defaultSet);
        }
        Set<String> loadedSet = loadSetFromFile(file, defaultSet);
        if (loadedSet == null || loadedSet.isEmpty()) {
            saveSetToFile(file, defaultSet);
            return defaultSet;
        }
        return loadedSet;
    }

    private static Map<String, Set<String>> ensureMapFileExists(Map<String, Set<String>> defaultMap) {
        if (!VeinminerConfig.BLOCKS_PER_TOOL_FILE.exists()) {
            saveMapToFile(defaultMap);
        }
        Map<String, Set<String>> loadedMap = loadMapFromFile(defaultMap);
        if (loadedMap == null || loadedMap.isEmpty()) {
            saveMapToFile(defaultMap);
            return defaultMap;
        }
        return loadedMap;
    }

    private static Set<String> loadSetFromFile(File file, Set<String> defaultSet) {
        if (!file.exists()) {
            return defaultSet;
        }

        try (FileReader reader = new FileReader(file)) {
            Map<String, List<String>> loadedData = getYaml().load(reader);
            if (loadedData == null || !loadedData.containsKey("AllowedBlocks")) {
                return defaultSet;
            }
            return new HashSet<>(loadedData.get("AllowedBlocks"));
        } catch (IOException e) {
            BetterMining.LOGGER.error("Error loading set from file: " + file.getName());
            return defaultSet;
        }
    }

    private static Map<String, Set<String>> loadMapFromFile(Map<String, Set<String>> defaultMap) {
        if (!VeinminerConfig.BLOCKS_PER_TOOL_FILE.exists()) {
            return defaultMap;
        }

        try (FileReader reader = new FileReader(VeinminerConfig.BLOCKS_PER_TOOL_FILE)) {
            Map<String, List<String>> loadedData = getYaml().load(reader);
            if (loadedData == null) {
                return defaultMap;
            }

            // Convert List<String> back to Set<String>
            Map<String, Set<String>> result = new HashMap<>();
            for (Map.Entry<String, List<String>> entry : loadedData.entrySet()) {
                result.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }
            return result;
        } catch (IOException e) {
            BetterMining.LOGGER.error("Error loading map from file: " + VeinminerConfig.BLOCKS_PER_TOOL_FILE.getName());
            return defaultMap;
        }
    }


    public void save() {
        saveGeneralConfig();
        saveSetToFile(TOOLS_FILE, allowedTools);
        saveSetToFile(BLOCKS_FILE, allowedBlocks);
        saveMapToFile(allowedBlocksPerTool);
    }

    private void saveGeneralConfig() {
        StringBuilder yamlOutput = new StringBuilder();

        yamlOutput.append("# Main gameplay settings\n");
        yamlOutput.append("MainSettings:\n");
        yamlOutput.append(" - veinminerEnabled: ").append(veinminerEnabled).append("  # Enable or disable Veinminer\n");
        yamlOutput.append(" - blocksPerTool: ").append(blocksPerTool).append("  # Toggle whether blocks are limited per tool\n");
        yamlOutput.append(" - requireCrouch: ").append(requireCrouch).append("  # Players must crouch to use Veinminer\n");
        yamlOutput.append(" - checkToolDurability: ").append(checkToolDurability).append("  # Prevent mining when tool durability is low\n");

        yamlOutput.append("\n# Cooldown settings for Veinminer ability\n");
        yamlOutput.append("Cooldown:\n");
        yamlOutput.append(" - cooldownEnabled: ").append(cooldownEnabled).append("  # Enable cooldown for Veinminer\n");
        yamlOutput.append(" - cooldownSeconds: ").append(cooldownSeconds).append("  # Set cooldown time in seconds\n");

        yamlOutput.append("\n# Block mining limits\n");
        yamlOutput.append("Blocks:\n");
        yamlOutput.append(" - maxBlocks: ").append(maxBlocks).append("  # Maximum blocks that can be mined at once\n");
        yamlOutput.append(" - minBlocks: ").append(minBlocks).append("  # Minimum blocks required to activate Veinminer\n");
        yamlOutput.append(" - dynamicMaxBlocks: ").append(dynamicMaxBlocks).append("  # Enable dynamic block mining limits\n");
        yamlOutput.append(" - maxDynamicBlocks: ").append(maxDynamicBlocks).append("  # Maximum blocks when dynamic limits are enabled\n");

        yamlOutput.append("\n# Particle effects for mined blocks\n");
        yamlOutput.append("Particles:\n");
        yamlOutput.append(" - enableParticleOutline: ").append(enableParticleOutline).append("  # Enable particle effect for mined blocks\n");
        yamlOutput.append(" - particleDurationTicks: ").append(particleDurationTicks).append("  # Duration of particle effect (ticks)\n");
        yamlOutput.append(" - particleBlue: ").append(particleBlue).append("  # Blue color value for particles (0-255)\n");
        yamlOutput.append(" - particleGreen: ").append(particleGreen).append("  # Green color value for particles (0-255)\n");
        yamlOutput.append(" - particleRed: ").append(particleRed).append("  # Red color value for particles (0-255)\n");

        try (FileWriter writer = new FileWriter(GENERAL_CONFIG_FILE)) {
            writer.write(yamlOutput.toString());
        } catch (IOException e) {
            BetterMining.LOGGER.error("Error saving general config: " + e);
        }
    }



    private static void saveSetToFile(File file, Set<String> data) {
        if (data == null || data.isEmpty()) {
            data = new HashSet<>();
        }

        Map<String, List<String>> formattedData = new LinkedHashMap<>();
        formattedData.put("AllowedBlocks", new ArrayList<>(data)); // Convert Set to List for YAML formatting

        try (FileWriter writer = new FileWriter(file)) {
            getYaml().dump(formattedData, writer);
        } catch (IOException e) {
            BetterMining.LOGGER.error("Error saving set to file: " + file.getName());
        }
    }

    private static void saveMapToFile(Map<String, Set<String>> data) {
        if (data == null || data.isEmpty()) {
            data = new HashMap<>();
        }

        // Convert each Set<String> into a List<String> before saving
        Map<String, List<String>> formattedData = new LinkedHashMap<>();
        for (Map.Entry<String, Set<String>> entry : data.entrySet()) {
            formattedData.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        try (FileWriter writer = new FileWriter(VeinminerConfig.BLOCKS_PER_TOOL_FILE)) {
            getYaml().dump(formattedData, writer);
        } catch (IOException e) {
            BetterMining.LOGGER.error("Error saving map to file: " + VeinminerConfig.BLOCKS_PER_TOOL_FILE.getName());
        }
    }


    /** Returns default tools */
    private static Set<String> getDefaultTools() {
        return new HashSet<>(Arrays.asList(
                Items.WOODEN_PICKAXE.getTranslationKey(),
                Items.STONE_PICKAXE.getTranslationKey(),
                Items.IRON_PICKAXE.getTranslationKey(),
                Items.GOLDEN_PICKAXE.getTranslationKey(),
                Items.DIAMOND_PICKAXE.getTranslationKey(),
                Items.NETHERITE_PICKAXE.getTranslationKey()
        ));
    }

    /** Returns default blocks */
    private static Set<String> getDefaultBlocks() {
        return new HashSet<>(Arrays.asList(
                Blocks.COAL_ORE.getTranslationKey(),
                Blocks.IRON_ORE.getTranslationKey(),
                Blocks.GOLD_ORE.getTranslationKey(),
                Blocks.DIAMOND_ORE.getTranslationKey(),
                Blocks.EMERALD_ORE.getTranslationKey(),
                Blocks.REDSTONE_ORE.getTranslationKey(),
                Blocks.LAPIS_ORE.getTranslationKey(),
                Blocks.COPPER_ORE.getTranslationKey()
        ));
    }

    /** Returns default blocks per tool */
    private static Map<String, Set<String>> getDefaultBlocksPerTool() {
        Map<String, Set<String>> map = new HashMap<>();
        for (String tool : getDefaultTools()) {
            map.put(tool, new HashSet<>(getDefaultBlocks()));
        }
        return map;
    }

    private static Yaml getYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Ensures block style
        options.setPrettyFlow(true);
        options.setIndent(2);
        return new Yaml(options);
    }


    // Converts a key-value pair into a map entry
    private static Map<String, Object> mapEntry(String key, Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }

    // Extracts a value from the YAML map, with a default if missing
    @SuppressWarnings("unchecked")
    private static <T> T getValue(Map<String, List<Map<String, Object>>> data, String section, String key, T defaultValue) {
        List<Map<String, Object>> sectionData = data.get(section);
        if (sectionData != null) {
            for (Map<String, Object> entry : sectionData) {
                if (entry.containsKey(key)) {
                    Object value = entry.get(key);
                    // Ensure type safety before casting
                    if (defaultValue.getClass().isInstance(value)) {
                        return (T) value;
                    }
                }
            }
        }
        return defaultValue;
    }
}
