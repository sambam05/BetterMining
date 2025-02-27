package com.sheath.bettermining.events;

import com.sheath.bettermining.BetterMining;
import com.sheath.bettermining.data.PlayerDataManager;
import com.sheath.bettermining.events.veinminer.CooldownManager;
import com.sheath.bettermining.events.veinminer.VeinMiningProcessor;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBreakHandler {

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register(BlockBreakHandler::onBlockBreak);
    }

    public static boolean onBlockBreak(World world, net.minecraft.entity.player.PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return true;
        if (!BetterMining.CONFIG.veinminerEnabled) return true;

        if (!BetterMining.hasPermission(serverPlayer, "veinminer.use")) {
            serverPlayer.sendMessage(Text.literal("You do not have permission to use Veinminer!"), false);
            return true;
        }

        if (!PlayerDataManager.isVeinminerEnabled(serverPlayer)) {
            serverPlayer.sendMessage(Text.literal("Veinminer is disabled. Use /veinminer toggle to enable it."), true);
            return true;
        }

        if (BetterMining.CONFIG.cooldownEnabled && CooldownManager.isOnCooldown(serverPlayer)) {
            serverPlayer.sendMessage(Text.literal("Veinminer is on cooldown!"), false);
            return false;
        }

        if (BetterMining.CONFIG.cooldownEnabled) {
            CooldownManager.setCooldown(serverPlayer);
        }

        if (BetterMining.CONFIG.requireCrouch && !player.isSneaking()) {
            return true;
        }

        if (!VeinMiningProcessor.isBlockAllowed(state, player.getMainHandStack())) {
            return true;
        }

        VeinMiningProcessor.runAsyncVeinMining((ServerWorld) world, serverPlayer, pos, state);
        return false;
    }
}
