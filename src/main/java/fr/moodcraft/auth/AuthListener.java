package fr.moodcraft.auth;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AuthListener
        implements Listener {

    private static final Set<UUID> logged =
            new HashSet<>();

    private static final Set<UUID> blocked =
            new HashSet<>();

    private static final Map<UUID, Integer> failedAttempts =
            new HashMap<>();

    private static final int MAX_ATTEMPTS =
            3;

    public static boolean isLogged(
            Player player
    ) {

        return logged.contains(
                player.getUniqueId()
        );
    }

    public static boolean isBlocked(
            Player player
    ) {

        return blocked.contains(
                player.getUniqueId()
        );
    }

    public static int registerFailedAttempt(
            Player player
    ) {

        int amount =
                failedAttempts.getOrDefault(
                        player.getUniqueId(),
                        0
                ) + 1;

        failedAttempts.put(
                player.getUniqueId(),
                amount
        );

        if (amount >= MAX_ATTEMPTS) {

            blocked.add(
                    player.getUniqueId()
            );
        }

        return amount;
    }

    public static void resetFailedAttempts(
            Player player
    ) {

        failedAttempts.remove(
                player.getUniqueId()
        );

        blocked.remove(
                player.getUniqueId()
        );
    }

    public static void sendFailedAttemptMessage(
            Player player,
            int attempts
    ) {

        player.sendMessage("");
        player.sendMessage("§8----- §6✦ Sécurité MoodCraft ✦ §8-----");
        player.sendMessage("");

        if (attempts >= MAX_ATTEMPTS) {

            sendStaffMessageContent(player);

        } else {

            player.sendMessage("§c✖ §fMot de passe incorrect.");
            player.sendMessage(detail("Tentative : §e" + attempts + "§8/§e" + MAX_ATTEMPTS));
            player.sendMessage("");
            player.sendMessage("§e➜ §e/login <motdepasse> §7réessayer");
            player.sendMessage("§e➜ §e/discord §7support et ticket");
            player.sendMessage("§e➜ §e/site §7site officiel");
        }

        player.sendMessage("");

        player.playSound(
                player.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_BASS,
                0.8f,
                0.8f
        );
    }

    public static void sendBlockedMessage(
            Player player
    ) {

        player.sendMessage("");
        player.sendMessage("§8----- §6✦ Sécurité MoodCraft ✦ §8-----");
        player.sendMessage("");

        sendStaffMessageContent(player);

        player.sendMessage("");

        player.playSound(
                player.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_BASS,
                0.8f,
                0.8f
        );
    }

    private static void sendStaffMessageContent(
            Player player
    ) {

        player.sendMessage("§c✖ §fTrop de tentatives incorrectes.");
        player.sendMessage(detail("Contactez un membre du staff via un §eticket Discord§7."));
        player.sendMessage("");
        player.sendMessage("§e➜ §e/discord §7support et ticket");
        player.sendMessage("§e➜ §e/site §7site officiel");
        player.sendMessage("");
        player.sendMessage(detail("Le staff pourra vérifier votre compte."));
    }

    public static void login(
            Player player
    ) {

        if (isLogged(player)) {
            return;
        }

        Bukkit.getScheduler().runTask(
                Main.get(),
                () -> {

                    logged.add(
                            player.getUniqueId()
                    );

                    resetFailedAttempts(player);

                    player.removePotionEffect(
                            PotionEffectType.BLINDNESS
                    );

                    player.removePotionEffect(
                            PotionEffectType.SLOW
                    );

                    player.sendActionBar(
                            "§a✔ §fConnexion confirmée §8• §eBienvenue sur MoodCraft"
                    );

                    player.sendMessage("");
                    player.sendMessage("§8----- §6✦ Sécurité MoodCraft ✦ §8-----");
                    player.sendMessage("");
                    player.sendMessage("§a✔ §fConnexion confirmée.");
                    player.sendMessage("");
                    player.sendMessage("§e➜ §fBienvenue sur §aMood§6Craft§f.");
                    player.sendMessage("");
                    player.sendMessage("§e➜ §e/menu §7menu principal");
                    player.sendMessage("§e➜ §e/menuville §7menu ville");
                    player.sendMessage("§e➜ §e/discord §7support et communauté");
                    player.sendMessage("§e➜ §e/site §7site officiel");
                    player.sendMessage("");
                    player.sendMessage(detail("Votre compte protège votre progression sur MoodCraft."));
                    player.sendMessage("");

                    player.playSound(
                            player.getLocation(),
                            Sound.UI_TOAST_CHALLENGE_COMPLETE,
                            0.8f,
                            1.1f
                    );

                    sendMoodBusinessAlerts(player);
                }
        );
    }

    public static void logout(
            Player player
    ) {

        logged.remove(
                player.getUniqueId()
        );

        resetFailedAttempts(player);
    }

    @EventHandler
    public void onJoin(
            PlayerJoinEvent e
    ) {

        Player p =
                e.getPlayer();

        logout(p);

        e.setJoinMessage(
                "§a[+] §f" + p.getName() + " §7a rejoint §aMood§6Craft"
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

        Bukkit.getScheduler().runTaskLater(
                Main.get(),
                () -> {

                    if (!p.isOnline() || isLogged(p)) {
                        return;
                    }

                    if (AuthManager.isRegistered(
                            p.getUniqueId().toString()
                    )) {

                        sendLoginMessage(p);
                        startLoginActionBar(p);

                    } else {

                        sendRegisterMessage(p);
                        startRegisterActionBar(p);
                    }
                },
                35L
        );
    }

    private void sendLoginMessage(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Sécurité MoodCraft ✦ §8-----");
        p.sendMessage("");
        p.sendMessage("§e➜ §fVeuillez vous connecter.");
        p.sendMessage("");
        p.sendMessage("§e➜ §e/login <motdepasse> §7connexion au compte");
        p.sendMessage("§e➜ §e/discord §7support et communauté");
        p.sendMessage("§e➜ §e/site §7site officiel");
        p.sendMessage("");
        p.sendMessage(detail("Votre compte protège votre progression sur MoodCraft."));
        p.sendMessage("");
    }

    private void sendRegisterMessage(
            Player p
    ) {

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Sécurité MoodCraft ✦ §8-----");
        p.sendMessage("");
        p.sendMessage("§e➜ §fVeuillez créer votre compte.");
        p.sendMessage("");
        p.sendMessage("§e➜ §e/register <motdepasse> §7création du compte");
        p.sendMessage("§e➜ §e/discord §7support et communauté");
        p.sendMessage("§e➜ §e/site §7site officiel");
        p.sendMessage("");
        p.sendMessage(detail("Ce compte protège votre progression sur MoodCraft."));
        p.sendMessage("");
    }

    private void startLoginActionBar(
            Player p
    ) {

        new BukkitRunnable() {

            @Override
            public void run() {

                if (!p.isOnline() || isLogged(p)) {

                    cancel();
                    return;
                }

                p.sendActionBar(
                        "§6Sécurité §8• §fConnexion requise §8• §e/login <motdepasse>"
                );
            }
        }.runTaskTimer(
                Main.get(),
                0L,
                40L
        );
    }

    private void startRegisterActionBar(
            Player p
    ) {

        new BukkitRunnable() {

            @Override
            public void run() {

                if (!p.isOnline() || isLogged(p)) {

                    cancel();
                    return;
                }

                p.sendActionBar(
                        "§6Sécurité §8• §fCompte requis §8• §e/register <motdepasse>"
                );
            }
        }.runTaskTimer(
                Main.get(),
                0L,
                40L
        );
    }

    private void startAura(
            Player p
    ) {

        new BukkitRunnable() {

            @Override
            public void run() {

                if (!p.isOnline() || isLogged(p)) {

                    cancel();
                    return;
                }

                p.playSound(
                        p.getLocation(),
                        Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                        0.15f,
                        1.8f
                );
            }
        }.runTaskTimer(
                Main.get(),
                0L,
                60L
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

        if (e.getTo() == null) {
            return;
        }

        if (e.getFrom().getX() == e.getTo().getX()
                && e.getFrom().getY() == e.getTo().getY()
                && e.getFrom().getZ() == e.getTo().getZ()) {
            return;
        }

        e.setTo(
                e.getFrom()
        );
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

    @EventHandler
    public void onDamageByEntity(
            EntityDamageByEntityEvent e
    ) {

        if (e.getDamager() instanceof Player p
                && !isLogged(p)) {

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(
            BlockBreakEvent e
    ) {

        if (!isLogged(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(
            BlockPlaceEvent e
    ) {

        if (!isLogged(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
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
                        .trim()
                        .toLowerCase();

        if (isAllowedAuthCommand(msg)) {
            return;
        }

        e.setCancelled(true);

        if (AuthManager.isRegistered(
                p.getUniqueId().toString()
        )) {

            if (isBlocked(p)) {

                sendBlockedMessage(p);
                return;
            }

            sendLoginMessage(p);

        } else {

            sendRegisterMessage(p);
        }
    }

    private boolean isAllowedAuthCommand(
            String msg
    ) {

        String base =
                msg.split(" ")[0];

        return base.equals("/login")
                || base.equals("/register")
                || base.equals("/discord")
                || base.equals("/site")
                || base.equals("/l");
    }

    @EventHandler
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
                p.getUniqueId().toString();

        String name =
                p.getName();

        String ip =
                p.getAddress()
                        .getAddress()
                        .getHostAddress();

        Bukkit.getScheduler().runTaskAsynchronously(
                Main.get(),
                () -> {

                    if (!p.isOnline()) {
                        return;
                    }

                    if (AuthManager.isRegistered(uuid)) {

                        if (isBlocked(p)) {

                            Bukkit.getScheduler().runTask(
                                    Main.get(),
                                    () -> sendBlockedMessage(p)
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

                        Bukkit.getScheduler().runTask(
                                Main.get(),
                                () -> sendFailedAttemptMessage(
                                        p,
                                        attempts
                                )
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

    private static void sendMoodBusinessAlerts(
            Player p
    ) {

        if (!Bukkit.getPluginManager()
                .isPluginEnabled("MoodBusiness")) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(
                Main.get(),
                () -> {

                    try {

                        Class<?> api =
                                Class.forName(
                                        "fr.moodcraft.business.api.MoodBusinessAPI"
                                );

                        Method method =
                                api.getMethod(
                                        "sendPendingAlerts",
                                        Player.class
                                );

                        method.invoke(
                                null,
                                p
                        );

                    } catch (Exception ignored) {

                        Bukkit.getConsoleSender().sendMessage(
                                "§c[MoodAuth] Impossible d'envoyer les alertes MoodBusiness."
                        );
                    }
                },
                20L
        );
    }

    private static String detail(String text) {
        return "§8• §7" + text;
    }
}
