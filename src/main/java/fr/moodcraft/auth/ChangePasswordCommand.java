package fr.moodcraft.auth;

import fr.moodcraft.auth.util.AuthMessages;

import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class ChangePasswordCommand
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
                    "§c✖ §fCommande joueur uniquement."
            );

            return true;
        }

        if (!AuthManager.isRegistered(
                p.getUniqueId().toString()
        )) {

            AuthMessages.error(
                    p,
                    "Sécurité " + AuthMessages.brand(),
                    "Aucun compte trouvé."
            );

            return true;
        }

        if (!AuthListener.isLogged(p)) {

            AuthMessages.error(
                    p,
                    "Sécurité " + AuthMessages.brand(),
                    "Connectez-vous avant de changer votre mot de passe."
            );

            return true;
        }

        if (args.length < 2) {

            AuthMessages.header(
                    p,
                    "Sécurité " + AuthMessages.brand()
            );

            p.sendMessage("§e➜ §fChanger votre mot de passe.");
            p.sendMessage("");
            p.sendMessage("§8• §7Utilisation : §e/changepassword <ancien> <nouveau>");

            AuthMessages.footer(p);

            return true;
        }

        String oldPassword =
                args[0];

        String newPassword =
                args[1];

        if (newPassword.length() < 4) {

            AuthMessages.error(
                    p,
                    "Sécurité " + AuthMessages.brand(),
                    "Le nouveau mot de passe est trop court."
            );

            return true;
        }

        boolean changed =
                AuthManager.changePassword(
                        p.getUniqueId().toString(),
                        oldPassword,
                        newPassword
                );

        if (!changed) {

            AuthMessages.error(
                    p,
                    "Sécurité " + AuthMessages.brand(),
                    "Ancien mot de passe incorrect."
            );

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_BASS,
                    0.8f,
                    0.8f
            );

            return true;
        }

        AuthMessages.success(
                p,
                "Sécurité " + AuthMessages.brand(),
                "Mot de passe changé."
        );

        p.playSound(
                p.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                0.8f,
                1.2f
        );

        return true;
    }
}