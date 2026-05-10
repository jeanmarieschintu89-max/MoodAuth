package fr.moodcraft.auth;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ChangePasswordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command cmd,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cCommande joueur uniquement.");
            return true;
        }

        if (!AuthListener.isLogged(p)) {
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§c🔒 §fSession verrouillée.");
            p.sendMessage("§7Connecte-toi avant de modifier ton mot de passe.");
            p.sendMessage("§6➜ §e/login <motdepasse>");
            return true;
        }

        if (args.length < 2) {
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§c⚠ §fInformations manquantes.");
            p.sendMessage("§7Utilisation : §e/changepass <ancien> <nouveau>");
            return true;
        }

        boolean success = AuthManager.changePassword(
                p.getUniqueId().toString(),
                args[0],
                args[1]
        );

        if (!success) {
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§c✖ §fAncien mot de passe incorrect.");
            p.sendMessage("§7Modification refusée.");
            return true;
        }

        p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
        p.sendMessage("§a✔ §fMot de passe mis à jour.");
        p.sendMessage("§7Ton compte est protégé.");
        return true;
    }
}