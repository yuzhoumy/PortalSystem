package quest.yuzhou.portalsystem.Commands.SubCommands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.portalsystem.Commands.SubCommand;
import quest.yuzhou.portalsystem.Modal.Portal;

import java.util.ArrayList;

import static quest.yuzhou.portalsystem.PortalSystem.getPlugin;
import static quest.yuzhou.portalsystem.PortalSystem.prefix;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.*;

public class addadminCommand extends SubCommand {

    @Override
    public String getName() {
        return "addadmin";
    }

    @Override
    public String getDescription() {
        return "使玩家獲得管理員權限（很危險！玩家擁有所有傳送門權限，如刪除傳送門，移除管理員等等）";
    }

    @Override
    public String getSyntax() {
        return "/porman addadmin <傳送門名字> <玩家名字>";
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(prefix + " 只有玩家可以執行這個指令");
            return;
        }

        if (args.length != 3) {
            commandSender.sendMessage(prefix + " 正確用法：" + getSyntax());
            return;
        }

        Player p = (Player) commandSender;
        OfflinePlayer target = getPlugin().getServer().getOfflinePlayer(args[2]);
        Portal portal = findPortalByName(args[1]);
        ArrayList<Portal> portals = findPortalsByAdmin(p.getUniqueId());

        if (portal == null || (!portals.contains(portal) && !p.isOp())) {
            p.sendMessage(prefix + " 你不是該傳送門的管理员，或者該傳送門不存在。");
            return;
        }

        if (!portal.isActive()) {
            p.sendMessage(prefix + " 這個傳送門已被刪除");
        }

        if (portal.getAllAdmin().contains(target.getUniqueId().toString())) {
            p.sendMessage(prefix + " 這個玩家本就是一位管理员。");
            return;
        }

        if (!target.hasPlayedBefore()) {
            p.sendMessage(prefix + " 該玩家從來沒有加入過伺服器。");
            return;
        }

        if (!portal.getAllMembers().contains(target.getUniqueId().toString())) {
            portal.addMember(target.getUniqueId());
        }
        portal.addAdmin(target.getUniqueId());
        updatePortal(portal);
        p.sendMessage(prefix + " 成功添加 " + ChatColor.GREEN + target.getName() + ChatColor.WHITE + " 至管理員當中。");

    }

}
