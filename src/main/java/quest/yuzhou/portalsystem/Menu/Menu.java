package quest.yuzhou.portalsystem.Menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import quest.yuzhou.portalsystem.Modal.Portal;
import quest.yuzhou.portalsystem.Utilities.Utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static quest.yuzhou.portalsystem.PortalSystem.getPlugin;
import static quest.yuzhou.portalsystem.Utilities.BlockTypeGetterAndConfigLoader.getPortalBlockType;

public class Menu {

    public static void openMainMenu(Player player, Portal portal) {

        Inventory inventory = Bukkit.createInventory(player, 27, ChatColor.BLUE + "傳送門管理界面界面-" + portal.getName());
        ItemStack upgradeBlock;
        ItemMeta upgradeBlock_meta;

        if (portal.getBlockLevel() < 20) {
            Material upgradeBlockMaterial = getPortalBlockType(portal.getBlockLevel() + 1);

            upgradeBlock = new ItemStack(upgradeBlockMaterial);
            upgradeBlock_meta = upgradeBlock.getItemMeta();
            upgradeBlock_meta.setDisplayName(ChatColor.YELLOW + "升級傳送門守護石");
        } else {
            upgradeBlock = new ItemStack(Material.BEDROCK);
            upgradeBlock_meta = upgradeBlock.getItemMeta();
            upgradeBlock_meta.setDisplayName(ChatColor.RED + "您的傳送門守護石已經是最頂級了！");
        }

        List<String> upgradeBlock_lore = new ArrayList<>();
        upgradeBlock_lore.add("目前等級：" + ChatColor.AQUA + portal.getBlockLevel());
        upgradeBlock_lore.add("總共需花費：" + ChatColor.AQUA + "$" + Utilities.upgradePortalBlockPrice(portal.getBlockLevel()));
        upgradeBlock_meta.setLore(upgradeBlock_lore);
        upgradeBlock.setItemMeta(upgradeBlock_meta);

        inventory.setItem(19, upgradeBlock);

        ItemStack repairBlock = new ItemStack(Material.STONE_AXE);
        ItemMeta repairBlock_meta = repairBlock.getItemMeta();
        repairBlock_meta.setDisplayName(ChatColor.YELLOW + "修復守護石");
        List<String> repairBlock_lore = new ArrayList<>();
        repairBlock_lore.add("目前血量：" + ChatColor.AQUA + portal.getBlockHp());
        repairBlock_lore.add("修復所需(每100血量)：" + ChatColor.AQUA + "$" + getPlugin().getConfig().getInt("portalBlockRepairPricePer100"));
        repairBlock_lore.add(ChatColor.AQUA + "您必須站在傳送門7格半徑之内才可以使用！");
        repairBlock_meta.setLore(repairBlock_lore);
        repairBlock.setItemMeta(repairBlock_meta);

        inventory.setItem(22, repairBlock);

        ItemStack toggle;
        ItemMeta toggle_meta;

        if (portal.isAttackNotification()) {
            toggle = new ItemStack(Material.GREEN_DYE);
            toggle_meta = toggle.getItemMeta();
            toggle_meta.setDisplayName(ChatColor.GREEN + "敵人攻擊傳送門守護石警報已開啓");
        } else {
            toggle = new ItemStack(Material.RED_DYE);
            toggle_meta = toggle.getItemMeta();
            toggle_meta.setDisplayName(ChatColor.RED + "敵人攻擊傳送門守護石警報已關閉");
        }

        toggle.setItemMeta(toggle_meta);

        inventory.setItem(25, toggle);

        ItemStack status;
        ItemMeta status_meta;
        int portalDangerHp = getPlugin().getConfig().getInt("portalDangerHp");

        if (portal.getBlockHp() > portalDangerHp) {
            status = new ItemStack(Material.LIME_CONCRETE_POWDER);
            status_meta = status.getItemMeta();
            status_meta.setDisplayName(ChatColor.GREEN + "您的傳送門目前安全");
            status.setItemMeta(status_meta);
        } else if (portal.getBlockHp() > 0) {
            status = new ItemStack(Material.ORANGE_CONCRETE_POWDER);
            status_meta = status.getItemMeta();
            status_meta.setDisplayName(ChatColor.YELLOW + "傳送門守護石血量低於" + portalDangerHp + "，請及時修復！");
            List<String> status_lore = new ArrayList<>();
            status_lore.add(ChatColor.GRAY + "請及時點擊上方石斧修復");
            status_lore.add(ChatColor.DARK_RED + "若敵人攻破守護石，");
            status_lore.add(ChatColor.DARK_RED + "敵人將可以進入並掠奪您的基地。");
            status_meta.setLore(status_lore);
            status.setItemMeta(status_meta);
        } else {
            status = new ItemStack(Material.RED_CONCRETE_POWDER);
            status_meta = status.getItemMeta();

            String pattern = "yyyy年MM月dd日 HH時mm分ss秒";
            DateFormat dateFormat = new SimpleDateFormat(pattern);
            String dateFormatted = dateFormat.format(portal.getScheduledClearTime());

            status_meta.setDisplayName(ChatColor.RED + "傳送門守護石已被攻破！");
            List<String> status_lore = new ArrayList<>();
            status_lore.add(ChatColor.GRAY + "請及時點擊上方石斧修復");
            status_lore.add(ChatColor.DARK_RED + "若不在" + ChatColor.RED + dateFormatted + ChatColor.DARK_RED + "之前修復，");
            status_lore.add(ChatColor.DARK_RED + "傳送門以及基地將會被系統清除");
            status_meta.setLore(status_lore);
            status.setItemMeta(status_meta);
        }

        inventory.setItem(4, status);

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1);
    }

    public static void openUpgradeBlockConfirmMenu(Player player, Portal portal) {

        final long portalPrice = Utilities.upgradePortalBlockPrice(portal.getBlockLevel());
        Inventory inventory = Bukkit.createInventory(player, 27, ChatColor.GOLD + "您確定要升級傳送門守護石？-" + portal.getName());

        ItemStack yes = new ItemStack(Material.GREEN_WOOL);
        ItemStack no = new ItemStack(Material.RED_WOOL);

        ItemMeta yes_meta = yes.getItemMeta();
        ItemMeta no_meta = no.getItemMeta();

        yes_meta.setDisplayName(ChatColor.GREEN + "花費 $" + portalPrice + "升級");
        no_meta.setDisplayName(ChatColor.RED + "不是");

        yes.setItemMeta(yes_meta);
        no.setItemMeta(no_meta);

        inventory.setItem(12, yes);
        inventory.setItem(14, no);

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10, 2);
    }

}
