package fr.moodcraft.auth;

import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LoginCommand
        implements CommandExecutor {

    private static final Map<UUID, Integer> FAILED_ATTEMPTS =
            new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS =
            3;

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

            p.sendMessage("");
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§a✔ §fVous êtes déjà connecté.");
            p.sendMessage("");

            return true;
        }

        if (!AuthManager.isRegistered(
                p.getUniqueId()
                        .toString()
        )) {

            p.sendMessage("");
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§fAucun compte trouvé.");
            p.sendMessage("");
            p.sendMessage("§7Commande : §e/register <motdepasse>");
            p.sendMessage("");
            p.sendMessage("§8Créez votre compte pour protéger votre progression.");
            p.sendMessage("");

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_BASS,
                    0.8f,
                    0.8f
            );

            return true;
        }

        if (args.length < 1) {

            p.sendMessage("");
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§cMot de passe manquant.");
            p.sendMessage("");
            p.sendMessage("§7Commande : §e/login <motdepasse>");
            p.sendMessage("");

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_BASS,
                    0.8f,
                    0.8f
            );

            return true;
        }

        String ip =
                p.getAddress()
                        .getAddress()
                        .getHostAddress();

        boolean success =
                AuthManager.login(
                        p.getUniqueId()
                                .toString(),
                        p.getName(),
                        args[0],
                        ip
                );

        if (!success) {

            int attempts =
                    FAILED_ATTEMPTS.merge(
                            p.getUniqueId(),
                            1,
                            Integer::sum
                    );

            p.sendMessage("");
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");

            if (attempts >= MAX_ATTEMPTS) {

                p.sendMessage("§cTrop de tentatives incorrectes.");
                p.sendMessage("§7Veuillez contacter un membre du staff");
                p.sendMessage("§7via un §eticket Discord§7.");
                p.sendMessage("");
                p.sendMessage("§8Le staff pourra vérifier votre compte.");

            } else {

                p.sendMessage("§cMot de passe incorrect.");
                p.sendMessage("");
                p.sendMessage("§7Commande : §e/login <motdepasse>");
                p.sendMessage("§8Tentative : §e" + attempts + "§8/§e" + MAX_ATTEMPTS);
            }

            p.sendMessage("");

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_BASS,
                    0.8f,
                    0.8f
            );

            return true;
        }

        FAILED_ATTEMPTS.remove(
                p.getUniqueId()
        );

        AuthListener.login(p);

        return true;
    }
}