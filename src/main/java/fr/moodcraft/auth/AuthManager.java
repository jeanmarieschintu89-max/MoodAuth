package fr.moodcraft.auth;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

public class AuthManager {

    private static File file;
    private static YamlConfiguration config;

    public static void init() {

        file = new File(Main.get().getDataFolder(), "users.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public static boolean isRegistered(String uuid) {
        return config.contains(uuid);
    }

    public static void register(String uuid, String password) {
        config.set(uuid, hash(password));
        save();
    }

    public static boolean login(String uuid, String password) {
        return config.getString(uuid).equals(hash(password));
    }

    public static boolean changePassword(String uuid, String oldPass, String newPass) {

        if (!login(uuid, oldPass)) return false;

        config.set(uuid, hash(newPass));
        save();

        return true;
    }

    private static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}