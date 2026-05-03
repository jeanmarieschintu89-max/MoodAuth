package fr.moodcraft.auth;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class AuthListener implements Listener {

    private static final Set<Player> logged = new HashSet<>();
    public static final Set<Player> waitingLogin = new HashSet<>();
    public static final Set<Player> waitingRegister = new HashSet<>();

    public static boolean isLogged(Player p) {
        return logged.contains(p);
    }

    public static void login(Player p) {
        logged.add(p);

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        p.sendMessage("§a✔ Connexion réussie !");
        p.sendMessage("§7Bienvenue sur §eMoodCraft ✨");
        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    public static void logout(Player p) {
        logged.remove(p);
        waitingLogin.remove(p);
        waitingRegister.remove(p);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        logout(p);

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        p.sendMessage("§6Bienvenue sur §eMoodCraft ✨");

        if (AuthManager.isRegistered(p.getUniqueId().toString())) {
            p.sendMessage("§eCompte détecté.");
            p.sendMessage("§7Clique dans le menu pour te connecter.");
        } else {
            p.sendMessage("§eNouveau joueur !");
            p.sendMessage("§7Crée ton compte via le menu.");
        }

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

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
            p.sendMessage("§c⚠ Connecte-toi via le menu.");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();
        if (isLogged(p)) return;

        String msg = e.getMessage();
        e.setCancelled(true);

        // LOGIN
        if (waitingLogin.contains(p)) {

            String ip = p.getAddress().getAddress().getHostAddress();

            if (AuthManager.login(p.getUniqueId().toString(), p.getName(), msg, ip)) {

                waitingLogin.remove(p);
                login(p);

            } else {
                p.sendMessage("§cMot de passe incorrect.");
            }

            return;
        }

        // REGISTER
        if (waitingRegister.contains(p)) {

            String ip = p.getAddress().getAddress().getHostAddress();

            AuthManager.register(p.getUniqueId().toString(), p.getName(), msg, ip);

            waitingRegister.remove(p);
            login(p);

            p.sendMessage("§aCompte créé avec succès !");
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {

        Player p = (Player) e.getPlayer();

        if (isLogged(p)) return;

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            LoginGUI.open(p);
        }, 5L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        logout(e.getPlayer());
    }
}