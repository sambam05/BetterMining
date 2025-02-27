package com.sheath.bettermining.events.veinminer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class BlockDropHandler {
    public static void processBlock(ServerWorld world, BlockPos pos, ServerPlayerEntity player) {
        BlockState state = world.getBlockState(pos);
        if (state.isAir()) return;

        List<net.minecraft.item.ItemStack> drops = Block.getDroppedStacks(state, world, pos, world.getBlockEntity(pos), player, player.getMainHandStack());

        world.breakBlock(pos, false);
        drops.forEach(drop -> Block.dropStack(world, pos, drop));
        state.onStacksDropped(world, pos, player.getMainHandStack(), true);
        world.playSound(null, pos, state.getSoundGroup().getBreakSound(), net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}
