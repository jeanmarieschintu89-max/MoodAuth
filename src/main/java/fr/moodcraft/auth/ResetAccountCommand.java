package fr.moodcraft.auth;

import fr.moodcraft.auth.util.AuthMessages;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetAccountCommand
        implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command cmd,
            String label,
            String[] args
    ) {

        if (!sender.hasPermission("moodauth.admin")
                && !sender.hasPermission("moodcraft.admin")) {

            AuthMessages.error(
                    sender,
                    "Sécurité " + AuthMessages.brand(),
                    "Accès réservé à l'administration."
            );

            return true;
        }

        if (args.length < 1) {

            AuthMessages.header(
                    sender,
                    "Sécurité " + AuthMessages.brand()
            );

            sender.sendMessage("§fRéinitialiser un compte.");
            sender.sendMessage("");
            sender.sendMessage("§7Utilisation:");
            sender.sendMessage("§e/resetcompte <joueur>");

            AuthMessages.footer(sender);

            return true;
        }

        OfflinePlayer target =
                Bukkit.getOfflinePlayer(args[0]);

        AuthManager.unregister(
                target.getUniqueId().toString()
        );

        AuthMessages.header(
                sender,
                "Sécurité " + AuthMessages.brand()
        );

        sender.sendMessage("§a✔ §fCompte réinitialisé.");
        sender.sendMessage("");
        sender.sendMessage("§7Joueur: §e" + safeName(target));
        sender.sendMessage("");
        AuthMessages.line(
                sender,
                "Le joueur devra refaire /register"
        );

        AuthMessages.footer(sender);

        if (sender instanceof org.bukkit.entity.Player p) {

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    0.8f,
                    1.2f
            );
        }

        return true;
    }

    private String safeName(
            OfflinePlayer player
    ) {

        return player.getName() != null
                ? player.getName()
                : "Inconnu";
    }
}