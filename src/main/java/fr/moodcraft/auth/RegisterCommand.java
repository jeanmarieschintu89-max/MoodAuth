
package fr.moodcraft.auth;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {

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
            p.sendMessage("§cTu es déjà connecté.");
            return true;
        }

        if (AuthManager.isRegistered(p.getUniqueId().toString())) {

            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§c🔒 §fCompte déjà existant.");
            p.sendMessage("§7Connecte-toi avec :");
            p.sendMessage("§6➜ §e/login <motdepasse>");
            return true;
        }

        if (args.length < 1) {

            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§c⚠ §fMot de passe manquant.");
            p.sendMessage("§7Utilisation :");
            p.sendMessage("§6➜ §e/register <motdepasse>");
            return true;
        }

        String ip = p.getAddress()
                .getAddress()
                .getHostAddress();

        AuthManager.register(
                p.getUniqueId().toString(),
                p.getName(),
                args[0],
                ip
        );

        AuthListener.login(p);

        return true;
    }
}