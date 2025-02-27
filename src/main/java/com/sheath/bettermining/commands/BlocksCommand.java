package com.sheath.bettermining.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import com.sheath.bettermining.BetterMining;

public class BlocksCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("blocks")
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("block", StringArgumentType.word())
                                .executes(context -> addBlock(context.getSource(), StringArgumentType.getString(context, "block")))))
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("block", StringArgumentType.word())
                                .executes(context -> removeBlock(context.getSource(), StringArgumentType.getString(context, "block")))))
                .then(CommandManager.literal("list")
                        .executes(context -> listBlocks(context.getSource())));
    }

    private static int addBlock(ServerCommandSource source, String block) {
        if (BetterMining.mining.allowedBlocks.contains(block)) {
            source.sendFeedback(() -> Text.literal("Block " + block + " is already in the list."), false);
        } else {
            BetterMining.mining.allowedBlocks.add(block);
            BetterMining.mining.save();
            source.sendFeedback(() -> Text.literal("Block " + block + " added to Veinminer."), true);
        }
        return 1;
    }

    private static int removeBlock(ServerCommandSource source, String block) {
        if (BetterMining.mining.allowedBlocks.remove(block)) {
            BetterMining.mining.save();
            source.sendFeedback(() -> Text.literal("Block " + block + " removed from Veinminer."), true);
        } else {
            source.sendFeedback(() -> Text.literal("Block " + block + " is not in the list."), false);
        }
        return 1;
    }

    private static int listBlocks(ServerCommandSource source) {
        if (BetterMining.mining.allowedBlocks.isEmpty()) {
            source.sendFeedback(() -> Text.literal("No blocks are currently added to the Veinminer list."), false);
        } else {
            source.sendFeedback(() -> Text.literal("Allowed blocks:"), false);
            BetterMining.mining.allowedBlocks.forEach(block -> source.sendFeedback(() -> Text.literal("- " + block), false));
        }
        return 1;
    }
}
