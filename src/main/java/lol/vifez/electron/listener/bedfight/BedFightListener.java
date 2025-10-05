package lol.vifez.electron.listener.bedfight;

import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.enums.KitType;
import lol.vifez.electron.profile.Profile;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author axtodev
 * @project Electron
 * @website https://axto.eu
 */

public class BedFightListener implements Listener {

    public BedFightListener() {
        Practice.getInstance().getServer().getPluginManager().registerEvents(this, Practice.getInstance());
    }

    @EventHandler
    public void onBedBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block == null || block.getType() != Material.BED_BLOCK) return;

        Player player = event.getPlayer();
        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getName());
        if (profile == null || !profile.inMatch()) return;
        if (profile.getMatch().getKit().getKitType() != KitType.BED_FIGHT) return;

        event.setCancelled(false);
        profile.getMatch().setWinner(profile);
        Practice.getInstance().getMatchManager().end(profile.getMatch());
    }
}
