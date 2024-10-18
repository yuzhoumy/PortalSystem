package quest.yuzhou.portalsystem.Modal;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.bukkit.Material.getMaterial;
import static quest.yuzhou.portalsystem.PortalSystem.*;
import static quest.yuzhou.portalsystem.Utilities.BlockTypeGetterAndConfigLoader.getPortalBlockType;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.recordPortalAfterDelete;

public class Portal {

    private final ArrayList<String> membersUUID;
    private final ArrayList<String> adminsUUID;
    private final long dateCreated;
    private final int x, y, z;
    private final String name;
    private int blockLevel;
    private int blockHp;
    private boolean attackNotification;
    private boolean active;
    private long scheduledClearTime;
    private transient BukkitTask clearTask;

    public static final int maxBlockHp = getPlugin().getConfig().getInt("maxBlockHp");
    private final Material portalMaterial = getMaterial(getPlugin().getConfig().getString("portalMaterial"));

    public Portal(Location location, UUID owner, String name) {
        this.membersUUID = new ArrayList<>();
        this.membersUUID.add(owner.toString());
        this.adminsUUID = new ArrayList<>();
        this.adminsUUID.add(owner.toString());
        this.dateCreated = System.currentTimeMillis();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.name = name;
        this.blockLevel = 1;
        this.blockHp = maxBlockHp;
        this.attackNotification = true;
        this.active = true;
        this.scheduledClearTime = -1;
    }

    // Method to reconstruct the Location object
    public Location getLocation() {
        return new Location(mainWorld, x, y, z);
    }

    public void create() {
        Location location = new Location(mainWorld, x, y, z);
        int virtualPortalYCoordinate = getPlugin().getConfig().getInt("virtualPortalYCoordinate");

        location.getBlock().setType(portalMaterial);
        location.setWorld(virtualWorld);
        location.setY(virtualPortalYCoordinate);
        location.getBlock().setType(portalMaterial);
        putBlock();
    }

    public void delete() {
        Location location = new Location(mainWorld, x, y, z);
        int virtualPortalYCoordinate = getPlugin().getConfig().getInt("virtualPortalYCoordinate");

        location.getBlock().setType(Material.AIR);
        location.setWorld(virtualWorld);
        location.setY(virtualPortalYCoordinate);
        location.getBlock().setType(Material.AIR);

        destroyBlock();
    }

    public void putBlock() {
        Location location = new Location(mainWorld, x, y+1, z);
        Material material = getPortalBlockType(this.blockLevel);
        location.getBlock().setType(material);
    }

    public void destroyBlock() {
        Location location = new Location(mainWorld, x, y+1, z);
        location.getBlock().setType(Material.AIR);
    }

    public void upgradeBlock() throws IllegalStateException {
        if (this.blockLevel >= 20) {
            throw new IllegalStateException("The block-level of portal " + this.name + " has reached 20");
        }
        this.blockLevel += 1;
        putBlock();
    }

    public void damageBlock(int dmg) {
        this.blockHp -= dmg;
        if (this.blockHp <= 0) {
            this.blockHp = 0;
            destroyBlock();
            notifyAllMembers(ChatColor.RED + getName() + " 被攻破了！敵人可能現在正在掠奪您的基地！請立刻前往修復傳送門守護石。若守護石血量24小時候仍然為0，" + ChatColor.YELLOW + "您的傳送門以及基地將會被清除！", Sound.ITEM_GOAT_HORN_SOUND_4);
            scheduleClear();
        }
    }

    public void repairBlock(int amount) throws IllegalStateException {
        int oldHp = this.blockHp;
        if (this.blockHp + amount > maxBlockHp) {
            throw new IllegalStateException("The blockHp of" + this.name + " has reached " + maxBlockHp);
        }
        this.blockHp += amount;
        if (oldHp == 0 && this.blockHp > 0) {
            notifyAllMembers(ChatColor.YELLOW + getName() + " 血量已回升！現在敵人已無法再進入您的基地！基地内可能仍有殘餘敵人，建議您進入基地將他們趕盡殺絕。", Sound.BLOCK_NOTE_BLOCK_CHIME);
            cancelScheduledClear();
            putBlock();
        }
    }

