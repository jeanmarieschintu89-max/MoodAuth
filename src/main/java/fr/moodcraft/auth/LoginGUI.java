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

            inv.setItem(13, ItemBuilder.create(
                    Material.LIME_CONCRETE,
                    "§aSe connecter",
                    "§7Ton compte est déjà enregistré",
                    "",
                    "§e▶ Clique puis tape",
                    "§f/login <motdepasse>"
            ));

        } else {

            inv.setItem(13, ItemBuilder.create(
                    Material.YELLOW_CONCRETE,
                    "§eCréer un compte",
                    "§7Bienvenue nouveau joueur",
                    "",
                    "§e▶ Clique puis tape",
                    "§f/register <motdepasse>"
            ));
        }

        p.openInventory(inv);
    }
}