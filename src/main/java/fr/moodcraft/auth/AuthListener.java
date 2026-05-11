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

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthListener
        implements Listener {

    private static final Set<UUID> logged =
            ConcurrentHashMap.newKeySet();

    private static final Map<UUID, Integer> failedAttempts =
            new ConcurrentHashMap<>();

    private static final Set<UUID> blocked =
            ConcurrentHashMap.newKeySet();

    private static final int MAX_ATTEMPTS =
            3;

    public static boolean isLogged(
            Player p
    ) {

        return logged.contains(
                p.getUniqueId()
        );
    }

    public static boolean isBlocked(
            Player p
    ) {

        return blocked.contains(
                p.getUniqueId()
        );
    }

    public static int registerFailedAttempt(
            Player p
    ) {

        int attempts =
                failedAttempts.merge(
                        p.getUniqueId(),
                        1,
                        Integer::sum
                );

        if (attempts >= MAX_ATTEMPTS) {

            blocked.add(
                    p.getUniqueId()
            );
        }

        return attempts;
    }

    public static void resetFailedAttempts(
            Player p
    ) {

        failedAttempts.remove(
                p.getUniqueId()
        );

        blocked.remove(
                p.getUniqueId()
        );
    }

    public static void sendFailedAttemptMessage(
            Player p,
            int attempts
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");

        if (attempts >= MAX_ATTEMPTS) {

            sendStaffMessageContent(p);

        } else {

            p.sendMessage("§cMot de passe incorrect.");
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
    }

    public static void sendBlockedMessage(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
        sendStaffMessageContent(p);
        p.sendMessage("");

        p.playSound(
                p.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_BASS,
                0.8f,
                0.8f
        );
    }

    private static void sendStaffMessageContent(
            Player p
    ) {

        p.sendMessage("§cTrop de tentatives incorrectes.");
        p.sendMessage("§7Veuillez contacter un membre du staff");
        p.sendMessage("§7via un §eticket Discord§7.");
        p.sendMessage("");
        p.sendMessage("§8Le staff pourra vérifier votre compte.");
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

                            resetFailedAttempts(p);

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
                            p.sendMessage("§7Bienvenue, §e" + p.getName() + "§7.");
                            p.sendMessage("");
                            p.sendMessage("§a✔ §fConnexion confirmée.");
                            p.sendMessage("§6➜ §e/menu §7ouvrir le menu principal");
                            p.sendMessage("§6➜ §e/menuville §7ouvrir le menu ville");
                            p.sendMessage("");
                            p.sendMessage("§8Votre compte protège votre progression sur MoodCraft.");
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

        resetFailedAttempts(p);
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

                            if (isBlocked(p)) {

                                p.sendActionBar(
                                        "§6Sécurité §8• §cTrop de tentatives §8• §eTicket Discord"
                                );

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

        Player p =
                e.getPlayer();

        if (isLogged(p)) {
            return;
        }

        Location from =
                e.getFrom();

        Location to =
                e.getTo();

        if (to == null) {
            return;
        }

        if (from.getX() == to.getX()
                && from.getY() == to.getY()
                && from.getZ() == to.getZ()) {
            return;
        }

        Location fixed =
                new Location(
                        from.getWorld(),
                        from.getX(),
                        from.getY(),
                        from.getZ(),
                        to.getYaw(),
                        to.getPitch()
                );

        e.setTo(fixed);
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

        String message =
                e.getMessage()
                        .trim()
                        .toLowerCase();

        if (isAllowedAuthCommand(message)) {
            return;
        }

        e.setCancelled(true);

        if (AuthManager.isRegistered(
                p.getUniqueId()
                        .toString()
        )) {

            if (isBlocked(p)) {

                sendBlockedMessage(p);
                return;
            }

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

    private boolean isAllowedAuthCommand(
            String message
    ) {

        String command =
                message.split(" ")[0];

        return command.equals("/login")
                || command.equals("/register")
                || command.equals("/l");
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

        String password =
                e.getMessage();

        String uuid =
                p.getUniqueId()
                        .toString();

        String name =
                p.getName();

        String ip =
                p.getAddress()
                        .getAddress()
                        .getHostAddress();

        Bukkit.getScheduler()
                .runTaskAsynchronously(
                        Main.get(),
                        () -> {

                            if (!p.isOnline()) {
                                return;
                            }

                            if (AuthManager.isRegistered(uuid)) {

                                if (isBlocked(p)) {

                                    Bukkit.getScheduler()
                                            .runTask(
                                                    Main.get(),
                                                    () -> {

                                                        if (!p.isOnline()) {
                                                            return;
                                                        }

                                                        sendBlockedMessage(p);
                                                    }
                                            );

                                    return;
                                }

                                boolean success =
                                        AuthManager.login(
                                                uuid,
                                                name,
                                                password,
                                                ip
                                        );

                                if (success) {

                                    login(p);
                                    return;
                                }

                                int attempts =
                                        registerFailedAttempt(p);

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

                                                    sendFailedAttemptMessage(
                                                            p,
                                                            attempts
                                                    );
                                                }
                                        );

                                return;
                            }

                            AuthManager.register(
                                    uuid,
                                    name,
                                    password,
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