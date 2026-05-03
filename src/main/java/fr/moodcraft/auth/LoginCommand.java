
package fr.moodcraft.auth;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        if (args.length < 1) {
            p.sendMessage("§c⚠ Mot de passe manquant.");
            p.sendMessage("§7Utilisation : §e/login <motdepasse>");
            return true;
        }

        String ip = p.getAddress().getAddress().getHostAddress();

        boolean success = AuthManager.login(
                p.getUniqueId().toString(),
                p.getName(),
                args[0],
                ip
        );

        if (!success) {
            p.sendMessage("§c❌ Mot de passe incorrect.");
            p.sendMessage("§7Réessaie ou utilise §e/changepassword");
            return true;
        }

        AuthListener.login(p);

        p.sendMessage("§a✔ Connexion réussie !");
        p.sendMessage("§7Bon jeu sur §eMoodCraft ✨");

        return true;
    }
}