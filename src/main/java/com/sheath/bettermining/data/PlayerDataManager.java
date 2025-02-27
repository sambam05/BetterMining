package com.sheath.bettermining.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.sheath.bettermining.BetterMining;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File DATA_FILE = new File("config/Veinminer/PlayerData.json");
    private static Map<UUID, Boolean> playerVeinminerStates = new HashMap<>();
    private static Map<UUID, Boolean> playerParticlesStates = new HashMap<>();

    // Load player data from JSON file
    public static void load() {
        if (!DATA_FILE.exists()) {
            save(); // Create the file if it doesn't exist
            return;
        }

        try (FileReader fileReader = new FileReader(DATA_FILE);
             JsonReader reader = new JsonReader(fileReader)) {

            reader.setLenient(true); // Allow Gson to parse leniently

            Type type = new TypeToken<Map<String, Boolean>>() {}.getType();
            Map<String, Boolean> rawData = GSON.fromJson(reader, type);

            playerVeinminerStates = new HashMap<>();
            playerParticlesStates = new HashMap<>();

            if (rawData != null) {
                for (Map.Entry<String, Boolean> entry : rawData.entrySet()) {
                    playerVeinminerStates.put(UUID.fromString(entry.getKey()), entry.getValue());
                }
            }

        } catch (IOException e) {
            BetterMining.LOGGER.error("Failed to load player data: " + e.getMessage());
            playerVeinminerStates = new HashMap<>();
            playerParticlesStates = new HashMap<>();
        }
    }

    // Save player data to JSON file
    public static void save() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            Map<String, Boolean> serializableData = new HashMap<>();
            for (Map.Entry<UUID, Boolean> entry : playerVeinminerStates.entrySet()) {
                serializableData.put(entry.getKey().toString(), entry.getValue());
            }
            GSON.toJson(serializableData, writer);

        } catch (IOException e) {
            BetterMining.LOGGER.error("Failed to save player data: " + e.getMessage());
        }
    }

    // Set Veinminer status for a player
    public static void setVeinminerEnabled(ServerPlayerEntity player, boolean enabled) {
        playerVeinminerStates.put(player.getUuid(), enabled);
        save();
    }

    // Get Veinminer status for a player
    public static boolean isVeinminerEnabled(ServerPlayerEntity player) {
        return playerVeinminerStates.getOrDefault(player.getUuid(), true); // Default to true
    }

    // Set Particles status for a player
    public static void setParticlesEnabled(ServerPlayerEntity player, boolean enabled) {
        playerParticlesStates.put(player.getUuid(), enabled);
        save();
    }

    // Get Particles status for a player
    public static boolean isParticlesEnabled(ServerPlayerEntity player) {
        return playerParticlesStates.getOrDefault(player.getUuid(), true); // Default to true
    }
}