    public void notifyAllMembers(String msg, @Nullable Sound sound) {
        for (String uuid : this.membersUUID) {
            Player player = Bukkit.getPlayer(UUID.fromString(uuid));
            if (player == null) continue;

            player.sendMessage(prefix + " " + msg);
            if (sound != null) {
                player.playSound(player.getLocation(), sound, 10, 1);
            }
        }
    }

    private void clear() {
        getPlugin().getLogger().info("傳送門以及基地 " + getName() + " 已移除。");
        notifyAllMembers(ChatColor.RED + "傳送門以及基地" + ChatColor.AQUA + getName() + ChatColor.RED + "已移除。", Sound.BLOCK_ANVIL_FALL);

        String pattern = "dd日MM月yyyy年 HH時mm分ss秒";
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        String theDateCreated = dateFormat.format(this.getDateCreated());

        Bukkit.broadcastMessage(prefix + ChatColor.YELLOW + " 塵歸塵……土歸土…… 從" + ChatColor.AQUA + theDateCreated + ChatColor.YELLOW + "開始興盛的" + ChatColor.GOLD + this.name + ChatColor.YELLOW + "，如今被刪除了。");
        delete();
        setActive(false);
        try {
            recordPortalAfterDelete(this);
        } catch (IOException e) {
            getPlugin().getLogger().info("Error while writing portal to deletedPortals.json");
            e.printStackTrace();
        }
    }

    private void scheduleClear() {
        this.scheduledClearTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000; // 24 hours from now
        this.clearTask = Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (this.blockHp == 0) {
                clear();
            } else {
                cancelScheduledClear();
            }
        }, 20 * 60 * 60 * 24); // Run after 24 hours
    }

    private void cancelScheduledClear() {
        if (this.clearTask != null) {
            this.clearTask.cancel();
            this.clearTask = null;
        }
        this.scheduledClearTime = -1;
    }

    public void rescheduleClearing() {
        if (this.scheduledClearTime > 0) {
            long delay = Math.max(0, this.scheduledClearTime - System.currentTimeMillis());
            this.clearTask = Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                if (this.blockHp == 0) {
                    clear();
                } else {
                    cancelScheduledClear();
                }
            }, delay / 50); // Convert milliseconds to ticks
        }
    }

    public void toggleAttackNotification() {
        attackNotification = !attackNotification;
    }

    public void attackNotification(Player attacker) {
        if (this.attackNotification) {
            notifyAllMembers(ChatColor.YELLOW + getName() + ChatColor.RED + " 的守護石正在被 " + ChatColor.GOLD + attacker.getName() + ChatColor.RED + " 攻擊！目前血量還剩 " + ChatColor.AQUA + this.blockHp, Sound.ITEM_GOAT_HORN_SOUND_2);
        }
    }

    public Date getDateCreated() {
        return new Date(dateCreated);
    }

    public void addMember(UUID uuid) {
        this.membersUUID.add(uuid.toString());
    }

    public void removeMember(UUID uuid) {
        this.membersUUID.remove(uuid.toString());
    }

    public ArrayList<String> getAllMembers() {
        return membersUUID;
    }

    public void addAdmin(UUID uuid) {
        this.adminsUUID.add(uuid.toString());
    }

    public void removeAdmin(UUID uuid) {
        this.adminsUUID.remove(uuid.toString());
    }

    public ArrayList<String> getAllAdmin() {
        return adminsUUID;
    }

    public String getName() {
        return name;
    }

    public int getBlockLevel() {
        return blockLevel;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getBlockHp() {
        return blockHp;
    }

    public boolean isAttackNotification() {
        return attackNotification;
    }

    public long getScheduledClearTime() {
        return scheduledClearTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean b) {
        this.active = b;
    }
}
