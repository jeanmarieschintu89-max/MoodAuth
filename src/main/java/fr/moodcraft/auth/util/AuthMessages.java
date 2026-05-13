package fr.moodcraft.auth.util;

import org.bukkit.command.CommandSender;

public final class AuthMessages {

    private AuthMessages() {}

    public static String brand() {

        return "§aMood§6Craft";
    }

    public static void header(
            CommandSender sender,
            String title
    ) {

        sender.sendMessage("");
        sender.sendMessage(
                "§8----- §6✦ "
                        + cleanTitle(title)
                        + " §6✦ §8-----"
        );
        sender.sendMessage("");
    }

    public static void footer(
            CommandSender sender
    ) {

        sender.sendMessage("");
        sender.sendMessage("§8-----------------------------");
        sender.sendMessage("");
    }

    public static void success(
            CommandSender sender,
            String title,
            String message
    ) {

        header(sender, title);
        sender.sendMessage("§a✔ §f" + message);
        footer(sender);
    }

    public static void error(
            CommandSender sender,
            String title,
            String message
    ) {

        header(sender, title);
        sender.sendMessage("§c✘ §fAction refusée.");
        sender.sendMessage("");
        sender.sendMessage("§7" + message);
        footer(sender);
    }

    public static void info(
            CommandSender sender,
            String title,
            String message
    ) {

        header(sender, title);
        sender.sendMessage("§7" + message);
        footer(sender);
    }

    public static void line(
            CommandSender sender,
            String message
    ) {

        sender.sendMessage("§8• §7" + message);
    }

    private static String cleanTitle(
            String title
    ) {

        if (title == null || title.isBlank()) {
            return "Sécurité " + brand();
        }

        return title
                .replace("§6", "")
                .replace("§f", "")
                .replace("§a", "")
                .replace("§c", "")
                .replace("§7", "")
                .replace("§8", "")
                .replace("✦", "")
                .trim();
    }
}