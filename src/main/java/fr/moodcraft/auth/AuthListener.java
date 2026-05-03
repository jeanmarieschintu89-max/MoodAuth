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

        // ✨ effet premium doux
        p.getWorld().spawnParticle(
                Particle.END_ROD,
                p.getLocation(),
                40,
                0.4, 1, 0.4,
                0
        );

        // 🎉 titre premium
        p.sendTitle("§f§lMood §6§lCraft", "§7Connexion réussie", 10, 40, 10);

        // 📜 message RP propre
        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        p.sendMessage("§6Bienvenue sur §fMoodCraft");
        p.sendMessage("");
        p.sendMessage("§7✔ Vous êtes maintenant connecté");
        p.sendMessage("§7Profitez de l'économie et des contrats");
        p.sendMessage("");
        p.sendMessage("§e➜ Utilisez §f/menu §epour commencer");
        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        p.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1f, 1f);
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

        // 🔒 freeze + écran noir
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 10));

        startAura(p);

        // 💣 wipe propre
        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            for (int i = 0; i < 60; i++) p.sendMessage("");
            p.resetTitle();
        }, 2L);

        // 🎬 intro douce
        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            p.sendTitle("§f§lMood §6§lCraft", "§7Chargement...", 10, 40, 10);
            p.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1f, 0.8f);
        }, 40L);

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {

            for (int i = 0; i < 30; i++) p.sendMessage("");

            p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            p.sendMessage("§6Bienvenue sur §fMoodCraft");
            p.sendMessage("");

            if (AuthManager.isRegistered(p.getUniqueId().toString())) {
                p.sendMessage("§7Veuillez vous connecter");
                p.sendMessage("§e➜ §f/login <motdepasse>");
            } else {
                p.sendMessage("§7Création de votre compte");
                p.sendMessage("§e➜ Entrez un mot de passe dans le chat");
            }

            p.sendMessage("");
            p.sendMessage("§7Votre progression sera sauvegardée");
            p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        }, 100L);

        // actionbar propre
        Bukkit.getScheduler().runTaskTimer(Main.get(), task -> {

            if (isLogged(p) || !p.isOnline()) {
                task.cancel();
                return;
            }

            p.sendActionBar("§6MoodCraft §8• §7Connexion requise");

        }, 100L, 40L);
    }

    // =========================
    // ✨ AURA PREMIUM
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {

        Player p = e.getPlayer();

        if (isLogged(p)) return;

        String msg = e.getMessage().toLowerCase();

        if (!msg.startsWith("/login") && !msg.startsWith("/l") && !msg.startsWith("/register")) {
            e.setCancelled(true);
            p.sendMessage("§cVeuillez vous connecter avec /login");
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
                p.sendMessage("§cMot de passe incorrect");
            }

        } else {

            AuthManager.register(p.getUniqueId().toString(), p.getName(), msg, ip);
            login(p); // ✔ PAS de double message
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        logout(e.getPlayer());
    }
}