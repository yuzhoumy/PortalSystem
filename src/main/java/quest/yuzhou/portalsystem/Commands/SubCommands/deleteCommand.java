package quest.yuzhou.portalsystem.Commands.SubCommands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.portalsystem.Commands.SubCommand;
import quest.yuzhou.portalsystem.Modal.Portal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static quest.yuzhou.portalsystem.PortalSystem.*;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.*;

public class deleteCommand extends SubCommand {

    HashMap<Player, String> readyToConfirm = new HashMap<>();
    int portalRefund = getPlugin().getConfig().getInt("portalRefund");

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "移除傳送門。獲得 " + portalRefund + "$ 補償。";
    }

    @Override
    public String getSyntax() {
        return "/porman delete <基地名稱>";
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(prefix + " 只有玩家可以使用此指令。");
            return;
        }

        if (args.length != 2) {
            commandSender.sendMessage(prefix + " 正確用法：" + getSyntax());
            return;
        }

        Player p = (Player) commandSender;
        ArrayList<Portal> portals = findPortalsByAdmin(p.getUniqueId());

        if (args[1].equalsIgnoreCase("confirm")) {

            if (readyToConfirm.containsKey(p)) {
                Economy economy = getEconomy();

                Portal portal = findPortalByName(readyToConfirm.get(p));
                portal.delete();
                removePortal(portal);
                economy.depositPlayer(p, portalRefund);

                p.sendMessage(prefix + " " + readyToConfirm.get(p) + " 清除成功！");
                p.sendMessage(prefix + " 您已獲得 $" + portalRefund + " 。");
                readyToConfirm.remove(p);

                try {
                    savePortals();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {

            Portal portal = findPortalByName(args[1]);

            if (portal == null) {
                p.sendMessage(prefix + " 該傳送門不存在。");
                return;
            }

            if (!portal.isActive()) {
                p.sendMessage(prefix + " 這個傳送門已被刪除");
            }

            if (!portals.contains(portal) && !p.isOp()) {
                p.sendMessage(prefix + " 您所提供的傳送門名字無效，或者您不是該基地的管理員。");
                return;
            }

            p.sendMessage(prefix + ChatColor.RED + " 你確定您要刪除傳送門&基地" + ChatColor.AQUA + args[1] + ChatColor.RED + " 嗎？");
            p.sendMessage(prefix + ChatColor.RED + " 您有30秒的時間決定。如果您確定要刪除，請輸入/porman delete confirm");
            if (!readyToConfirm.containsKey(p)) {
                readyToConfirm.put(p, args[1]);
                Bukkit.getScheduler().runTaskLater(getPlugin(), () -> readyToConfirm.remove(p), 600);
            }
        }
    }
}
