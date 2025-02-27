package com.sheath.bettermining.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import com.sheath.bettermining.BetterMining;
import java.util.Set;
import java.util.HashSet;

public class BlockPerToolCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("blockpertool")
                .then(CommandManager.literal("blocks")
                        .then(CommandManager.argument("tool", StringArgumentType.word())
                                .then(CommandManager.literal("add")
                                        .then(CommandManager.argument("block", StringArgumentType.word())
                                                .executes(context -> addBlockPerTool(context.getSource(),
                                                        StringArgumentType.getString(context, "tool"),
                                                        StringArgumentType.getString(context, "block")))))
                                .then(CommandManager.literal("remove")
                                        .then(CommandManager.argument("block", StringArgumentType.word())
                                                .executes(context -> removeBlockPerTool(context.getSource(),
                                                        StringArgumentType.getString(context, "tool"),
                                                        StringArgumentType.getString(context, "block")))))
                                .then(CommandManager.literal("list")
                                        .executes(context -> listBlocksPerTool(context.getSource(),
                                                StringArgumentType.getString(context, "tool"))))));
    }

    private static int addBlockPerTool(ServerCommandSource source, String tool, String block) {
        Set<String> toolBlocks = BetterMining.CONFIG.allowedBlocksPerTool.computeIfAbsent(tool, k -> new HashSet<>());
        if (toolBlocks.add(block)) {
            BetterMining.CONFIG.save();
            source.sendFeedback(() -> Text.literal("Block " + block + " added for tool " + tool + "."), true);
        } else {
            source.sendFeedback(() -> Text.literal("Block " + block + " is already in the list for tool " + tool + "."), false);
        }
        return 1;
    }

    private static int removeBlockPerTool(ServerCommandSource source, String tool, String block) {
        Set<String> toolBlocks = BetterMining.CONFIG.allowedBlocksPerTool.get(tool);
        if (toolBlocks != null && toolBlocks.remove(block)) {
            BetterMining.CONFIG.save();
            source.sendFeedback(() -> Text.literal("Block " + block + " removed from tool " + tool + "."), true);
        } else {
            source.sendFeedback(() -> Text.literal("Block " + block + " is not in the list for tool " + tool + "."), false);
        }
        return 1;
    }

    private static int listBlocksPerTool(ServerCommandSource source, String tool) {
        Set<String> toolBlocks = BetterMining.CONFIG.allowedBlocksPerTool.get(tool);
        if (toolBlocks == null || toolBlocks.isEmpty()) {
            source.sendFeedback(() -> Text.literal("No blocks are currently added for tool " + tool + "."), false);
        } else {
            source.sendFeedback(() -> Text.literal("Blocks for tool " + tool + ":"), false);
            toolBlocks.forEach(block -> source.sendFeedback(() -> Text.literal("- " + block), false));
        }
        return 1;
    }
}
