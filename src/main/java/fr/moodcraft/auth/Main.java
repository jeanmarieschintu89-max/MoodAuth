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

        getCommand("login").setExecutor(new LoginCommand());
        getCommand("register").setExecutor(new RegisterCommand());
        getCommand("changepassword").setExecutor(new ChangePasswordCommand());

        getServer().getPluginManager().registerEvents(new AuthListener(), this);

        getLogger().info("MoodAuth activé");
    }
}