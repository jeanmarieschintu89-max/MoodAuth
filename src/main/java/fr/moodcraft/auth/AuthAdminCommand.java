package fr.moodcraft.auth;

import fr.moodcraft.auth.util.AuthMessages;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import java.util.Locale;

public class AuthAdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!sender.hasPermission("moodauth.admin")
                && !sender.hasPermission("moodcraft.admin")) {

            error(sender, "Accès réservé à l'administration.");
            return true;
        }

        if (args.length == 0) {
            help(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "help", "aide" -> help(sender);
            case "status", "etat", "état" -> status(sender);
            case "info" -> info(sender, args);
            case "reset" -> reset(sender, args);
            case "unlock", "debloquer", "débloquer" -> unlock(sender, args);
            case "forcelogout", "logout" -> forceLogout(sender, args);
            case "forcecheck", "check" -> forceCheck(sender, args);
            case "reload" -> reload(sender);
            default -> help(sender);
        }

        return true;
    }

    private void status(CommandSender sender) {
        int online = Bukkit.getOnlinePlayers().size();
        int logged = 0;
        int blocked = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (AuthListener.isLogged(player)) {
                logged++;
            }
            if (AuthListener.isBlocked(player)) {
                blocked++;
            }
        }

        header(sender, "Admin Auth");
        sender.sendMessage("§e➜ §7Joueurs en ligne : §e" + online);
        sender.sendMessage("§e➜ §7Connectés Auth : §e" + logged);
        sender.sendMessage("§e➜ §7En attente Auth : §e" + Math.max(0, online - logged));
        sender.sendMessage("§e➜ §7Bloqués en ligne : §e" + blocked);
        footer(sender);
    }

    private void info(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/authadmin info <joueur>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        boolean registered = AuthManager.isRegistered(target.getUniqueId().toString());
        Player online = target.getPlayer();

        header(sender, "Admin Auth");
        sender.sendMessage("§e➜ §7Joueur : §e" + safeName(target));
        sender.sendMessage("§e➜ §7UUID : §f" + target.getUniqueId());
        sender.sendMessage("§e➜ §7Compte créé : " + (registered ? "§aoui" : "§cnon"));
        sender.sendMessage("§e➜ §7En ligne : " + (online != null ? "§aoui" : "§cnon"));

        if (online != null) {
            sender.sendMessage("§e➜ §7Connecté Auth : " + (AuthListener.isLogged(online) ? "§aoui" : "§cnon"));
            sender.sendMessage("§e➜ §7Bloqué : " + (AuthListener.isBlocked(online) ? "§coui" : "§anon"));
        }

        footer(sender);
    }

    private void reset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/authadmin reset <joueur>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        AuthManager.unregister(target.getUniqueId().toString());

        if (target.getPlayer() != null) {
            AuthListener.logout(target.getPlayer());
        }

        success(sender, "Compte réinitialisé : §e" + safeName(target));
    }

    private void unlock(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/authadmin unlock <joueur>");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            error(sender, "Le joueur doit être en ligne pour cette action.");
            return;
        }

        AuthListener.resetFailedAttempts(target);
        success(sender, "Tentatives réinitialisées : §e" + target.getName());
    }

    private void forceLogout(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/authadmin forcelogout <joueur>");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            error(sender, "Le joueur doit être en ligne pour cette action.");
            return;
        }

        AuthListener.logout(target);
        target.sendMessage("");
        target.sendMessage("§8----- §6✦ Sécurité MoodCraft ✦ §8-----");
        target.sendMessage("§e➜ §7Votre session Auth a été fermée par le staff.");
        target.sendMessage("§8-----------------------------");
        target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.8f);

        success(sender, "Session fermée : §e" + target.getName());
    }

    private void forceCheck(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/authadmin forcecheck <joueur>");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            error(sender, "Le joueur doit être en ligne pour cette action.");
            return;
        }

        AuthListener.logout(target);
        AuthListener.resetFailedAttempts(target);
        success(sender, "Vérification Auth relancée : §e" + target.getName());
    }

    private void reload(CommandSender sender) {
        AuthManager.init();
        success(sender, "Données Auth rechargées.");
    }

    private void help(CommandSender sender) {
        header(sender, "Admin Auth");
        sender.sendMessage("§e➜ §7/authadmin status");
        sender.sendMessage("§e➜ §7/authadmin info <joueur>");
        sender.sendMessage("§e➜ §7/authadmin reset <joueur>");
        sender.sendMessage("§e➜ §7/authadmin unlock <joueur>");
        sender.sendMessage("§e➜ §7/authadmin forcelogout <joueur>");
        sender.sendMessage("§e➜ §7/authadmin forcecheck <joueur>");
        sender.sendMessage("§e➜ §7/authadmin reload");
        footer(sender);
    }

    private void usage(CommandSender sender, String usage) {
        header(sender, "Admin Auth");
        sender.sendMessage("§c✖ §fCommande incomplète.");
        sender.sendMessage("§e➜ §7Utilisation : §e" + usage);
        footer(sender);
    }

    private void success(CommandSender sender, String message) {
        header(sender, "Admin Auth");
        sender.sendMessage("§a✔ §f" + message);
        footer(sender);
    }

    private void error(CommandSender sender, String message) {
        header(sender, "Admin Auth");
        sender.sendMessage("§c✖ §f" + message);
        footer(sender);
    }

    private void header(CommandSender sender, String title) {
        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ " + title + " ✦ §8-----");
    }

    private void footer(CommandSender sender) {
        sender.sendMessage("§8-----------------------------");
    }

    private String safeName(OfflinePlayer player) {
        return player.getName() != null ? player.getName() : "Inconnu";
    }
}
