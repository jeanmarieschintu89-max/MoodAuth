@EventHandler
public void onClick(InventoryClickEvent e) {

    if (!(e.getWhoClicked() instanceof Player p)) return;

    if (!e.getView().getTitle().equals("§6Authentification")) return;

    e.setCancelled(true);

    p.closeInventory();

    if (AuthManager.isRegistered(p.getUniqueId().toString())) {

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        p.sendMessage("§eConnexion requise");
        p.sendMessage("§7➡ §f/login <motdepasse>");
        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

    } else {

        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        p.sendMessage("§eCréation de compte");
        p.sendMessage("§7➡ §f/register <motdepasse>");
        p.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
}