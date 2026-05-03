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

    public static void register(String uuid, String name, String password, String ip) {
        config.set(uuid + ".name", name);
        config.set(uuid + ".password", hash(password));
        config.set(uuid + ".ip", ip);
        save();
    }

    public static boolean login(String uuid, String name, String password, String ip) {

        String saved = config.getString(uuid + ".password");

        if (saved == null || !saved.equals(hash(password))) return false;

        config.set(uuid + ".name", name);
        config.set(uuid + ".ip", ip);

        save();
        return true;
    }

    public static boolean changePassword(String uuid, String oldPass, String newPass) {

        String saved = config.getString(uuid + ".password");

        if (saved == null || !saved.equals(hash(oldPass))) return false;

        config.set(uuid + ".password", hash(newPass));
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