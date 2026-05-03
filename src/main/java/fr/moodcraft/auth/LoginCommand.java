package fr.moodcraft.auth;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        if (args.length < 1) {
            p.sendMessage("§c/login <motdepasse>");
            return true;
        }

        String ip = p.getAddress().getAddress().getHostAddress();

        if (AuthManager.login(
                p.getUniqueId().toString(),
                p.getName(),
                args[0],
                ip
        )) {
            AuthListener.login(p);
            p.sendMessage("§aConnecté !");
        } else {
            p.sendMessage("§cMot de passe incorrect.");
        }

        return true;
    }
}