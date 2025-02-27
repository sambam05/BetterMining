package com.sheath.bettermining.utils;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3i;

public class ParticleUtils {

    public static void spawnPersistentEdgeParticles(ServerWorld world, BlockPos pos, int r, int g, int b, int durationTicks) {
        Vector3i color = new Vector3i(r, g, b);
        int packedColor = ColorUtils.vector3fToInt(color);
        DustParticleEffect particle = new DustParticleEffect(packedColor, 1.0f);

        double[][] edgeOffsets = {
                {0, 0, 0}, {1, 0, 0}, {0, 0, 1}, {1, 0, 1},
                {0, 1, 0}, {1, 1, 0}, {0, 1, 1}, {1, 1, 1},
                {0, 0, 0}, {0, 1, 0}, {1, 0, 0}, {1, 1, 0},
                {0, 0, 1}, {0, 1, 1}, {1, 0, 1}, {1, 1, 1}
        };

        MinecraftServer server = world.getServer();
        int spawnIntervalTicks = 5;

        for (int tick = 0; tick < durationTicks; tick += spawnIntervalTicks) {
            server.execute(() -> {
                for (double[] offset : edgeOffsets) {
                    double x = pos.getX() + offset[0];
                    double y = pos.getY() + offset[1];
                    double z = pos.getZ() + offset[2];
                    world.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0.01);
                }
            });
        }
    }
}
