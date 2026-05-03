package fr.moodcraft.auth;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class AuthListener implements Listener {

    private static final Set<Player> logged = new HashSet<>();

    public static boolean isLogged(Player p) {
        return logged.contains(p);
    }

    public static void login(Player p) {
        logged.add(p);

        // 🔥 CLEAR CHAT
        for (int i = 0; i < 40; i++) p.sendMessage("");

        // 🎉 MESSAGE PROPRE
        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        p.sendMessage("§6✨ §lBienvenue sur §e§lMoodCraft");
        p.sendMessage("");
        p.sendMessage("§a✔ Connexion réussie");
        p.sendMessage("§7Profite de ton aventure !");
        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        p.sendTitle("§6MoodCraft", "§aConnexion réussie", 10, 40, 10);
    }

    public static void logout(Player p) {
        logged.remove(p);
    }

    // =========================
    // 🚀 JOIN
    // =========================
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        logout(p);

        // ❌ supprime message join serveur
        e.setJoinMessage(null);

        // 🔥 CLEAR CHAT
        for (int i = 0; i < 40; i++) p.sendMessage("");

        // ❌ reset titres plugins
        p.resetTitle();

        // 🎬 ACCUEIL STYLÉ
        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        p.sendMessage("§6✨ §lBienvenue sur §e§lMoodCraft");
        p.sendMessage("");

        if (AuthManager.isRegistered(p.getUniqueId().toString())) {

            p.sendMessage("§7Compte détecté");
            p.sendMessage("§e🔐 Connecte-toi");
            p.sendMessage("");
            p.sendMessage("§f➜ §e/login <motdepasse>");

        } else {

            p.sendMessage("§7Nouveau joueur");
            p.sendMessage("§e🆕 Crée ton compte");
            p.sendMessage("");
            p.sendMessage("§f➜ §eTape ton mot de passe");

        }

        p.sendMessage("");
        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.2f);
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

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {

        Player p = e.getPlayer();

        if (isLogged(p)) return;

        String msg = e.getMessage().toLowerCase();

        if (!msg.startsWith("/login") && !msg.startsWith("/register")) {
            e.setCancelled(true);
            p.sendMessage("§c⚠ Connecte-toi avec /login");
        }
    }

    // =========================
    // 💬 CHAT LOGIN SYSTEM
    // =========================
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();

        if (isLogged(p)) return;

        String msg = e.getMessage();
        e.setCancelled(true);

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

            p.sendMessage("§a✔ Compte créé avec succès !");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        logout(e.getPlayer());
    }
}