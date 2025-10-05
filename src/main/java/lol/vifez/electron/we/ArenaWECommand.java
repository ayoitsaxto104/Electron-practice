package lol.vifez.electron.we;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import lol.vifez.electron.Practice;
import lol.vifez.electron.arena.Arena;
import lol.vifez.electron.util.CC;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

@CommandAlias("arenawe|aworldedit|wearena")
@CommandPermission("electron.admin")
public class ArenaWECommand extends BaseCommand {

    @Default
    public void help(CommandSender sender) {
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&b&lArena WE Commands"));
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&7▪ &b/arenawe setmin &7<arena> &f- &fSet arena min from WE selection"));
        sender.sendMessage(CC.translate("&7▪ &b/arenawe setmax &7<arena> &f- &fSet arena max from WE selection"));
        sender.sendMessage(CC.translate(" "));
    }

    @Subcommand("setmin")
    public void setMin(Player sender, String arenaName) {
        Arena arena = Practice.getInstance().getArenaManager().getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }
        Location min = getWESelectionPoint(sender, true);
        if (min == null) {
            sender.sendMessage(CC.translate("&cMake a WorldEdit selection first!"));
            return;
        }
        arena.setPositionOne(min);
        sender.sendMessage(CC.translate("&aMinimum point set from WE selection!"));
    }

    @Subcommand("setmax")
    public void setMax(Player sender, String arenaName) {
        Arena arena = Practice.getInstance().getArenaManager().getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }
        Location max = getWESelectionPoint(sender, false);
        if (max == null) {
            sender.sendMessage(CC.translate("&cMake a WorldEdit selection first!"));
            return;
        }
        arena.setPositionTwo(max);
        sender.sendMessage(CC.translate("&aMaximum point set from WE selection!"));
    }

    private Location getWESelectionPoint(Player player, boolean min) {
        Plugin we = Practice.getInstance().getServer().getPluginManager().getPlugin("WorldEdit");
        if (we == null || !we.isEnabled()) return null;
        try {
            Class<?> wePluginClz = Class.forName("com.sk89q.worldedit.bukkit.WorldEditPlugin");
            if (!wePluginClz.isInstance(we)) return null;
            Method getSel = wePluginClz.getMethod("getSelection", Player.class);
            Object selection = getSel.invoke(we, player);
            if (selection == null) return null;
            Class<?> selClz = Class.forName("com.sk89q.worldedit.bukkit.selections.Selection");
            Method getWorld = selClz.getMethod("getWorld");
            Object bWorld = getWorld.invoke(selection);
            if (bWorld == null) return null;
            Method pointMethod = selClz.getMethod(min ? "getMinimumPoint" : "getMaximumPoint");
            Object vec = pointMethod.invoke(selection);
            double x = ((Number) vec.getClass().getMethod("getX").invoke(vec)).doubleValue();
            double y = ((Number) vec.getClass().getMethod("getY").invoke(vec)).doubleValue();
            double z = ((Number) vec.getClass().getMethod("getZ").invoke(vec)).doubleValue();
            return new Location((org.bukkit.World) bWorld, x, y, z, 0f, 0f);
        } catch (Throwable ignored) {
        }
        return null;
    }
}
