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

    private static void clearChat(Player p) {
        for (int i = 0; i < 80; i++) {
            p.sendMessage("");
        }
    }

    public static void login(Player p) {

        if (isLogged(p)) return;

        Bukkit.getScheduler().runTask(Main.get(), () -> {

            if (!p.isOnline()) return;
            if (isLogged(p)) return;

            logged.add(p.getUniqueId());

            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.removePotionEffect(PotionEffectType.SLOW);

            clearChat(p);

            p.spawnParticle(
                    Particle.END_ROD,
                    p.getLocation(),
                    60,
                    0.5,
                    1,
                    0.5,
                    0
            );

            p.sendTitle(
                    "§a§lMood§e§lCraft",
                    "§aAccès confirmé",
                    10,
                    50,
                    10
            );

            p.sendMessage("§8----- §aMood§eCraft §8-----");
            p.sendMessage("§a✔ §fSession ouverte : §e" + p.getName());
            p.sendMessage("§7Bienvenue sur le serveur.");
            p.sendMessage("");
            p.sendMessage("§6➜ §e/menu §7ouvrir le menu principal");
            p.sendMessage("§8----- §7Sécurité §8-----");
            p.sendMessage("§7Changer le mot de passe : §e/changepass");
            p.sendMessage("");

            p.playSound(
                    p.getLocation(),
                    Sound.UI_TOAST_CHALLENGE_COMPLETE,
                    1f,
                    1f
            );
        });
    }

    public static void logout(Player p) {
        logged.remove(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        logout(p);

        e.setJoinMessage(
                "§8[§a+§8] §e" + p.getName() + " §7a rejoint §aMood§eCraft"
        );

        p.addPotionEffect(
                new PotionEffect(PotionEffectType.BLINDNESS, 9999, 1)
        );

        p.addPotionEffect(
                new PotionEffect(PotionEffectType.SLOW, 9999, 10)
        );

        startAura(p);

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {

            if (!p.isOnline()) return;
            if (isLogged(p)) return;

            p.sendTitle(
                    "§a§lMood§e§lCraft",
                    "§7Vérification de session...",
                    10,
                    40,
                    10
            );

        }, 40L);

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {

            if (!p.isOnline()) return;
            if (isLogged(p)) return;

            clearChat(p);

            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");

            if (AuthManager.isRegistered(p.getUniqueId().toString())) {

                p.sendMessage("§c🔒 §fCompte détecté");
                p.sendMessage("§7Ta session est verrouillée.");
                p.sendMessage("");
                p.sendMessage("§6➜ §e/login <motdepasse>");
                p.sendMessage("");

                Bukkit.getScheduler().runTaskTimer(Main.get(), task -> {

                    if (isLogged(p) || !p.isOnline()) {
                        task.cancel();
                        return;
                    }

                    p.sendActionBar("§c🔒 Session verrouillée §8• §e/login <motdepasse>");

                }, 0L, 40L);

            } else {

                p.sendMessage("§a✨ §fPremière connexion");
                p.sendMessage("§7Crée ton accès personnel.");
                p.sendMessage("");
                p.sendMessage("§6➜ §e/register <motdepasse>");
                p.sendMessage("");

                Bukkit.getScheduler().runTaskTimer(Main.get(), task -> {

                    if (isLogged(p) || !p.isOnline()) {
                        task.cancel();
                        return;
                    }

                    p.sendActionBar("§a✨ Nouveau compte §8• §e/register <motdepasse>");

                }, 0L, 40L);
            }

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

        if (!isLogged(e.getPlayer())) {
            e.setCancelled(true);
        }
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
                || msg.startsWith("/l")) {
            return;
        }

        e.setCancelled(true);

        if (AuthManager.isRegistered(p.getUniqueId().toString())) {
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§c🔒 §fConnecte-toi d'abord.");
            p.sendMessage("§6➜ §e/login <motdepasse>");
        } else {
            p.sendMessage("§8----- §6Sécurité MoodCraft §8-----");
            p.sendMessage("§a✨ §fCrée ton compte d'abord.");
            p.sendMessage("§6➜ §e/register <motdepasse>");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();

        if (p.hasMetadata("input_active")) return;
        if (isLogged(p)) return;

        e.setCancelled(true);

        String msg = e.getMessage();

        Bukkit.getScheduler().runTaskAsynchronously(Main.get(), () -> {

            if (!p.isOnline()) return;

            String ip = p.getAddress().getAddress().getHostAddress();
            String uuid = p.getUniqueId().toString();

            if (AuthManager.isRegistered(uuid)) {

                boolean success = AuthManager.login(
                        uuid,
                        p.getName(),
                        msg,
                        ip
                );

                if (success) {
                    login(p);
                    return;
                }

                Bukkit.getScheduler().runTask(Main.get(), () -> {

                    if (!p.isOnline()) return;
                    if (isLogged(p)) return;

                    p.sendMessage("§8[§c✖§8] §cMot de passe incorrect.");
                    p.sendMessage("§6➜ §e/login <motdepasse>");
                });

                return;
            }

            AuthManager.register(
                    uuid,
                    p.getName(),
                    msg,
                    ip
            );

            login(p);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        logout(e.getPlayer());
    }
}