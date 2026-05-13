package fr.moodcraft.auth;

import fr.moodcraft.auth.util.AuthMessages;

import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class RegisterCommand
        implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command cmd,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player p)) {

            sender.sendMessage(
                    "§cCommande joueur uniquement."
            );

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

        if (AuthManager.isRegistered(
                p.getUniqueId().toString()
        )) {

            AuthMessages.header(
                    p,
                    "Sécurité " + AuthMessages.brand()
            );

            p.sendMessage("§c✘ §fCompte déjà créé.");
            p.sendMessage("");
            p.sendMessage("§7Connectez-vous avec:");
            p.sendMessage("§e/login <motdepasse>");

            AuthMessages.footer(p);

            return true;
        }

        if (args.length < 1) {

            AuthMessages.header(
                    p,
                    "Sécurité " + AuthMessages.brand()
            );

            p.sendMessage("§c✘ §fMot de passe manquant.");
            p.sendMessage("");
            p.sendMessage("§7Utilisation:");
            p.sendMessage("§e/register <motdepasse>");
            p.sendMessage("");
            AuthMessages.line(
                    p,
                    "Choisissez un mot de passe privé"
            );

            AuthMessages.footer(p);

            return true;
        }

        String password =
                args[0];

        if (password.length() < 4) {

            AuthMessages.error(
                    p,
                    "Sécurité " + AuthMessages.brand(),
                    "Mot de passe trop court. Minimum: 4 caractères."
            );

            return true;
        }

        String ip =
                p.getAddress()
                        .getAddress()
                        .getHostAddress();

        AuthManager.register(
                p.getUniqueId().toString(),
                p.getName(),
                password,
                ip
        );

        AuthListener.login(p);

        AuthMessages.header(
                p,
                "Sécurité " + AuthMessages.brand()
        );

        p.sendMessage("§a✔ §fCompte créé avec succès.");
        p.sendMessage("");
        AuthMessages.line(
                p,
                "Votre progression est protégée"
        );
        AuthMessages.line(
                p,
                "Gardez votre mot de passe secret"
        );

        AuthMessages.footer(p);

        p.playSound(
                p.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                0.8f,
                1.1f
        );

        return true;
    }
}