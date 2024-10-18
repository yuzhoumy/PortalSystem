package quest.yuzhou.portalsystem.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import quest.yuzhou.portalsystem.Modal.Portal;
/*import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;*/
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;
import static quest.yuzhou.portalsystem.PortalSystem.getPlugin;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.getPortals;

public class Utilities {

    public static boolean isValidCoordinate(Location location) {

        int x = location.getBlockX();
        int z = location.getBlockZ();

        FileConfiguration RCPasteGradleConfig = Bukkit.getPluginManager().getPlugin("RCPasteGrandle").getConfig();
        ConfigurationSection pasteSection = RCPasteGradleConfig.getConfigurationSection("paste");
        for (String key : pasteSection.getKeys(false)) {
            List<String> locs = pasteSection.getStringList(key + ".locs");
            int sizeX = Integer.parseInt(pasteSection.getString(key + ".size").split(",")[0]);
            int sizeZ = Integer.parseInt(pasteSection.getString(key + ".size").split(",")[1]);
            for (String loc : locs) {
                int locX = Integer.parseInt(loc.split(",")[0]);
                int locZ = Integer.parseInt(loc.split(",")[2]);

                if (
                        x >= locX && x < locX + sizeX &&
                        z >= locZ && z < locZ + sizeZ
                ) return false;
            }
        }
        return true;
    }

    public static boolean isChunkGetUsed(Location location) {

        ArrayList<Portal> portals = getPortals();

        if (portals == null) {
            getPlugin().getLogger().info("[PortalSystem] Portals Object is Null arggggggggggggggggggggggggghhhhhhhhhhhh");
            return true;
        }

        Chunk chunk1 = location.getChunk();

        for (Portal portal : portals) {

            Chunk chunk2 = portal.getLocation().getChunk();

            if (chunk1.getX() == chunk2.getX() && chunk1.getZ() == chunk2.getZ()) {
                return true;
            }
        }
        return false;
    }

    public static long upgradePortalBlockPrice(int blockLevel) {
        double basePrice = getPlugin().getConfig().getDouble("portalBlockUpgradeBasePrice");
        double percentage = getPlugin().getConfig().getDouble("portalBlockUpgradePercentage");
        double price = basePrice;
        for (int i = 1; i <= blockLevel; i++) {
            price *= (1 + percentage);
        }
        return round(price);
    }

    public static boolean isPlayerInRadius(Player player, Location centerLocation, double radius) {
        Location playerLocation = player.getLocation();
        double distance = playerLocation.distance(centerLocation);
        return distance <= radius;
    }

    public static int[] getNorthWestCornerInChunk(Location location) {
        // Calculate the smallest x coordinate in the chunk
        int[] xAndZ = null;

        xAndZ[0] = location.getBlockX() & ~15; // Equivalent to location.getBlockX() - (location.getBlockX() % 16)

        // Calculate the smallest z coordinate in the chunk
        xAndZ[1] = location.getBlockZ() & ~15; // Equivalent to location.getBlockZ() - (location.getBlockZ() % 16)

        return xAndZ;

    }

    /* public static void Paste(Location location) {
        File file = new File(getPlugin().getDataFolder() + "/virtualworldchunk.schem");
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);
        Clipboard clipboard;

        World worldBukkit = Bukkit.getServer().getWorld(worldString);
        if (worldBukkit == null) throw new NullPointerException("Fail to paste schematic due to world is null");

        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(worldBukkit);

        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build()) {
            int X = Integer.parseInt(loc.split(",")[0]);
            int Y = Integer.parseInt(loc.split(",")[1]);
            int Z = Integer.parseInt(loc.split(",")[2]);

            BlockVector3 blockVector3 = BlockVector3.at(X, Y, Z); // Adjust Y coordinate for each paste

            try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(file))) {
                clipboard = clipboardReader.read();

                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(blockVector3)
                        .ignoreAirBlocks(false)
                        .build();

                Operations.complete(operation);
            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
