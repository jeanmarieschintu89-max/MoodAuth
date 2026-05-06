package fr.moodcraft.auth.command;

import fr.moodcraft.auth.AuthListener;
import fr.moodcraft.auth.AuthManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ResetAccountCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command cmd,
                             String label,
                             String[] args) {

        //
        // 🔒 PERMISSION
        //

        if (!sender.hasPermission("moodauth.admin")) {

            sender.sendMessage("§c❌ Permission refusée.");
            return true;
        }

        //
        // 📛 USAGE
        //

        if (args.length < 1) {

            sender.sendMessage("§cUsage: /resetcompte <joueur>");
            return true;
        }

        //
        // 👤 TARGET
        //

        OfflinePlayer target =
                Bukkit.getOfflinePlayer(args[0]);

        if (target.getName() == null) {

            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        //
        // 🔥 RESET AUTH
        //

        AuthManager.unregister(
                target.getUniqueId().toString()
        );

        //
        // 🔒 SI CONNECTÉ
        //

        if (target.isOnline()) {

            Player p = target.getPlayer();

            AuthListener.logout(p);

            p.sendTitle(
                    "§cCompte réinitialisé",
                    "§7Refais §e/register",
                    10,
                    60,
                    10
            );

            p.sendMessage("");
            p.sendMessage("§8╔════════════════════════════╗");
            p.sendMessage("§8║   §cCompte supprimé");
            p.sendMessage("§8╠════════════════════════════╣");
            p.sendMessage("§8║ §7Ton compte Auth");
            p.sendMessage("§8║ §7a été réinitialisé.");
            p.sendMessage("§8║");
            p.sendMessage("§8║ §6➜ §e/register <motdepasse>");
            p.sendMessage("§8╚════════════════════════════╝");
            p.sendMessage("");

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_ANVIL_BREAK,
                    1f,
                    0.8f
            );
        }

        //
        // ✅ ADMIN MESSAGE
        //

        sender.sendMessage("");
        sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§a✔ Compte auth réinitialisé");
        sender.sendMessage("§7Joueur: §e" + target.getName());
        sender.sendMessage("§8━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("");

        return true;
    }
}