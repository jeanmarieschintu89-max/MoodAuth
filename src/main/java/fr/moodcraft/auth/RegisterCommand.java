package fr.moodcraft.auth;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        if (AuthManager.isRegistered(p.getUniqueId().toString())) {
            p.sendMessage("§cDéjà enregistré.");
            return true;
        }

        if (args.length < 1) {
            p.sendMessage("§c/register <motdepasse>");
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

        p.sendMessage("§aCompte créé !");
        return true;
    }
}