package fr.moodcraft.auth;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class LoginGUI {

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, "§6Authentification");

        boolean registered = AuthManager.isRegistered(p.getUniqueId().toString());

        if (registered) {
            inv.setItem(13, ItemBuilder.create(Material.LIME_CONCRETE,
                    "§aSe connecter",
                    "§7Clique puis entre ton mot de passe"
            ));
        } else {
            inv.setItem(13, ItemBuilder.create(Material.YELLOW_CONCRETE,
                    "§eCréer un compte",
                    "§7Clique puis choisis un mot de passe"
            ));
        }

        p.openInventory(inv);
    }
}