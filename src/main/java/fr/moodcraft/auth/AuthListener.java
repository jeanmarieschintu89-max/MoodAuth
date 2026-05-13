package fr.moodcraft.auth;

import fr.moodcraft.auth.util.AuthMessages;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthListener
        implements Listener {

    private static final Set<UUID> logged =
            new HashSet<>();

    public static boolean isLogged(
            Player player
    ) {

        return logged.contains(
                player.getUniqueId()
        );
    }

    public static void login(
            Player player
    ) {

        logged.add(
                player.getUniqueId()
        );

        player.removePotionEffect(
                PotionEffectType.BLINDNESS
        );

        player.removePotionEffect(
                PotionEffectType.SLOWNESS
        );

        AuthMessages.header(
                player,
                "Sécurité " + AuthMessages.brand()
        );

        player.sendMessage("§a✔ §fConnexion confirmée.");
        player.sendMessage("");
        player.sendMessage("§7Bienvenue sur §aMood§6Craft§7.");
        player.sendMessage("");
        AuthMessages.line(
                player,
                "Menu principal: §e/menu"
        );
        AuthMessages.line(
                player,
                "Menu ville: §e/menuville"
        );
        AuthMessages.line(
                player,
                "Votre compte protège votre progression"
        );

        AuthMessages.footer(player);

        player.playSound(
                player.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                0.8f,
                1.1f
        );
    }

    @EventHandler
    public void onJoin(
            PlayerJoinEvent e
    ) {

        Player p =
                e.getPlayer();

        logged.remove(
                p.getUniqueId()
        );

        p.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.BLINDNESS,
                        Integer.MAX_VALUE,
                        1,
                        false,
                        false,
                        false
                )
        );

        p.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.SLOWNESS,
                        Integer.MAX_VALUE,
                        4,
                        false,
                        false,
                        false
                )
        );

        Bukkit.getScheduler().runTaskLater(
                Main.getInstance(),
                () -> sendLoginHint(p),
                20L
        );
    }

    @EventHandler
    public void onQuit(
            PlayerQuitEvent e
    ) {

        logged.remove(
                e.getPlayer().getUniqueId()
        );
    }

    @EventHandler
    public void onMove(
            PlayerMoveEvent e
    ) {

        Player p =
                e.getPlayer();

        if (isLogged(p)) {
            return;
        }

        if (e.getFrom().getX() != e.getTo().getX()
                || e.getFrom().getZ() != e.getTo().getZ()) {

            e.setTo(e.getFrom());

            sendActionHint(p);
        }
    }

    @EventHandler
    public void onCommand(
            PlayerCommandPreprocessEvent e
    ) {

        Player p =
                e.getPlayer();

        if (isLogged(p)) {
            return;
        }

        String msg =
                e.getMessage()
                        .toLowerCase();

        if (msg.startsWith("/login")
                || msg.startsWith("/register")) {
            return;
        }

        e.setCancelled(true);

        sendActionHint(p);
    }

    @EventHandler
    public void onChat(
            AsyncPlayerChatEvent e
    ) {

        if (isLogged(e.getPlayer())) {
            return;
        }

        e.setCancelled(true);

        sendActionHint(e.getPlayer());
    }

    @EventHandler
    public void onBreak(
            BlockBreakEvent e
    ) {

        if (!isLogged(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(
            BlockPlaceEvent e
    ) {

        if (!isLogged(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(
            EntityDamageEvent e
    ) {

        if (e.getEntity() instanceof Player p
                && !isLogged(p)) {

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByEntity(
            EntityDamageByEntityEvent e
    ) {

        if (e.getDamager() instanceof Player p
                && !isLogged(p)) {

            e.setCancelled(true);
        }
    }

    private void sendLoginHint(
            Player p
    ) {

        if (isLogged(p)) {
            return;
        }

        AuthMessages.header(
                p,
                "Sécurité " + AuthMessages.brand()
        );

        if (AuthManager.isRegistered(
                p.getUniqueId().toString()
        )) {

            p.sendMessage("§fConnexion requise.");
            p.sendMessage("");
            p.sendMessage("§7Commande:");
            p.sendMessage("§e/login <motdepasse>");

        } else {

            p.sendMessage("§fCréation de compte requise.");
            p.sendMessage("");
            p.sendMessage("§7Commande:");
            p.sendMessage("§e/register <motdepasse>");
        }

        p.sendMessage("");
        AuthMessages.line(
                p,
                "Vous pourrez bouger après validation"
        );

        AuthMessages.footer(p);
    }

    private void sendActionHint(
            Player p
    ) {

        if (AuthManager.isRegistered(
                p.getUniqueId().toString()
        )) {

            p.sendActionBar(
                    "§6Sécurité §8• §fConnexion requise §8• §e/login <motdepasse>"
            );

        } else {

            p.sendActionBar(
                    "§6Sécurité §8• §fCompte requis §8• §e/register <motdepasse>"
            );
        }
    }
}