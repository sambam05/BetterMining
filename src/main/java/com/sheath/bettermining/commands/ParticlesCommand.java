package com.sheath.bettermining.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import com.sheath.bettermining.BetterMining;

public class ParticlesCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("particles")
                .then(CommandManager.literal("enable")
                        .executes(context -> toggleParticles(context.getSource(), true)))
                .then(CommandManager.literal("disable")
                        .executes(context -> toggleParticles(context.getSource(), false)))
                .then(CommandManager.literal("setcolor")
                        .then(CommandManager.argument("red", IntegerArgumentType.integer(0,255))
                                .then(CommandManager.argument("green", IntegerArgumentType.integer(0,255))
                                        .then(CommandManager.argument("blue", IntegerArgumentType.integer(0,255))
                                                .executes(context -> setParticleColor(context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "red"),
                                                        IntegerArgumentType.getInteger(context, "green"),
                                                        IntegerArgumentType.getInteger(context, "blue")))))))
                .then(CommandManager.literal("setduration")
                        .then(CommandManager.argument("duration", IntegerArgumentType.integer(1))
                                .executes(context -> setParticleDuration(context.getSource(), IntegerArgumentType.getInteger(context, "duration")))));
    }

    private static int toggleParticles(ServerCommandSource source, boolean enable) {
        BetterMining.CONFIG.enableParticleOutline = enable;
        BetterMining.CONFIG.save();
        source.sendFeedback(() -> Text.literal("Particles " + (enable ? "enabled" : "disabled") + "."), true);
        return 1;
    }

    private static int setParticleColor(ServerCommandSource source, int red, int green, int blue) {
        BetterMining.CONFIG.particleRed = red;
        BetterMining.CONFIG.particleGreen = green;
        BetterMining.CONFIG.particleBlue = blue;
        BetterMining.CONFIG.save();
        source.sendFeedback(() -> Text.literal("Particle color set."), true);
        return 1;
    }

    private static int setParticleDuration(ServerCommandSource source, int duration) {
        BetterMining.CONFIG.particleDurationTicks = duration;
        BetterMining.CONFIG.save();
        source.sendFeedback(() -> Text.literal("Particle duration set to " + duration + " ticks."), true);
        return 1;
    }
}
