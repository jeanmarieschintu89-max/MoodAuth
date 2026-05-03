package fr.moodcraft.auth;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        if (AuthManager.isRegistered(p.getUniqueId().toString())) {
            p.sendMessage("§c⚠ Tu as déjà un compte.");
            p.sendMessage("§7Utilise : §e/login <motdepasse>");
            return true;
        }

        if (args.length < 1) {
            p.sendMessage("§c⚠ Mot de passe manquant.");
            p.sendMessage("§7Utilisation : §e/register <motdepasse>");
            return true;
        }

        String ip = p.getAddress().getAddress().getHostAddress();

        AuthManager.register(
                p.getUniqueId().toString(),
                p.getName(),
                args[0],
                ip
        );

        AuthListener.login(p);

        p.sendMessage("§a✔ Compte créé avec succès !");
        p.sendMessage("§7Tu es maintenant connecté.");
        p.sendMessage("§8(Pense à garder ton mot de passe en sécurité)");

        return true;
    }
}