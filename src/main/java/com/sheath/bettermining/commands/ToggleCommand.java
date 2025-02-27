package com.sheath.bettermining.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import com.sheath.bettermining.data.PlayerDataManager;

public class ToggleCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("toggle")
                .executes(context -> toggleVeinminer(context.getSource()))
                .then(CommandManager.literal("particles")
                        .executes(context -> toggleParticles(context.getSource())));
    }

    private static int toggleVeinminer(ServerCommandSource source) {
        if (source.getEntity() instanceof ServerPlayerEntity player) {
            boolean currentState = PlayerDataManager.isVeinminerEnabled(player);
            PlayerDataManager.setVeinminerEnabled(player, !currentState);
            source.sendFeedback(() -> Text.literal("Veinminer is now " + (!currentState ? "enabled" : "disabled") + "."), true);
        } else {
            source.sendError(Text.literal("This command can only be used by players."));
        }
        return 1;
    }

    private static int toggleParticles(ServerCommandSource source) {
        if (source.getEntity() instanceof ServerPlayerEntity player) {
            boolean currentState = PlayerDataManager.isParticlesEnabled(player);
            PlayerDataManager.setParticlesEnabled(player, !currentState);
            source.sendFeedback(() -> Text.literal("Particles are now " + (!currentState ? "enabled" : "disabled") + " for you."), true);
        } else {
            source.sendError(Text.literal("This command can only be used by players."));
        }
        return 1;
    }
}
