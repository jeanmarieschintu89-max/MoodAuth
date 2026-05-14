package fr.moodcraft.auth;

import fr.moodcraft.auth.util.AuthMessages;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import java.lang.reflect.Method;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LoginCommand implements CommandExecutor {

    private static final Map<UUID, Integer> FAILED_ATTEMPTS = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = 3;

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command cmd,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage("§c✖ §fCommande joueur uniquement.");
            return true;
        }

        if (AuthListener.isLogged(p)) {
            AuthMessages.success(
                    p,
                    "Sécurité " + AuthMessages.brand(),
                    "Vous êtes déjà connecté."
            );
            return true;
        }

        if (!AuthManager.isRegistered(p.getUniqueId().toString())) {
            AuthMessages.header(p, "Sécurité " + AuthMessages.brand());
            p.sendMessage("§c✖ §fAucun compte trouvé.");
            AuthMessages.command(p, "/register <motdepasse>", "créer votre compte");
            AuthMessages.line(p, "Votre compte protège votre progression");
            AuthMessages.footer(p);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.8f);
            return true;
        }

        if (args.length < 1) {
            AuthMessages.header(p, "Sécurité " + AuthMessages.brand());
            p.sendMessage("§c✖ §fMot de passe manquant.");
            AuthMessages.command(p, "/login <motdepasse>", "connexion au compte");
            AuthMessages.footer(p);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.8f);
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
            int attempts = FAILED_ATTEMPTS.merge(p.getUniqueId(), 1, Integer::sum);

            AuthMessages.header(p, "Sécurité " + AuthMessages.brand());

            if (attempts >= MAX_ATTEMPTS) {
                p.sendMessage("§c✖ §fTrop de tentatives.");
                AuthMessages.line(p, "Contactez le staff via un ticket Discord");
                AuthMessages.line(p, "Le staff pourra vérifier votre compte");
            } else {
                p.sendMessage("§c✖ §fMot de passe incorrect.");
                AuthMessages.command(p, "/login <motdepasse>", "réessayer");
                AuthMessages.line(p, "Tentative : §e" + attempts + "§8/§e" + MAX_ATTEMPTS);
            }

            AuthMessages.footer(p);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.8f);
            return true;
        }

        FAILED_ATTEMPTS.remove(p.getUniqueId());
        AuthListener.login(p);
        sendMoodBusinessAlerts(p);

        return true;
    }

    private void sendMoodBusinessAlerts(Player p) {

        if (!Bukkit.getPluginManager().isPluginEnabled("MoodBusiness")) {
            return;
        }

        if (Main.getInstance() == null) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(
                Main.getInstance(),
                () -> {
                    try {
                        Class<?> api = Class.forName("fr.moodcraft.business.api.MoodBusinessAPI");
                        Method method = api.getMethod("sendPendingAlerts", Player.class);
                        method.invoke(null, p);
                    } catch (Exception ignored) {
                        Bukkit.getConsoleSender().sendMessage(
                                "§c✖ §f[MoodAuth] Impossible d'envoyer les alertes MoodBusiness."
                        );
                    }
                },
                20L
        );
    }
}
