package com.sheath.bettermining.events.veinminer;

import com.sheath.bettermining.BetterMining;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Queue;
import java.util.Set;
import java.util.LinkedList;

public class BlockFinder {
    public static void findVein(World world, BlockPos startPos, BlockState startState, Set<BlockPos> blocksToMine) {
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(startPos);

        while (!queue.isEmpty() && blocksToMine.size() < BetterMining.general.maxBlocks) {
            BlockPos currentPos = queue.poll();
            if (blocksToMine.contains(currentPos)) continue;

            blocksToMine.add(currentPos);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        BlockPos neighborPos = currentPos.add(dx, dy, dz);
                        BlockState neighborState = world.getBlockState(neighborPos);

                        if (neighborState.getBlock() == startState.getBlock() && !blocksToMine.contains(neighborPos)) {
                            queue.add(neighborPos);
                        }
                    }
                }
            }
        }
    }
}
