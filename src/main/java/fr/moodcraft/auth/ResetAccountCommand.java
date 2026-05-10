
package fr.moodcraft.auth;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ResetAccountCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command cmd,
            String label,
            String[] args
    ) {

        if (!sender.hasPermission("moodauth.admin")) {
            sender.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            sender.sendMessage("§c✖ §fPermission refusée.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            sender.sendMessage("§c⚠ §fJoueur manquant.");
            sender.sendMessage("§7Utilisation : §e/resetcompte <joueur>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target.getName() == null && !target.hasPlayedBefore()) {
            sender.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            sender.sendMessage("§c✖ §fJoueur introuvable.");
            return true;
        }

        AuthManager.unregister(
                target.getUniqueId().toString()
        );

        if (target.isOnline()) {

            Player p = target.getPlayer();

            if (p != null) {

                AuthListener.logout(p);

                p.addPotionEffect(
                        new org.bukkit.potion.PotionEffect(
                                org.bukkit.potion.PotionEffectType.BLINDNESS,
                                9999,
                                1
                        )
                );

                p.addPotionEffect(
                        new org.bukkit.potion.PotionEffect(
                                org.bukkit.potion.PotionEffectType.SLOW,
                                9999,
                                10
                        )
                );

                for (int i = 0; i < 80; i++) {
                    p.sendMessage("");
                }

                p.sendTitle(
                        "§cCompte réinitialisé",
                        "§7Crée un nouvel accès",
                        10,
                        60,
                        10
                );

                p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
                p.sendMessage("§c🔒 §fCompte réinitialisé.");
                p.sendMessage("§7Ton ancien accès a été supprimé.");
                p.sendMessage("");
                p.sendMessage("§6➜ §e/register <motdepasse>");
                p.sendMessage("");

                p.playSound(
                        p.getLocation(),
                        Sound.BLOCK_ANVIL_BREAK,
                        1f,
                        0.8f
                );
            }
        }

        sender.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
        sender.sendMessage("§a✔ §fCompte auth réinitialisé.");
        sender.sendMessage("§7Joueur : §e" + target.getName());

        return true;
    }
}