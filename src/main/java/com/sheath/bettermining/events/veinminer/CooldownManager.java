package com.sheath.bettermining.events.veinminer;

import com.sheath.bettermining.BetterMining;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.HashMap;
import java.util.Map;

public class CooldownManager {
    private static final Map<ServerPlayerEntity, Long> playerCooldowns = new HashMap<>();

    public static boolean isOnCooldown(ServerPlayerEntity player) {
        long currentTime = System.currentTimeMillis();
        long lastUse = playerCooldowns.getOrDefault(player, 0L);
        return currentTime - lastUse < BetterMining.CONFIG.cooldownSeconds * 1000L;
    }

    public static void setCooldown(ServerPlayerEntity player) {
        playerCooldowns.put(player, System.currentTimeMillis());
    }
}
