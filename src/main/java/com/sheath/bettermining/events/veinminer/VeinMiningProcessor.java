package com.sheath.bettermining.events.veinminer;

import com.sheath.bettermining.BetterMining;
import com.sheath.bettermining.data.PlayerDataManager;
import com.sheath.bettermining.utils.ParticleUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VeinMiningProcessor {
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(4); // Adjust thread count as needed

    public static void runAsyncVeinMining(ServerWorld world, ServerPlayerEntity player, BlockPos initialPos, BlockState initialState) {
        CompletableFuture.runAsync(() -> {
            Set<BlockPos> blocksToMine = new HashSet<>();
            BlockFinder.findVein(world, initialPos, initialState, blocksToMine);

            if (blocksToMine.size() > BetterMining.CONFIG.maxBlocks) {
                player.sendMessage(Text.literal("Veinminer limit reached!"), false);
                return;
            }

            blocksToMine.forEach(blockPos -> {
                if (PlayerDataManager.isParticlesEnabled(player) && BetterMining.CONFIG.enableParticleOutline) {
                    ParticleUtils.spawnPersistentEdgeParticles(
                            world, blockPos,
                            BetterMining.CONFIG.particleRed,
                            BetterMining.CONFIG.particleGreen,
                            BetterMining.CONFIG.particleBlue,
                            BetterMining.CONFIG.particleDurationTicks
                    );
                }

                BlockDropHandler.processBlock(world, blockPos, player);
            });

        }, threadPool);
    }

    // Checks if a block is allowed based on the tool and block lists
    public static boolean isBlockAllowed(BlockState state, ItemStack tool) {
        if (BetterMining.CONFIG.blocksPerTool) {
            // Tool-specific block checking
            String toolKey = tool.getItem().getTranslationKey();
            Set<String> allowedBlocksForTool = BetterMining.CONFIG.allowedBlocksPerTool.get(toolKey);
            return allowedBlocksForTool != null && allowedBlocksForTool.contains(state.getBlock().getTranslationKey());
        } else {
            // Global block checking
            return BetterMining.CONFIG.allowedBlocks.contains(state.getBlock().getTranslationKey());
        }
    }

    // Gracefully shuts down the thread pool when the server stops
    public static void shutdownThreadPool() {
        try {
            threadPool.shutdown(); // Disable new tasks
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow(); // Force shutdown if tasks do not terminate
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("VeinMiningProcessor thread pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
