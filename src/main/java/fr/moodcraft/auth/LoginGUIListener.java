package fr.moodcraft.auth;

import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;

public class LoginGUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!e.getView().getTitle().equals("§6Authentification")) return;

        e.setCancelled(true);
        p.closeInventory();

        if (AuthManager.isRegistered(p.getUniqueId().toString())) {

            AuthListener.waitingLogin.add(p);
            p.sendMessage("§e🔐 Entre ton mot de passe dans le chat");

        } else {

            AuthListener.waitingRegister.add(p);
            p.sendMessage("§e🆕 Choisis ton mot de passe dans le chat");
        }
    }
}