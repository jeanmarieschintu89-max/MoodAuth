package fr.moodcraft.auth;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthListener implements Listener {

    // 🔥 FIX CRITIQUE → UUID (plus jamais de bug de détection)
    private static final Set<UUID> logged = new HashSet<>();

    public static boolean isLogged(Player p) {
        return logged.contains(p.getUniqueId());
    }

    public static void login(Player p) {

        if (isLogged(p)) return;
        logged.add(p.getUniqueId());

        // 🔓 retire effets
        p.removePotionEffect(PotionEffectType.BLINDNESS);
        p.removePotionEffect(PotionEffectType.SLOW);

        // 💣 clean écran
        for (int i = 0; i < 40; i++) p.sendMessage("");

        // ✨ effet premium léger
        p.getWorld().spawnParticle(
                Particle.END_ROD,
                p.getLocation(),
                40,
                0.4, 1, 0.4,
                0
        );

        // 🎬 titre
        p.sendTitle("§a§lMood§e§lCraft", "§aConnexion réussie", 10, 40, 10);

        // 💎 MESSAGE PREMIUM
        p.sendMessage("");
        p.sendMessage("§8╔════════════════════════════╗");
        p.sendMessage("§8║   §a§lMood§e§lCraft §8• §6Accès validé");
        p.sendMessage("§8╠════════════════════════════╣");
        p.sendMessage("§8║ §a✔ §fBienvenue §e" + p.getName());
        p.sendMessage("§8║");
        p.sendMessage("§8║ §7Ta progression est chargée");
        p.sendMessage("§8║ §7et prête à évoluer.");
        p.sendMessage("§8║");
        p.sendMessage("§8║ §7Ville §8• §aMétiers §8• §eBourse");
        p.sendMessage("§8║");
        p.sendMessage("§8║ §6➜ §e/menu §7pour commencer");
        p.sendMessage("§8╚════════════════════════════╝");
        p.sendMessage("");

        p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
    }

    public static void logout(Player p) {
        logged.remove(p.getUniqueId());
    }

    // =========================
    // 🎬 JOIN
    // =========================
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        logout(p);

        e.setJoinMessage(null);

        // 🔒 freeze + écran noir
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 10));

        startAura(p);

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            for (int i = 0; i < 60; i++) p.sendMessage("");
            p.resetTitle();
        }, 2L);

        // 🎬 intro
        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            p.sendTitle("§a§lMood§e§lCraft", "§7Chargement...", 10, 40, 10);
        }, 40L);

        // 🔐 LOGIN SCREEN PREMIUM
        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {

            for (int i = 0; i < 30; i++) p.sendMessage("");

            p.sendMessage("");
            p.sendMessage("§8╔════════════════════════════╗");
            p.sendMessage("§8║   §a§lMood§e§lCraft §8• §bAuthentification");
            p.sendMessage("§8╠════════════════════════════╣");

            if (AuthManager.isRegistered(p.getUniqueId().toString())) {

                p.sendMessage("§8║ §c🔒 §fAccès restreint");
                p.sendMessage("§8║");
                p.sendMessage("§8║ §7Veuillez vous connecter");
                p.sendMessage("§8║ §6➜ §e/login <motdepasse>");
                p.sendMessage("§8║");

            } else {

                p.sendMessage("§8║ §a✨ §fCréation de compte");
                p.sendMessage("§8║");
                p.sendMessage("§8║ §7Entre un mot de passe dans le chat");
                p.sendMessage("§8║");
            }

            p.sendMessage("§8║ §8Vos données sont sécurisées");
            p.sendMessage("§8╚════════════════════════════╝");
            p.sendMessage("");

        }, 100L);

        // 🔔 actionbar
        Bukkit.getScheduler().runTaskTimer(Main.get(), task -> {

            if (isLogged(p) || !p.isOnline()) {
                task.cancel();
                return;
            }

            p.sendActionBar("§aMood§eCraft §8• §eConnexion requise");

        }, 100L, 40L);
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

            for (int i = 0; i < 10; i++) {
                double angle = 2 * Math.PI * i / 10;
                double x = Math.cos(angle);
                double z = Math.sin(angle);

                p.getWorld().spawnParticle(
                        Particle.END_ROD,
                        loc.clone().add(x, 0, z),
                        1
                );
            }

        }, 0L, 10L);
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

    // 🔥 FIX → ne bloque QUE si PAS connecté
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {

        Player p = e.getPlayer();

        if (isLogged(p)) return;

        String msg = e.getMessage().toLowerCase();

        if (!msg.startsWith("/login") && !msg.startsWith("/register")) {
            e.setCancelled(true);
            p.sendMessage("§c➜ Connecte-toi avec §e/login");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
                p.sendMessage("§8[§c✖§8] §cMot de passe incorrect");
            }

        } else {

            AuthManager.register(p.getUniqueId().toString(), p.getName(), msg, ip);
            login(p);
            p.sendMessage("§8[§a✔§8] §aCompte créé !");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        logout(e.getPlayer());
    }
}