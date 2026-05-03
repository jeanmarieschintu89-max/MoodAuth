package fr.moodcraft.auth;

import org.bukkit.Bukkit;
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

        p.sendMessage("§a✔ Connexion réussie !");
        p.sendMessage("§7Bon jeu sur §eMoodCraft ✨");
    }

    public static void logout(Player p) {
        logged.remove(p);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        logout(p);

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        p.sendMessage("§6Bienvenue sur §eMoodCraft ✨");

        if (AuthManager.isRegistered(p.getUniqueId().toString())) {
            p.sendMessage("§eTon compte existe déjà.");
            p.sendMessage("§7➡ §fConnecte-toi via le menu.");
        } else {
            p.sendMessage("§eAucun compte trouvé.");
            p.sendMessage("§7➡ §fCrée ton compte via le menu.");
        }

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            LoginGUI.open(p);
        }, 20L);
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

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {

        Player p = e.getPlayer();

        if (isLogged(p)) return;

        String msg = e.getMessage().toLowerCase();

        if (!msg.startsWith("/login") && !msg.startsWith("/register")) {

            e.setCancelled(true);

            p.sendMessage("§c⚠ Tu dois te connecter avant de jouer.");
            p.sendMessage("§7Utilise le menu ou : §e/login <motdepasse>");
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {

        Player p = (Player) e.getPlayer();

        if (isLogged(p)) return;

        if (e.getView().getTitle().equals("§6Authentification")) {
            Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
                LoginGUI.open(p);
            }, 5L);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        logout(e.getPlayer());
    }
}