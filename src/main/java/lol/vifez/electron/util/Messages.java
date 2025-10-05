package lol.vifez.electron.util;

import lol.vifez.electron.Practice;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public final class Messages {
    private Messages() {}

    public static FileConfiguration cfg() {
        return Practice.getInstance().getMessagesFile().getConfiguration();
    }

    public static String raw(String path, String def) {
        String s = cfg().getString(path);
        return s != null ? s : def;
    }

    public static String raw(String path) {
        return raw(path, path);
    }

    public static String prefix() {
        return raw("general.prefix", "");
    }

    public static String format(String message, Map<String, String> placeholders) {
        if (message == null) return null;
        String out = message;
        if (placeholders != null) {
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                out = out.replace("{" + e.getKey() + "}", e.getValue());
            }
        }
        return CC.translate(prefix() + out);
    }

    public static void send(CommandSender to, String path) {
        String msg = format(raw(path), null);
        if (msg != null && !msg.isEmpty()) to.sendMessage(msg);
    }

    public static void send(CommandSender to, String path, Map<String, String> placeholders) {
        String msg = format(raw(path), placeholders);
        if (msg != null && !msg.isEmpty()) to.sendMessage(msg);
    }

    public static void send(Player to, String path) { send((CommandSender) to, path); }
    public static void send(Player to, String path, Map<String, String> placeholders) { send((CommandSender) to, path, placeholders); }
}
