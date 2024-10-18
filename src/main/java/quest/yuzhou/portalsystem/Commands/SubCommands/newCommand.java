package quest.yuzhou.portalsystem.Commands.SubCommands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.portalsystem.Commands.SubCommand;
import quest.yuzhou.portalsystem.Modal.Portal;


import java.io.IOException;
import java.util.HashMap;

import static quest.yuzhou.portalsystem.PortalSystem.*;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.*;
import static quest.yuzhou.portalsystem.Utilities.Utilities.isChunkGetUsed;
import static quest.yuzhou.portalsystem.Utilities.Utilities.isValidCoordinate;

public class newCommand extends SubCommand {

    HashMap<Player, String> readyToConfirm = new HashMap<>();

    @Override
    public String getName() {
        return "new";
    }

    @Override
    public String getDescription() {
        return "創建一個新的傳送門以及對應的基地";
    }

    @Override
    public String getSyntax() {
        return "/porman new <name>";
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player) {

            Player p = (Player) commandSender;

            if (args.length == 2) {

                int portalPrice = getPlugin().getConfig().getInt("portalPrice");
                if (args[1].equalsIgnoreCase("confirm")) {

                    if (readyToConfirm.containsKey(p)) {

                        Economy economy = getEconomy();

                        if (!(economy.getBalance(p) > portalPrice)) {
                            p.sendMessage(prefix + " 您的餘額不足，至少需要 " + portalPrice + " $創建一個野外基地。");
                            return;
                        }

                        p.sendMessage(prefix + " 請稍等，正在爲您創建傳送門…");

                        Portal portal = new Portal(p.getLocation(), p.getUniqueId(), readyToConfirm.get(p));

                        try {
                            addPortal(portal);
                        } catch (IllegalArgumentException e) {
                            p.sendMessage(prefix + " 已經有一個相同名字的傳送門了");
                            return;
                        }

                        try {
                            savePortals();
                        } catch (IOException e) {
                            getPlugin().getLogger().info("[PortalSystem] 保存傳送門時出現嚴重錯誤。");
                            e.printStackTrace();
                            p.sendMessage(prefix + ChatColor.RED + "很抱歉，創建傳送門時出現嚴重錯誤。若重試后仍然不行，請*立即*通報管理員。");
                            removePortal(portal);
                            return;
                        }

                        economy.withdrawPlayer(p, portalPrice);
                        p.sendMessage(prefix + " 已從您的餘額扣除" + portalPrice + "$");
                        portal.create();

                        p.sendMessage(prefix + " 傳送門創建完成！");
                        p.sendMessage(prefix + " 您可以通過 /porman addmember " + portal.getName() + " <玩家> 邀請您的好友獲得進出權限");
                        p.sendMessage(prefix + " 您也可以通過 /porman addadmin " + portal.getName() + " <玩家> 使您的朋友成爲傳送門管理員");
                        p.sendMessage(prefix + " 您也可以輸入/porman menu " + portal.getName() + " 或者敲碎傳送門下半部的方塊，打開傳送門管理界面，查看傳送門狀態（是否被攻陷），以及修復傳送門守護石等等。");
                        p.sendMessage(prefix + ChatColor.RED + " 注意！" + ChatColor.YELLOW + "如果傳送門上方的方塊被敵人持續打爆，當傳送門守護石血量降到0的時候，所有人都進得去你的基地！請務必時常修復傳送門守護石！");
                        readyToConfirm.remove(p);
                    }
                } else {

                    if (args[1].equalsIgnoreCase("confirm")) {
                        p.sendMessage(prefix + " 您不能使用這個名字，請想一個更好的名字。");
                        return;
                    }

                    if (!isValidCoordinate(p.getLocation())) {
                        p.sendMessage(prefix + " 這個坐標與資源點衝突，請找另一個位置。");
                        return;
                    }

                    if (isChunkGetUsed(p.getLocation())) {
                        p.sendMessage(prefix + " 這個區塊已經被占用了。");
                        return;
                    }

                    p.sendMessage(prefix + ChatColor.RED + " 你確定您要創建傳送門&基地 " + ChatColor.AQUA + args[1] + ChatColor.RED + " 嗎？");
                    p.sendMessage(prefix + ChatColor.RED + " 您有30秒的時間決定。如果您確定要花費$" + portalPrice + "創建，請輸入/porman new confirm");
                    if (!readyToConfirm.containsKey(p)) {
                        readyToConfirm.put(p, args[1]);
                        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> readyToConfirm.remove(p), 600);
                    }

                }
            } else {
                commandSender.sendMessage(prefix + " 請輸入傳送門名稱。/porman newportal <傳送門名稱>");
            }
        } else {
            commandSender.sendMessage(prefix + " 只有玩家可以使用此指令。");
        }
    }
}
