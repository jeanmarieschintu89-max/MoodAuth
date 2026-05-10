package fr.moodcraft.auth;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main get() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        AuthManager.init();

        getCommand("login")
                .setExecutor(new LoginCommand());

        getCommand("register")
                .setExecutor(new RegisterCommand());

        getCommand("changepass")
                .setExecutor(new ChangePasswordCommand());

        getCommand("resetcompte")
                .setExecutor(new ResetAccountCommand());

        getServer()
                .getPluginManager()
                .registerEvents(
                        new AuthListener(),
                        this
                );

        getLogger().info("=================================");
        getLogger().info("✅ MoodAuth chargé");
        getLogger().info("🔐 Auth système: OK");
        getLogger().info("🛡 Protection session: OK");
        getLogger().info("⚡ Reset compte: OK");
        getLogger().info("=================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("🛑 MoodAuth arrêté.");
    }
}