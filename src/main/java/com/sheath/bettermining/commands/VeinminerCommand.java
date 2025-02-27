package com.sheath.bettermining.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.sheath.bettermining.configuration.VeinminerConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class VeinminerCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("veinminer")
                .then(BlocksCommand.register())
                .then(ToolsCommand.register())
                .then(SettingsCommand.register())
                .then(BlockPerToolCommand.register())
                .then(ParticlesCommand.register())
                .then(ToggleCommand.register())
                .then(EnchantmentCommand.register())
                .then(CommandManager.literal("help").executes(context -> showHelp(context.getSource())))
                .then(CommandManager.literal("reload").executes(context -> reloadConfig(context.getSource())))
        );
    }

    private static int showHelp(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal("/veinminer help - Show this help message"), false);
        source.sendFeedback(() -> Text.literal("/veinminer reload - Reload the Veinminer configuration"), false);
        return 1;
    }

    private static int reloadConfig(ServerCommandSource source) {
        VeinminerConfig.load();
        source.sendFeedback(() -> Text.literal("Veinminer configuration reloaded."), true);
        return 1;
    }
}
