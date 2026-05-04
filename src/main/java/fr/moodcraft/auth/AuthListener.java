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

    private static final Set<UUID> logged = new HashSet<>();

    public static boolean isLogged(Player p) {
        return logged.contains(p.getUniqueId());
    }

    public static void login(Player p) {

        if (isLogged(p)) return;
        logged.add(p.getUniqueId());

        p.removePotionEffect(PotionEffectType.BLINDNESS);
        p.removePotionEffect(PotionEffectType.SLOW);

        for (int i = 0; i < 30; i++) p.sendMessage("");

        p.spawnParticle(Particle.END_ROD, p.getLocation(), 60, 0.5, 1, 0.5, 0);

        p.sendTitle("§a§lMood§e§lCraft", "§aConnexion réussie", 10, 50, 10);

        // ✅ ON GARDE SEULEMENT LE TABLEAU (propre)
        p.sendMessage("");
        p.sendMessage("§8╔════════════════════════════╗");
        p.sendMessage("§8║   §a§lMood§e§lCraft §8• §aBienvenue");
        p.sendMessage("§8╠════════════════════════════╣");
        p.sendMessage("§8║ §a✔ §fConnecté en tant que §e" + p.getName());
        p.sendMessage("§8║");
        p.sendMessage("§8║ §7Ton aventure peut commencer.");
        p.sendMessage("§8║");
        p.sendMessage("§8║ §6➜ §e/menu §7pour ouvrir le menu");
        p.sendMessage("§8╚════════════════════════════╝");
        p.sendMessage("");

        p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
    }

    public static void logout(Player p) {
        logged.remove(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        logout(p);

        e.setJoinMessage("§8[§a+§8] §e" + p.getName() + " §7a rejoint §aMood§eCraft");

        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 10));

        startAura(p);

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            p.sendTitle("§a§lMood§e§lCraft", "§7Chargement...", 10, 40, 10);
        }, 40L);

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {

            for (int i = 0; i < 20; i++) p.sendMessage("");

            p.sendMessage("§8╔════════════════════════════╗");
            p.sendMessage("§8║   §a§lMood§e§lCraft §8• §bAuthentification");
            p.sendMessage("§8╠════════════════════════════╣");

            if (AuthManager.isRegistered(p.getUniqueId().toString())) {

                p.sendMessage("§8║ §c🔒 §fCompte détecté");
                p.sendMessage("§8║");
                p.sendMessage("§8║ §7Entre ton mot de passe :");
                p.sendMessage("§8║");
                p.sendMessage("§8║ §6➜ §e/login <motdepasse>");
                p.sendMessage("§8║");

                Bukkit.getScheduler().runTaskTimer(Main.get(), task -> {

                    if (isLogged(p) || !p.isOnline()) {
                        task.cancel();
                        return;
                    }

                    p.sendActionBar("§c🔒 Connecte-toi avec §e/login");

                }, 0L, 40L);

            } else {

                p.sendMessage("§8║ §a✨ §fNouveau joueur");
                p.sendMessage("§8║");
                p.sendMessage("§8║ §7Crée ton compte :");
                p.sendMessage("§8║");
                p.sendMessage("§8║ §6➜ §e/register <motdepasse>");
                p.sendMessage("§8║");

                Bukkit.getScheduler().runTaskTimer(Main.get(), task -> {

                    if (isLogged(p) || !p.isOnline()) {
                        task.cancel();
                        return;
                    }

                    p.sendActionBar("§a✨ Crée ton compte avec §e/register");

                }, 0L, 40L);
            }

            p.sendMessage("§8╚════════════════════════════╝");

        }, 80L);
    }

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

        if (msg.startsWith("/login")
                || msg.startsWith("/register")
                || msg.startsWith("/l")
                || msg.startsWith("/menu")) {
            return;
        }

        e.setCancelled(true);
        p.sendMessage("§c➜ Connecte-toi avec §e/login");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();

        // 🔥 laisse passer tes systèmes d'input
        if (p.hasMetadata("input_active")) return;

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
            login(p); // ✅ déjà le message dans login → pas de doublon
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        logout(e.getPlayer());
    }
}