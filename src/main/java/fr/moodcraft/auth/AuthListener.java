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

        p.removePotionEffect(PotionEffectType.BLINDNESS);
        p.removePotionEffect(PotionEffectType.SLOW);

        // ✨ effet léger
        p.getWorld().spawnParticle(
                Particle.END_ROD,
                p.getLocation().add(0, 1, 0),
                25,
                0.3, 0.5, 0.3,
                0
        );

        // 🎬 titre
        p.sendTitle("§a§lMood §e§lCraft", "§bBienvenue", 10, 40, 10);

        // 💎 MESSAGE LOGIN SUCCESS
        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {

            p.sendMessage("");
            p.sendMessage("§8╔══════════════════════════════╗");
            p.sendMessage("§8║   §a§lMood §e§lCraft §8• §bConnexion");
            p.sendMessage("§8╠══════════════════════════════╣");

            p.sendMessage("§8║ §a✔ §fBienvenue §e" + p.getName());
            p.sendMessage("§8║");

            p.sendMessage("§8║ §7Développe ta §aVille §7et tes §eMétiers");
            p.sendMessage("§8║ §7et domine la §6Bourse §7de §aMood §eCraft");

            p.sendMessage("§8║");
            p.sendMessage("§8║ §6➜ §e/menu §7pour commencer");

            p.sendMessage("§8╚══════════════════════════════╝");
            p.sendMessage("");

        }, 10L);

        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.2f);
    }

    public static void logout(Player p) {
        logged.remove(p);
    }

    // =========================
    // JOIN
    // =========================
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        logout(p);

        e.setJoinMessage(null);

        // 🔒 freeze court
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 10));

        startAura(p);

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            for (int i = 0; i < 40; i++) p.sendMessage("");
            p.resetTitle();
        }, 2L);

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            p.sendTitle("§a§lMood §e§lCraft", "§7Chargement...", 10, 30, 10);
        }, 20L);

        // 💎 MESSAGE LOGIN
        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {

            p.sendMessage("");
            p.sendMessage("§8╔══════════════════════════════╗");
            p.sendMessage("§8║   §a§lMood §e§lCraft §8• §bAuthentification");
            p.sendMessage("§8╠══════════════════════════════╣");

            if (AuthManager.isRegistered(p.getUniqueId().toString())) {

                p.sendMessage("§8║ §6🔐 §eConnexion requise");
                p.sendMessage("§8║");
                p.sendMessage("§8║ §fEntre ton mot de passe :");
                p.sendMessage("§8║ §6➜ §e/login §7<motdepasse>");
                p.sendMessage("§8║");

            } else {

                p.sendMessage("§8║ §d✨ §eNouveau joueur");
                p.sendMessage("§8║");
                p.sendMessage("§8║ §fChoisis ton mot de passe :");
                p.sendMessage("§8║ §7Tape-le directement dans le chat");
                p.sendMessage("§8║");
            }

            p.sendMessage("§8║ §a✔ §7Données sauvegardées");
            p.sendMessage("§8╚══════════════════════════════╝");
            p.sendMessage("");

        }, 60L);

        // 🎯 actionbar rappel
        Bukkit.getScheduler().runTaskTimer(Main.get(), task -> {

            if (isLogged(p) || !p.isOnline()) {
                task.cancel();
                return;
            }

            p.sendActionBar("§aMood §eCraft §8• §6/login §7requis");

        }, 60L, 40L);
    }

    // =========================
    // AURA
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
    // BLOQUAGES
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
            p.sendMessage("§8[§c✖§8] §cConnecte-toi avec /login");
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
            p.sendMessage("§8[§a✔§8] §aCompte créé avec succès !");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        logout(e.getPlayer());
    }
}