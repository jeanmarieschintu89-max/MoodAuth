package fr.moodcraft.auth;

import org.bukkit.event.*;
import org.bukkit.event.player.*;
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
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        logged.remove(p);

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        p.sendMessage("§6Bienvenue sur MoodCraft ✨");

        if (AuthManager.isRegistered(p.getUniqueId().toString())) {

            p.sendMessage("§eTon compte existe déjà.");
            p.sendMessage("§7➡ §fConnecte-toi avec : §e/login <motdepasse>");

        } else {

            p.sendMessage("§eAucun compte trouvé.");
            p.sendMessage("§7➡ §fCrée-en un avec : §e/register <motdepasse>");
        }

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!isLogged(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {

        if (isLogged(e.getPlayer())) return;

        String msg = e.getMessage().toLowerCase();

        if (!msg.startsWith("/login") && !msg.startsWith("/register")) {

            e.setCancelled(true);

            e.getPlayer().sendMessage("§c⚠ Tu dois te connecter avant de jouer.");
            e.getPlayer().sendMessage("§7Utilise : §e/login <motdepasse>");
        }
    }
}