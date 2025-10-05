package lol.vifez.electron.listener;

import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.enums.KitType;
import lol.vifez.electron.profile.Profile;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * @author axtodev
 * @project Electron
 * @website https://axto.eu
 */


public class FireballListener implements Listener {

    public FireballListener() {
        Practice.getInstance().getServer().getPluginManager().registerEvents(this, Practice.getInstance());
    }

    @EventHandler
    public void onUseFireball(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getName());
        if (profile == null || !profile.inMatch()) return;
        if (profile.getMatch().getKit().getKitType() != KitType.FIREBALL_FIGHT) return;

        ItemStack inHand = player.getItemInHand();
        if (inHand == null) return;
        if (inHand.getType() != Material.FIREBALL) return; // 1.8 material name

        event.setCancelled(true);

        // consume one fireball
        if (inHand.getAmount() > 1) {
            inHand.setAmount(inHand.getAmount() - 1);
        } else {
            player.setItemInHand(null);
        }

        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setIsIncendiary(false);
        fireball.setYield(2.5f);
        Vector direction = player.getLocation().getDirection().normalize().multiply(1.2);
        fireball.setVelocity(direction);
    }
}
