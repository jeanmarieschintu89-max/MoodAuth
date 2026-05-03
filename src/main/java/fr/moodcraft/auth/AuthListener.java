package fr.moodcraft.auth;

import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class AuthListener implements Listener {

    private static final Set<Player> logged = new HashSet<>();

    // =========================
    // 🔐 LOGIN STATE
    // =========================
    public static boolean isLogged(Player p) {
        return logged.contains(p);
    }

    public static void login(Player p) {
        logged.add(p);
    }

    public static void logout(Player p) {
        logged.remove(p);
    }

    // =========================
    // 👋 JOIN
    // =========================
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        logout(p);

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        p.sendMessage("§6Bienvenue sur §eMoodCraft ✨");

        if (AuthManager.isRegistered(p.getUniqueId().toString())) {

            p.sendMessage("§eTon compte existe déjà.");
            p.sendMessage("§7➡ §fConnecte-toi avec : §e/login <motdepasse>");

        } else {

            p.sendMessage("§eAucun compte trouvé.");
            p.sendMessage("§7➡ §fCrée-en un avec : §e/register <motdepasse>");
        }

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    // =========================
    // 🚶 BLOCAGE MOUVEMENT
    // =========================
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!isLogged(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    // =========================
    // 💬 BLOCAGE COMMANDES
    // =========================
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {

        Player p = e.getPlayer();

        if (isLogged(p)) return;

        String msg = e.getMessage().toLowerCase();

        if (!msg.startsWith("/login") && !msg.startsWith("/register")) {

            e.setCancelled(true);

            p.sendMessage("§c⚠ Tu dois te connecter avant de jouer.");
            p.sendMessage("§7Utilise : §e/login <motdepasse>");
        }
    }

    // =========================
    // ❤️ BLOCAGE DÉGÂTS
    // =========================
    @EventHandler
    public void onDamage(EntityDamageEvent e) {

        if (e.getEntity() instanceof Player p) {

            if (!isLogged(p)) {
                e.setCancelled(true);
            }
        }
    }

    // =========================
    // 🗑️ BLOCAGE DROP
    // =========================
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {

        if (!isLogged(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    // =========================
    // 🖱️ BLOCAGE INTERACTION
    // =========================
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        if (!isLogged(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    // =========================
    // 🚪 QUIT CLEAN
    // =========================
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        logout(e.getPlayer());
    }
}