package com.sheath.bettermining.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import com.sheath.bettermining.BetterMining;

public class SettingsCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("settings")
                .then(CommandManager.literal("blockpertool")
                        .executes(context -> toggleBlockPerTool(context.getSource())))
                .then(CommandManager.literal("cooldown")
                        .then(CommandManager.literal("enable")
                                .executes(context -> enableCooldown(context.getSource(), true)))
                        .then(CommandManager.literal("disable")
                                .executes(context -> enableCooldown(context.getSource(), false)))
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("seconds", IntegerArgumentType.integer(1))
                                        .executes(context -> setCooldown(context.getSource(), IntegerArgumentType.getInteger(context, "seconds"))))))
                .then(CommandManager.literal("maxblocks")
                        .then(CommandManager.argument("number", IntegerArgumentType.integer(1))
                                .executes(context -> setMaxBlocks(context.getSource(), IntegerArgumentType.getInteger(context, "number")))));
    }

    private static int toggleBlockPerTool(ServerCommandSource source) {
        BetterMining.CONFIG.blocksPerTool = !BetterMining.CONFIG.blocksPerTool;
        BetterMining.CONFIG.save();
        source.sendFeedback(() -> Text.literal("BlockPerTool is now " + (BetterMining.CONFIG.blocksPerTool ? "enabled" : "disabled")), true);
        return 1;
    }

    private static int enableCooldown(ServerCommandSource source, boolean enabled) {
        BetterMining.CONFIG.cooldownEnabled = enabled;
        BetterMining.CONFIG.save();
        source.sendFeedback(() -> Text.literal("Cooldown is now " + (enabled ? "enabled" : "disabled")), true);
        return 1;
    }

    private static int setCooldown(ServerCommandSource source, int seconds) {
        BetterMining.CONFIG.cooldownSeconds = seconds;
        BetterMining.CONFIG.save();
        source.sendFeedback(() -> Text.literal("Cooldown time set to " + seconds + " seconds"), true);
        return 1;
    }

    private static int setMaxBlocks(ServerCommandSource source, int maxBlocks) {
        BetterMining.CONFIG.maxBlocks = maxBlocks;
        BetterMining.CONFIG.save();
        source.sendFeedback(() -> Text.literal("Max blocks set to " + maxBlocks), true);
        return 1;
    }
}
