package quest.yuzhou.portalsystem.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
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

                    Player player = e.getPlayer();

                    e.setCancelled(true);
                    if (!playerCanBreakBlock(player)) {
                        player.sendMessage(prefix + ChatColor.RED + " 你只可以用 精製十字鎬 來破壞傳送門守護石！");
                        return;
                    }

                    player.sendMessage(prefix + " 此傳送門守護石的血量還剩 " + ChatColor.YELLOW + portal.getBlockHp());
                    portal.damageBlock(1);
                    portal.attackNotification(player);
                    if (portal.getBlockHp() <= 0) {
                        player.sendMessage(prefix + ChatColor.AQUA + " 此傳送門已被攻破，現在所有人都可進入掠奪！");
                    }
                }
                break;
            }
        }
    }

    private boolean playerCanBreakBlock(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (!itemStack.hasItemMeta()) return true;
        if (!itemStack.getItemMeta().hasDisplayName()) return true;
        return itemStack.getItemMeta().getDisplayName().equalsIgnoreCase("§7§l精製十字鎬");
    }

}
