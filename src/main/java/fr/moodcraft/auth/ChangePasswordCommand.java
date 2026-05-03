package fr.moodcraft.auth;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ChangePasswordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        if (args.length < 2) {
            p.sendMessage("§c⚠ Informations manquantes.");
            p.sendMessage("§7Utilisation : §e/changepassword <ancien> <nouveau>");
            return true;
        }

        boolean success = AuthManager.changePassword(
                p.getUniqueId().toString(),
                args[0],
                args[1]
        );

        if (!success) {
            p.sendMessage("§c❌ Ancien mot de passe incorrect.");
            return true;
        }

        p.sendMessage("§a✔ Mot de passe mis à jour !");
        p.sendMessage("§7Ton compte est maintenant sécurisé.");

        return true;
    }
}