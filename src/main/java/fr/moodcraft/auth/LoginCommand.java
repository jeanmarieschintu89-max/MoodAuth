package fr.moodcraft.auth;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {

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

        if (AuthListener.isLogged(p)) {
            p.sendMessage("§a✔ Tu es déjà connecté.");
            return true;
        }

        if (!AuthManager.isRegistered(p.getUniqueId().toString())) {
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§a✨ §fAucun compte trouvé.");
            p.sendMessage("§7Crée ton accès avec :");
            p.sendMessage("§6➜ §e/register <motdepasse>");
            return true;
        }

        if (args.length < 1) {
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§c⚠ §fMot de passe manquant.");
            p.sendMessage("§7Utilisation :");
            p.sendMessage("§6➜ §e/login <motdepasse>");
            return true;
        }

        String ip = p.getAddress()
                .getAddress()
                .getHostAddress();

        boolean success = AuthManager.login(
                p.getUniqueId().toString(),
                p.getName(),
                args[0],
                ip
        );

        if (!success) {
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§c✖ §fMot de passe incorrect.");
            p.sendMessage("§7Réessaie avec :");
            p.sendMessage("§6➜ §e/login <motdepasse>");
            return true;
        }

        AuthListener.login(p);
        return true;
    }
}