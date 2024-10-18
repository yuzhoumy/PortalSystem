package quest.yuzhou.portalsystem.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import quest.yuzhou.portalsystem.Modal.Portal;

import static org.bukkit.Material.getMaterial;
import static quest.yuzhou.portalsystem.PortalSystem.*;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.getPortals;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();

        try {
            if (block == null) {
                return;
            }

            Material portalMaterial = getMaterial(getPlugin().getConfig().getString("portalMaterial"));
            if (portalMaterial == null) {
                player.sendMessage(prefix + ChatColor.RED + " 配置文件中的傳送門材料無效。");
                return;
            }

            if (e.getAction() != Action.RIGHT_CLICK_BLOCK || block.getType() != portalMaterial) {
                return;
            }

            for (Portal portal : getPortals()) {
                if (
                        portal.getLocation().getBlockX() == block.getLocation().getBlockX() &&
                        portal.getLocation().getBlockZ() == block.getLocation().getBlockZ()
                ) {
                    e.setCancelled(true);

                    if (portal.getBlockHp() != 0) {
                        if (!portal.getAllMembers().contains(player.getUniqueId().toString())) {
                            player.sendMessage(prefix + " 傳送門名稱：" + ChatColor.YELLOW + portal.getName());
                            player.sendMessage(prefix + " 守護石血量：" + ChatColor.BLUE + portal.getBlockHp());
                            player.sendMessage(prefix + " 守護石等級：" + ChatColor.BLUE + portal.getBlockLevel());
                            player.sendMessage(prefix + " 成員總人數：" + ChatColor.BLUE + portal.getAllMembers().size());
                            return;
                        }

                    }

                    World targetWorld = (block.getWorld() == mainWorld) ? virtualWorld : mainWorld;
                    int x = block.getX();
                    int y = (block.getWorld() == mainWorld) ? block.getY() : getPlugin().getConfig().getInt("virtualPortalYCoordinate");
                    int z = block.getZ();

                    player.teleport(new Location(targetWorld, x, y, z));
                    player.sendMessage(prefix + ChatColor.YELLOW + " 傳送成功！");
                    return;
                }
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }
    }
}

