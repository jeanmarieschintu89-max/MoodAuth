package fr.moodcraft.auth;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Particle.DustOptions;

import java.util.HashSet;
import java.util.Set;

public class AuthListener implements Listener {

    private static final Set<Player> logged = new HashSet<>();

    public static boolean isLogged(Player p) {
        return logged.contains(p);
    }

    public static void login(Player p) {

        if (logged.contains(p)) return;
        logged.add(p);

        // 🔓 retire effets
        p.removePotionEffect(PotionEffectType.BLINDNESS);
        p.removePotionEffect(PotionEffectType.SLOW);

        // 💣 clean
        for (int i = 0; i < 40; i++) p.sendMessage("");

        // 💥 explosion verte
        p.getWorld().spawnParticle(
                Particle.REDSTONE,
                p.getLocation(),
                120,
                1, 1, 1,
                new DustOptions(Color.fromRGB(0,255,70), 2)
        );

        // 🎉 final
        p.sendTitle("§aMood §eCraft", "§aConnexion réussie", 5, 40, 10);

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        p.sendMessage("§a✔ Accès autorisé");
        p.sendMessage("§7Bienvenue dans §aMood §eCraft");
        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
    }

    public static void logout(Player p) {
        logged.remove(p);
    }

    // =========================
    // 🎬 JOIN CINEMATIC
    // =========================
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        logout(p);

        e.setJoinMessage(null);

        // écran noir + freeze
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 10));

        startAura(p);
        startMatrixRain(p);

        // nettoyage messages plugins
        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            for (int i = 0; i < 80; i++) p.sendMessage("");
            p.resetTitle();
        }, 20L);

        // intro
        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            p.sendTitle("", "§8Connexion au système...", 0, 30, 10);
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 0.5f);
        }, 40L);

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            p.sendTitle("§a§lMood §e§lCraft", "§2[ ACCÈS SÉCURISÉ ]", 5, 40, 10);
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }, 80L);

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {

            for (int i = 0; i < 30; i++) p.sendMessage("");

            p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            p.sendMessage("§a§lMood §e§lCraft");
            p.sendMessage("");

            if (AuthManager.isRegistered(p.getUniqueId().toString())) {
                p.sendMessage("§2> Connexion requise");
                p.sendMessage("§f➜ §a/login <motdepasse>");
            } else {
                p.sendMessage("§2> Création de compte");
                p.sendMessage("§f➜ Tape ton mot de passe");
            }

            p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        }, 120L);

        // actionbar animée
        Bukkit.getScheduler().runTaskTimer(Main.get(), task -> {

            if (isLogged(p) || !p.isOnline()) {
                task.cancel();
                return;
            }

            p.sendActionBar("§2[ SYSTEM ] §aConnexion en attente...");

        }, 120L, 40L);
    }

    // =========================
    // ✨ AURA
    // =========================
    private void startAura(Player p) {

        Bukkit.getScheduler().runTaskTimer(Main.get(), task -> {

            if (isLogged(p) || !p.isOnline()) {
                task.cancel();
                return;
            }

            Location loc = p.getLocation().clone().add(0, 1, 0);

            for (int i = 0; i < 20; i++) {
                double angle = 2 * Math.PI * i / 20;
                double x = Math.cos(angle);
                double z = Math.sin(angle);

                p.getWorld().spawnParticle(
                        Particle.PORTAL,
                        loc.clone().add(x, 0, z),
                        2
                );
            }

        }, 0L, 5L);
    }

    // =========================
    // 🟢 MATRIX RAIN
    // =========================
    private void startMatrixRain(Player p) {

        DustOptions green = new DustOptions(Color.fromRGB(0,255,70), 1.2f);

        Bukkit.getScheduler().runTaskTimer(Main.get(), task -> {

            if (isLogged(p) || !p.isOnline()) {
                task.cancel();
                return;
            }

            Location base = p.getLocation().clone().add(0, 3, 0);

            for (int i = 0; i < 20; i++) {

                double x = (Math.random() - 0.5) * 4;
                double z = (Math.random() - 0.5) * 4;
                double y = Math.random() * 3;

                Location loc = base.clone().add(x, y, z);

                for (int j = 0; j < 6; j++) {
                    p.getWorld().spawnParticle(
                            Particle.REDSTONE,
                            loc.clone().subtract(0, j * 0.3, 0),
                            1,
                            0, 0, 0,
                            green
                    );
                }
            }

        }, 0L, 3L);
    }

    // =========================
    // 🔒 BLOQUAGES
    // =========================
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!isLogged(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p && !isLogged(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {

        Player p = e.getPlayer();

        if (isLogged(p)) return;

        String msg = e.getMessage().toLowerCase();

        if (!msg.startsWith("/login") && !msg.startsWith("/l") && !msg.startsWith("/register")) {
            e.setCancelled(true);
            p.sendMessage("§c🔒 Connecte-toi avec /login");
        }
    }

    // =========================
    // 💬 LOGIN CHAT
    // =========================
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();

        if (isLogged(p)) return;

        e.setCancelled(true);

        String msg = e.getMessage();
        String ip = p.getAddress().getAddress().getHostAddress();

        if (AuthManager.isRegistered(p.getUniqueId().toString())) {

            if (AuthManager.login(p.getUniqueId().toString(), p.getName(), msg, ip)) {
                login(p);
            } else {
                p.sendMessage("§c❌ Mot de passe incorrect");
            }

        } else {

            AuthManager.register(p.getUniqueId().toString(), p.getName(), msg, ip);
            login(p);
            p.sendMessage("§a✔ Compte créé !");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        logout(e.getPlayer());
    }
}