package com.sheath.bettermining.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import com.sheath.bettermining.BetterMining;

public class ToolsCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("tools")
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("tool", StringArgumentType.word())
                                .executes(context -> addTool(context.getSource(), StringArgumentType.getString(context, "tool")))))
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("tool", StringArgumentType.word())
                                .executes(context -> removeTool(context.getSource(), StringArgumentType.getString(context, "tool")))))
                .then(CommandManager.literal("list")
                        .executes(context -> listTools(context.getSource())));
    }

    private static int addTool(ServerCommandSource source, String tool) {
        if (BetterMining.CONFIG.allowedTools.contains(tool)) {
            source.sendFeedback(() -> Text.literal("Tool " + tool + " is already in the list."), false);
        } else {
            BetterMining.CONFIG.allowedTools.add(tool);
            BetterMining.CONFIG.save();
            source.sendFeedback(() -> Text.literal("Tool " + tool + " added to Veinminer."), true);
        }
        return 1;
    }

    private static int removeTool(ServerCommandSource source, String tool) {
        if (BetterMining.CONFIG.allowedTools.remove(tool)) {
            BetterMining.CONFIG.save();
            source.sendFeedback(() -> Text.literal("Tool " + tool + " removed from Veinminer."), true);
        } else {
            source.sendFeedback(() -> Text.literal("Tool " + tool + " is not in the list."), false);
        }
        return 1;
    }

    private static int listTools(ServerCommandSource source) {
        if (BetterMining.CONFIG.allowedTools.isEmpty()) {
            source.sendFeedback(() -> Text.literal("No tools are currently added to the Veinminer list."), false);
        } else {
            source.sendFeedback(() -> Text.literal("Allowed tools:"), false);
            BetterMining.CONFIG.allowedTools.forEach(tool -> source.sendFeedback(() -> Text.literal("- " + tool), false));
        }
        return 1;
    }
}
