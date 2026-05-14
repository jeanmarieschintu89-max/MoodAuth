package fr.moodcraft.auth;

import org.bukkit.Bukkit;

import org.bukkit.command.PluginCommand;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    public static Main get() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        AuthManager.init();

        registerCommands();
        registerListeners();

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§8----- §6✦ §aMood§6Auth §6✦ §8-----");
        Bukkit.getConsoleSender().sendMessage("§a✔ §fSécurité chargée.");
        Bukkit.getConsoleSender().sendMessage("§e➜ §7Service officiel de §aMood§6Craft§7.");
        Bukkit.getConsoleSender().sendMessage("§8-----------------------------");
    }

    @Override
    public void onDisable() {

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§8----- §6✦ §aMood§6Auth §6✦ §8-----");
        Bukkit.getConsoleSender().sendMessage("§c✖ §fSécurité arrêtée.");
        Bukkit.getConsoleSender().sendMessage("§8-----------------------------");
    }

    private void registerCommands() {
        registerCommand("login", new LoginCommand());
        registerCommand("register", new RegisterCommand());
        registerCommand("changepassword", new ChangePasswordCommand());
        registerCommand("resetcompte", new ResetAccountCommand());
        registerCommand("authadmin", new AuthAdminCommand());
    }

    private void registerCommand(
            String name,
            org.bukkit.command.CommandExecutor executor
    ) {

        PluginCommand command = getCommand(name);

        if (command != null) {
            command.setExecutor(executor);
        }
    }

    private void registerListeners() {

        Bukkit.getPluginManager().registerEvents(
                new AuthListener(),
                this
        );
    }
}
