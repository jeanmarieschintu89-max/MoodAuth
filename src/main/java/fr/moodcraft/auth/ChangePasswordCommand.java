package fr.moodcraft.auth;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ChangePasswordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        if (args.length < 2) {
            p.sendMessage("§c/changepassword <ancien> <nouveau>");
            return true;
        }

        boolean success = AuthManager.changePassword(
                p.getUniqueId().toString(),
                args[0],
                args[1]
        );

        if (!success) {
            p.sendMessage("§cMot de passe incorrect.");
            return true;
        }

        p.sendMessage("§aMot de passe changé !");
        return true;
    }
}