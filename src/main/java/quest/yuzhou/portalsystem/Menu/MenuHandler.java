package quest.yuzhou.portalsystem.Menu;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import quest.yuzhou.portalsystem.Modal.Portal;
import quest.yuzhou.portalsystem.Utilities.Utilities;

import static quest.yuzhou.portalsystem.Menu.Menu.openMainMenu;
import static quest.yuzhou.portalsystem.Menu.Menu.openUpgradeBlockConfirmMenu;
import static quest.yuzhou.portalsystem.PortalSystem.*;
import static quest.yuzhou.portalsystem.Utilities.BlockTypeGetterAndConfigLoader.getPortalBlockType;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.findPortalByName;

public class MenuHandler implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().startsWith(ChatColor.BLUE + "傳送門管理界面界面")) {
            e.setCancelled(true);

            ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem == null) return;

            Player p = (Player) e.getWhoClicked();
            Portal portal = findPortalByName(e.getView().getTitle().split("-")[1]);

            Economy economy = getEconomy();

            if (portal.getBlockLevel() == 20) {
                if (clickedItem.getType() == Material.BEDROCK) {
                    return;
                }
            } else {
                Material portalBlockUpgradematerial = getPortalBlockType(portal.getBlockLevel() + 1);

                if (clickedItem.getType() == portalBlockUpgradematerial) {
                    handleUpgradeClick(p, portal, economy);
                }
            }

            if (clickedItem.getType() == Material.STONE_AXE) {
                handleRepairClick(p, portal, economy);
            } else if (clickedItem.getType() == Material.RED_DYE || clickedItem.getType() == Material.GREEN_DYE) {
                handleNotificationToggle(p, portal);
            }
        } else if (e.getView().getTitle().startsWith(ChatColor.GOLD + "您確定要升級傳送門守護石？-")) {
            handleUpgradeConfirmation(e);
        }
    }

    private void handleUpgradeClick(Player p, Portal portal, Economy economy) {
        final long price = Utilities.upgradePortalBlockPrice(portal.getBlockLevel());
        if (economy.getBalance(p) < price) {
            p.closeInventory();
            p.sendMessage(prefix + " 您的餘額不足。");
        } else {
            openUpgradeBlockConfirmMenu(p, portal);
        }
    }

    private void handleRepairClick(Player p, Portal portal, Economy economy) {
        final int price = getPlugin().getConfig().getInt("portalBlockRepairPricePer100");
        if (economy.getBalance(p) < price) {
            p.closeInventory();
            p.sendMessage(prefix + " 您的餘額不足。");
        } else {
            if (!Utilities.isPlayerInRadius(p, portal.getLocation(), 7)) {
                p.sendMessage(prefix + ChatColor.RED + " 您不在傳送門半徑7格之内！");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 2);
            } else if (portal.getBlockHp() + 100 > Portal.maxBlockHp) {
                p.sendMessage(prefix + " 再加下去，血量就要溢出來嚕。");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10, 1);
            } else {
                portal.repairBlock(100);
                economy.withdrawPlayer(p, price);
                p.sendMessage(prefix + ChatColor.YELLOW + " 恢复成功！");
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
                openMainMenu(p, portal);
            }
        }
    }

    private void handleNotificationToggle(Player p, Portal portal) {
        portal.toggleAttackNotification();
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 10, 2);
        openMainMenu(p, portal);
    }

    private void handleUpgradeConfirmation(InventoryClickEvent e) {
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        Portal portal = findPortalByName(e.getView().getTitle().split("-")[1]);
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem != null) {
            if (clickedItem.getType() == Material.GREEN_WOOL) {
                final long price = Utilities.upgradePortalBlockPrice(portal.getBlockLevel());
                Economy economy = getEconomy();
                economy.withdrawPlayer(p, price);
                p.sendMessage(prefix + " 已將 " + portal.getName() + " 的傳送門守護石升級至" + (portal.getBlockLevel() + 1) + "級！");
                p.closeInventory();
                portal.upgradeBlock();
            } else if (clickedItem.getType() == Material.RED_WOOL) {
                openMainMenu(p, portal);
            }
        }
    }
}