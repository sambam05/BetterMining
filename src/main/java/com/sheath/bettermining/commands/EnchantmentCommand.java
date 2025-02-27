package com.sheath.bettermining.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import com.sheath.bettermining.configuration.VeinminerConfig;
import java.util.Set;

public class EnchantmentCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("enchantments")
                .then(CommandManager.literal("list")
                        .executes(context -> listEnchantments(context.getSource())))
                .then(CommandManager.literal("enable")
                        .executes(context -> toggleEnchantments(context.getSource(), true)))
                .then(CommandManager.literal("disable")
                        .executes(context -> toggleEnchantments(context.getSource(), false)));
    }

    private static int listEnchantments(ServerCommandSource source) {
        Set<String> enchantments = VeinminerConfig.getRegisteredEnchantments();
        if (enchantments.isEmpty()) {
            source.sendFeedback(() -> Text.literal("No Veinminer enchantments registered."), false);
        } else {
            source.sendFeedback(() -> Text.literal("Registered Veinminer enchantments:"), false);
            enchantments.forEach(enchantment -> source.sendFeedback(() -> Text.literal("- " + enchantment), false));
        }
        return 1;
    }

    private static int toggleEnchantments(ServerCommandSource source, boolean enable) {
        VeinminerConfig.enchantmentsEnabled = enable;
        VeinminerConfig.save();
        source.sendFeedback(() -> Text.literal("Veinminer enchantments are now " + (enable ? "enabled" : "disabled") + "."), true);
        return 1;
    }
}
