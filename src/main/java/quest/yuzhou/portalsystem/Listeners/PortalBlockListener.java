package quest.yuzhou.portalsystem.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import quest.yuzhou.portalsystem.Modal.Portal;

import java.util.List;

import static quest.yuzhou.portalsystem.PortalSystem.*;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.getPortals;

public class PortalBlockListener implements Listener {

    List<String> portalBlockTypeList = getPlugin().getConfig().getStringList("portalBlockType");

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getWorld() != mainWorld) return;

        if (!portalBlockTypeList.contains(e.getBlock().getType().toString())) return;

        for (Portal portal : getPortals()) {
            Block b = e.getBlock();
            int x = portal.getX();
            int y = portal.getY();
            int z = portal.getZ();

            if (b.getX() == x && b.getY() == y + 1 && b.getZ() == z) {
                if (portal.getBlockHp() > 0) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(prefix + " 此傳送門守護石的血量還剩 " + ChatColor.YELLOW + portal.getBlockHp());
                    portal.damageBlock(1);
                    portal.attackNotification(e.getPlayer());
                    if (portal.getBlockHp() <= 0) {
                        e.getPlayer().sendMessage(prefix + ChatColor.AQUA + " 此傳送門已被攻破，現在所有人都可進入掠奪！");;
                    }
                }
            }
        }
    }

}
