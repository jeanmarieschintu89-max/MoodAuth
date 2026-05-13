package fr.moodcraft.auth;

import org.bukkit.Bukkit;

import org.bukkit.command.PluginCommand;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {

        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        AuthManager.init(this);

        registerCommands();
        registerListeners();

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(
                "§8----- §6✦ §aMood§6Auth §6✦ §8-----"
        );
        Bukkit.getConsoleSender().sendMessage(
                "§a✔ §fSécurité chargée."
        );
        Bukkit.getConsoleSender().sendMessage(
                "§7Service officiel de §aMood§6Craft§7."
        );
        Bukkit.getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(
                "§8----- §6✦ §aMood§6Auth §6✦ §8-----"
        );
        Bukkit.getConsoleSender().sendMessage(
                "§c✘ §fSécurité arrêtée."
        );
        Bukkit.getConsoleSender().sendMessage("");
    }

    private void registerCommands() {

        PluginCommand login =
                getCommand("login");

        if (login != null) {

            login.setExecutor(
                    new LoginCommand()
            );
        }

        PluginCommand register =
                getCommand("register");

        if (register != null) {

            register.setExecutor(
                    new RegisterCommand()
            );
        }

        PluginCommand change =
                getCommand("changepassword");

        if (change != null) {

            change.setExecutor(
                    new ChangePasswordCommand()
            );
        }

        PluginCommand reset =
                getCommand("resetcompte");

        if (reset != null) {

            reset.setExecutor(
                    new ResetAccountCommand()
            );
        }
    }

    private void registerListeners() {

        Bukkit.getPluginManager().registerEvents(
                new AuthListener(),
                this
        );
    }
}