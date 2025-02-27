package com.sheath.bettermining;

import com.sheath.bettermining.commands.VeinminerCommand;

import com.sheath.bettermining.configuration.EnchantmentConfig;
import com.sheath.bettermining.configuration.GeneralConfig;
import com.sheath.bettermining.configuration.MiningConfig;
import com.sheath.bettermining.events.BlockBreakHandler;
import com.sheath.bettermining.events.veinminer.VeinMiningProcessor;
import com.sheath.bettermining.init.EnchantmentInit;
import com.sheath.bettermining.miscellaneous.TPSTracker;
import com.sheath.bettermining.utils.LoggerTool;
import com.sheath.bettermining.data.PlayerDataManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;


// Import LuckPerms API
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class BetterMining implements ModInitializer {

    public static final String MOD_ID ="bettermining";
    public static final LoggerTool LOGGER = LoggerTool.getLogger(BetterMining.class);

    // Store the config instances
    public static GeneralConfig general;
    public static MiningConfig mining;
    public static EnchantmentConfig enchantments;


    public static final Map<ServerPlayerEntity, Boolean> playerVeinminerToggles = new HashMap<>();

    // LuckPerms instance (optional)
    private static LuckPerms luckPerms;

    @Override
    public void onInitialize() {

        //Initialize message
        LOGGER.info("Initializing");

        PlayerDataManager.load();

        loadConfigs();

        // Handle player join event
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
        });

        // Handle player disconnect event
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
        });

        // Enchantment Register
        EnchantmentInit.register();

        // Register for server starting event
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);

        // Register for server started event
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

        // Register for server stopping event
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);

        // Register a tick event to monitor TPS
        ServerTickEvents.END_SERVER_TICK.register(this::onEndServerTick);

        // Register custom commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> VeinminerCommand.register(dispatcher));
    }

    private void onServerStarting(MinecraftServer minecraftServer){
        // Register the block break event with the vein miner handler if enabled
        if (general.veinminerEnabled) {
            BlockBreakHandler.register();
            LOGGER.info("Initialized with config: ");
        } else {
            LOGGER.info("Mod disabled by config.");
        }
    }

    private void onServerStarted(MinecraftServer minecraftServer) {
        // Attempt to hook into LuckPerms if available
        try {
            // Check if LuckPerms is loaded
            Class.forName("net.luckperms.api.LuckPermsProvider");
            luckPerms = LuckPermsProvider.get();
            LOGGER.info("LuckPerms detected, integrating permissions.");
        } catch (ClassNotFoundException | IllegalStateException e) {
            LOGGER.info("LuckPerms not detected, skipping integration.");
            luckPerms = null;
        }
    }

    private void onServerStopping(MinecraftServer minecraftServer){
        PlayerDataManager.save();
        LOGGER.info("Shutting down Veinminer thread pool");
        VeinMiningProcessor.shutdownThreadPool();
    }

    private void onEndServerTick(MinecraftServer server) {
        // Update TPS Tracker
        com.sheath.bettermining.miscellaneous.TPSTracker.tick(System.currentTimeMillis());

        // Adjust maxBlocks based on TPS
        if (general.dynamicMaxBlocks) {
            double tps = TPSTracker.getTPS();
            int adjustedMaxBlocks = (int) ((tps / 20.0) * (general.maxDynamicBlocks - general.minBlocks)) + general.minBlocks;
            general.maxBlocks = Math.max(general.minBlocks, adjustedMaxBlocks);
        }
    }

    // Check if the player has the permission using LuckPerms
    public static boolean hasPermission(ServerPlayerEntity player, String permission) {
        if (luckPerms != null) { // Check if LuckPerms is available
            User user = luckPerms.getUserManager().getUser(player.getUuid());
            if (user != null) {
                return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
            }
        }
        return true; // Default to true if LuckPerms is not installed
    }
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID,path);
    }

    public static void loadConfigs() {
        general = GeneralConfig.load();
        mining = MiningConfig.load();
        enchantments = EnchantmentConfig.load();

        // Ensure all configs are saved if they don’t exist
        general.save();
        mining.save();
        enchantments.save();
    }


    public static void saveConfigs() {
        general.save();
        mining.save();
        enchantments.save();
    }
}
