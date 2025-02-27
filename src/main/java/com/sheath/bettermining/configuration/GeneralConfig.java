package com.sheath.bettermining.configuration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.sheath.bettermining.BetterMining;
import net.fabricmc.loader.api.FabricLoader;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class GeneralConfig {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "Veinminer/GeneralConfig.yml");
    public boolean veinminerEnabled = true;
    public boolean requireCrouch = true;
    public boolean checkToolDurability = true;
    public boolean cooldownEnabled = false;
    public boolean enchantmentsEnabled = true;
    public boolean blocksPerTool = true;
    public int cooldownSeconds = 5;
    public boolean dynamicMaxBlocks = false;
    public int maxBlocks = 64;
    public int minBlocks = 16;
    public int maxDynamicBlocks = 64;
    public boolean enableParticleOutline = true;
    public int particleDurationTicks = 60;
    public int particleRed = 255;
    public int particleGreen = 0;
    public int particleBlue = 0;

    public static GeneralConfig load() {
        if (!CONFIG_FILE.exists()) {
            GeneralConfig defaultConfig = new GeneralConfig();
            defaultConfig.save(); // Save default config on first load
            return defaultConfig;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Yaml yaml = new Yaml();
            return yaml.loadAs(reader, GeneralConfig.class);
        } catch (IOException e) {
            return new GeneralConfig();
        }
    }

    public void save() {
        if (!CONFIG_FILE.getParentFile().exists()) {
            CONFIG_FILE.getParentFile().mkdirs();
        }

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

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            writer.write(yamlOutput.toString());
        } catch (IOException e) {
            BetterMining.LOGGER.error("Error saving general config: " + e);
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
