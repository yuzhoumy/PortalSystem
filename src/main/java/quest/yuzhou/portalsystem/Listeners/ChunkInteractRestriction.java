package quest.yuzhou.portalsystem.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static quest.yuzhou.portalsystem.PortalSystem.prefix;
import static quest.yuzhou.portalsystem.PortalSystem.virtualWorld;

public class ChunkInteractRestriction implements Listener {

    private boolean isNotSameChunk(Location loc1, Location loc2) {
        Chunk chunk1 = loc1.getChunk();
        Chunk chunk2 = loc2.getChunk();
        return chunk1.getX() != chunk2.getX() || chunk1.getZ() != chunk2.getZ();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getBlock().getWorld() != virtualWorld) return;

        Location playerLocation = e.getPlayer().getLocation();
        Location blockLocation = e.getBlock().getLocation();

        if (isNotSameChunk(playerLocation, blockLocation)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(prefix + ChatColor.RED + " 您不可以在其他基地放置方塊。");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getWorld() != virtualWorld) return;

        Location playerLocation = e.getPlayer().getLocation();
        Location blockLocation = e.getBlock().getLocation();

        if (isNotSameChunk(playerLocation, blockLocation)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(prefix + ChatColor.RED + " 您不可以破壞其他基地的方塊。");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getClickedBlock().getWorld() != virtualWorld) return;

        Location playerLocation = e.getPlayer().getLocation();
        Location blockLocation = e.getClickedBlock().getLocation();

        if (isNotSameChunk(playerLocation, blockLocation)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(prefix + ChatColor.RED + " 您不可以與其他基地的方塊交互。");
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getWorld() != virtualWorld) return;

        Location playerLocation = e.getPlayer().getLocation();
        Location blockLocation = e.getRightClicked().getLocation();

        if (isNotSameChunk(playerLocation, blockLocation)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(prefix + ChatColor.RED + " 您不可以與其他基地的實體交互。");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

        if (e.getPlayer().isOp()) return;
        if (e.getFrom().getWorld() != virtualWorld) return;

        Location from = e.getFrom();
        Location to = e.getTo();

        Chunk fromChunk = from.getChunk();
        Chunk toChunk = to.getChunk();

        if (fromChunk.getX() != toChunk.getX() || fromChunk.getZ() != toChunk.getZ()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(prefix + " 你不能去到另一個基地/區塊");
        }

    }
}

