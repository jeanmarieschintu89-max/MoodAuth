package fr.moodcraft.auth;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityDamageEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthListener
        implements Listener {

    private static final Set<UUID> logged =
            new HashSet<>();

    public static boolean isLogged(
            Player p
    ) {

        return logged.contains(
                p.getUniqueId()
        );
    }

    private static void clearChat(
            Player p
    ) {

        for (int i = 0; i < 60; i++) {
            p.sendMessage("");
        }
    }

    public static void login(
            Player p
    ) {

        if (isLogged(p)) {
            return;
        }

        Bukkit.getScheduler()
                .runTask(
                        Main.get(),
                        () -> {

                            if (!p.isOnline()) {
                                return;
                            }

                            if (isLogged(p)) {
                                return;
                            }

                            logged.add(
                                    p.getUniqueId()
                            );

                            p.removePotionEffect(
                                    PotionEffectType.BLINDNESS
                            );

                            p.removePotionEffect(
                                    PotionEffectType.SLOW
                            );

                            clearChat(p);

                            p.spawnParticle(
                                    Particle.END_ROD,
                                    p.getLocation()
                                            .add(0, 1, 0),
                                    40,
                                    0.45,
                                    0.7,
                                    0.45,
                                    0
                            );

                            p.sendTitle(
                                    "§a§lMood§e§lCraft",
                                    "§fConnexion confirmée",
                                    10,
                                    45,
                                    10
                            );

                            p.sendMessage("");
                            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
                            p.sendMessage("§a✔ §fConnexion confirmée.");
                            p.sendMessage("§7Bienvenue, §e" + p.getName() + "§7.");
                            p.sendMessage("");
                            p.sendMessage("§6➜ §e/menu §7ouvrir le menu principal");
                            p.sendMessage("§6➜ §e/menuville §7ouvrir le menu ville");
                            p.sendMessage("§8----- §7Compte §8-----");
                            p.sendMessage("§7Changer le mot de passe : §e/changepass");
                            p.sendMessage("");

                            p.playSound(
                                    p.getLocation(),
                                    Sound.UI_TOAST_CHALLENGE_COMPLETE,
                                    0.8f,
                                    1.1f
                            );
                        }
                );
    }

    public static void logout(
            Player p
    ) {

        logged.remove(
                p.getUniqueId()
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(
            PlayerJoinEvent e
    ) {

        Player p =
                e.getPlayer();

        logout(p);

        e.setJoinMessage(
                "§8[§a+§8] §e" + p.getName() + " §7a rejoint §aMood§eCraft"
        );

        p.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.BLINDNESS,
                        9999,
                        1
                )
        );

        p.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.SLOW,
                        9999,
                        10
                )
        );

        startAura(p);

        Bukkit.getScheduler()
                .runTaskLater(
                        Main.get(),
                        () -> {

                            if (!p.isOnline()) {
                                return;
                            }

                            if (isLogged(p)) {
                                return;
                            }

                            p.sendTitle(
                                    "§a§lMood§e§lCraft",
                                    "§fConnexion requise",
                                    10,
                                    45,
                                    10
                            );

                        },
                        35L
                );

        Bukkit.getScheduler()
                .runTaskLater(
                        Main.get(),
                        () -> {

                            if (!p.isOnline()) {
                                return;
                            }

                            if (isLogged(p)) {
                                return;
                            }

                            clearChat(p);

                            if (AuthManager.isRegistered(
                                    p.getUniqueId()
                                            .toString()
                            )) {

                                sendLoginMessage(p);
                                startLoginActionBar(p);

                            } else {

                                sendRegisterMessage(p);
                                startRegisterActionBar(p);
                            }

                        },
                        70L
                );
    }

    private void sendLoginMessage(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
        p.sendMessage("§fVeuillez vous connecter.");
        p.sendMessage("");
        p.sendMessage("§7Commande : §e/login <motdepasse>");
        p.sendMessage("");
        p.sendMessage("§8Votre compte protège votre progression sur MoodCraft.");
        p.sendMessage("");
    }

    private void sendRegisterMessage(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
        p.sendMessage("§fVeuillez créer votre compte.");
        p.sendMessage("");
        p.sendMessage("§7Commande : §e/register <motdepasse>");
        p.sendMessage("");
        p.sendMessage("§8Ce compte protège votre progression sur MoodCraft.");
        p.sendMessage("");
    }

    private void startLoginActionBar(
            Player p
    ) {

        Bukkit.getScheduler()
                .runTaskTimer(
                        Main.get(),
                        task -> {

                            if (isLogged(p) || !p.isOnline()) {

                                task.cancel();
                                return;
                            }

                            p.sendActionBar(
                                    "§6Sécurité §8• §fVeuillez vous connecter §8• §e/login <motdepasse>"
                            );

                        },
                        0L,
                        40L
                );
    }

    private void startRegisterActionBar(
            Player p
    ) {

        Bukkit.getScheduler()
                .runTaskTimer(
                        Main.get(),
                        task -> {

                            if (isLogged(p) || !p.isOnline()) {

                                task.cancel();
                                return;
                            }

                            p.sendActionBar(
                                    "§6Sécurité §8• §fVeuillez créer votre compte §8• §e/register <motdepasse>"
                            );

                        },
                        0L,
                        40L
                );
    }

    private void startAura(
            Player p
    ) {

        Bukkit.getScheduler()
                .runTaskTimer(
                        Main.get(),
                        task -> {

                            if (isLogged(p) || !p.isOnline()) {

                                task.cancel();
                                return;
                            }

                            Location loc =
                                    p.getLocation()
                                            .clone()
                                            .add(0, 1, 0);

                            for (int i = 0; i < 8; i++) {

                                double angle =
                                        2 * Math.PI * i / 8;

                                double x =
                                        Math.cos(angle) * 0.75;

                                double z =
                                        Math.sin(angle) * 0.75;

                                p.getWorld()
                                        .spawnParticle(
                                                Particle.END_ROD,
                                                loc.clone()
                                                        .add(x, 0, z),
                                                1,
                                                0,
                                                0,
                                                0,
                                                0
                                        );
                            }

                        },
                        0L,
                        14L
                );
    }

    @EventHandler
    public void onMove(
            PlayerMoveEvent e
    ) {

        if (!isLogged(
                e.getPlayer()
        )) {

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(
            EntityDamageEvent e
    ) {

        if (e.getEntity() instanceof Player p
                && !isLogged(p)) {

            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(
            PlayerCommandPreprocessEvent e
    ) {

        Player p =
                e.getPlayer();

        if (isLogged(p)) {
            return;
        }

        String msg =
                e.getMessage()
                        .toLowerCase();

        if (msg.startsWith("/login")
                || msg.startsWith("/register")
                || msg.startsWith("/l")) {
            return;
        }

        e.setCancelled(true);

        if (AuthManager.isRegistered(
                p.getUniqueId()
                        .toString()
        )) {

            p.sendMessage("");
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§fVeuillez vous connecter.");
            p.sendMessage("§7Commande : §e/login <motdepasse>");
            p.sendMessage("");

        } else {

            p.sendMessage("");
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§fVeuillez créer votre compte.");
            p.sendMessage("§7Commande : §e/register <motdepasse>");
            p.sendMessage("");
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onChat(
            AsyncPlayerChatEvent e
    ) {

        Player p =
                e.getPlayer();

        if (p.hasMetadata("input_active")) {
            return;
        }

        if (isLogged(p)) {
            return;
        }

        e.setCancelled(true);

        String msg =
                e.getMessage();

        Bukkit.getScheduler()
                .runTaskAsynchronously(
                        Main.get(),
                        () -> {

                            if (!p.isOnline()) {
                                return;
                            }

                            String ip =
                                    p.getAddress()
                                            .getAddress()
                                            .getHostAddress();

                            String uuid =
                                    p.getUniqueId()
                                            .toString();

                            if (AuthManager.isRegistered(uuid)) {

                                boolean success =
                                        AuthManager.login(
                                                uuid,
                                                p.getName(),
                                                msg,
                                                ip
                                        );

                                if (success) {

                                    login(p);
                                    return;
                                }

                                Bukkit.getScheduler()
                                        .runTask(
                                                Main.get(),
                                                () -> {

                                                    if (!p.isOnline()) {
                                                        return;
                                                    }

                                                    if (isLogged(p)) {
                                                        return;
                                                    }

                                                    p.sendMessage("");
                                                    p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
                                                    p.sendMessage("§cMot de passe incorrect.");
                                                    p.sendMessage("§7Commande : §e/login <motdepasse>");
                                                    p.sendMessage("");

                                                    p.playSound(
                                                            p.getLocation(),
                                                            Sound.BLOCK_NOTE_BLOCK_BASS,
                                                            0.8f,
                                                            0.8f
                                                    );
                                                }
                                        );

                                return;
                            }

                            AuthManager.register(
                                    uuid,
                                    p.getName(),
                                    msg,
                                    ip
                            );

                            login(p);
                        }
                );
    }

    @EventHandler
    public void onQuit(
            PlayerQuitEvent e
    ) {

        logout(
                e.getPlayer()
        );
    }
}