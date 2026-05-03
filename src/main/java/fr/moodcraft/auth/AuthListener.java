
package fr.moodcraft.auth;

import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class AuthListener implements Listener {

    private static final Set<Player> logged = new HashSet<>();
    private static final Set<Player> waitingLogin = new HashSet<>();
    private static final Set<Player> waitingRegister = new HashSet<>();

    public static boolean isLogged(Player p) {
        return logged.contains(p);
    }

    public static void login(Player p) {
        logged.add(p);

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        p.sendMessage("§a✔ Connexion réussie !");
        p.sendMessage("§7Bon jeu sur §eMoodCraft ✨");
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

            p.sendMessage("§eTon compte existe déjà.");
            p.sendMessage("§7➡ §fConnecte-toi avec : §e/login <motdepasse>");

        } else {

            waitingRegister.add(p);

            p.sendMessage("§eAucun compte trouvé.");
            p.sendMessage("§7➡ §fTape directement ton mot de passe pour créer ton compte");
        }

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
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

        if (msg.startsWith("/login")) {

            String[] args = msg.split(" ");

            if (args.length < 2) {
                p.sendMessage("§cUsage: /login <motdepasse>");
                return;
            }

            waitingLogin.add(p);
            p.chat(args[1]);
            e.setCancelled(true);
            return;
        }

        if (msg.startsWith("/register")) {

            String[] args = msg.split(" ");

            if (args.length < 2) {
                p.sendMessage("§cUsage: /register <motdepasse>");
                return;
            }

            waitingRegister.add(p);
            p.chat(args[1]);
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
        p.sendMessage("§cTu dois te connecter.");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();

        if (isLogged(p)) return;

        String msg = e.getMessage();
        e.setCancelled(true);

        String ip = p.getAddress().getAddress().getHostAddress();

        // LOGIN
        if (AuthManager.isRegistered(p.getUniqueId().toString())) {

            if (waitingLogin.isEmpty()) {
                p.sendMessage("§e➡ Tape /login <motdepasse>");
                return;
            }

            if (AuthManager.login(p.getUniqueId().toString(), p.getName(), msg, ip)) {

                waitingLogin.remove(p);
                login(p);

            } else {
                p.sendMessage("§cMot de passe incorrect.");
            }

            return;
        }

        // REGISTER
        AuthManager.register(p.getUniqueId().toString(), p.getName(), msg, ip);
        login(p);

        p.sendMessage("§aCompte créé !");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        logout(e.getPlayer());
    }
}