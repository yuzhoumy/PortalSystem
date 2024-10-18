package quest.yuzhou.portalsystem.Listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import quest.yuzhou.portalsystem.Modal.Portal;

import java.util.ArrayList;

import static org.bukkit.Material.getMaterial;
import static quest.yuzhou.portalsystem.Menu.Menu.openMainMenu;
import static quest.yuzhou.portalsystem.PortalSystem.*;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.findPortalsByAdmin;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.getPortals;

public class PortalMainBlockProtecter implements Listener {

    Material portalMaterial = getMaterial(getPlugin().getConfig().getString("portalMaterial"));

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        if (block.getType() != portalMaterial) return;

        Player p = e.getPlayer();

        if (block.getWorld() == mainWorld || block.getWorld() == virtualWorld) {

            ArrayList<Portal> portals = findPortalsByAdmin(p.getUniqueId());

            for (Portal portal : getPortals()) {
                int x = portal.getLocation().getBlockX();
                int z = portal.getLocation().getBlockZ();
                if (x == block.getX() && z == block.getZ()) {

                    if (portals.contains(portal)) {
                        openMainMenu(p, portal);
                    } else {
                        p.sendMessage(prefix + " 您不能破壞傳送門方塊。");
                    }
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

}
