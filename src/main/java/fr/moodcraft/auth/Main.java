package fr.moodcraft.auth;

import fr.moodcraft.auth.command.ResetAccountCommand;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main get() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        //
        // 🔐 INIT AUTH
        //

        AuthManager.init();

        //
        // 📜 COMMANDES
        //

        getCommand("login")
                .setExecutor(new LoginCommand());

        getCommand("register")
                .setExecutor(new RegisterCommand());

        getCommand("changepassword")
                .setExecutor(new ChangePasswordCommand());

        //
        // 🔥 RESET COMPTE
        //

        getCommand("resetcompte")
                .setExecutor(new ResetAccountCommand());

        //
        // 🎧 LISTENERS
        //

        getServer()
                .getPluginManager()
                .registerEvents(
                        new AuthListener(),
                        this
                );

        //
        // 🚀 LOG
        //

        getLogger().info("=================================");
        getLogger().info("✅ MoodAuth chargé");
        getLogger().info("🔐 Auth système: OK");
        getLogger().info("🛡 Protection session: OK");
        getLogger().info("⚡ Reset compte: OK");
        getLogger().info("=================================");
    }
}