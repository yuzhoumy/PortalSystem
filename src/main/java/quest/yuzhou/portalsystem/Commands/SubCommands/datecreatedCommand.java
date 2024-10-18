package quest.yuzhou.portalsystem.Commands.SubCommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.portalsystem.Commands.SubCommand;
import quest.yuzhou.portalsystem.Modal.Portal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static quest.yuzhou.portalsystem.PortalSystem.prefix;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.findPortalByName;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.findPortalsByMember;

public class datecreatedCommand extends SubCommand {
    @Override
    public String getName() {
        return "datecreated";
    }

    @Override
    public String getDescription() {
        return "獲得傳送門的創建時間";
    }

    @Override
    public String getSyntax() {
        return "/porman datecreated <傳送門名字>";
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(prefix + " 只有玩家可以執行這個指令");
            return;
        }

        if (args.length != 2) {
            commandSender.sendMessage(prefix + " 正確用法：" + getSyntax());
            return;
        }

        Player p = (Player) commandSender;
        ArrayList<Portal> portals = findPortalsByMember(p.getUniqueId());
        Portal portal = findPortalByName(args[1]);

        if (portal == null || (!portals.contains(portal) && !p.isOp())) {
            p.sendMessage(prefix + " 你不是該傳送門的成員，或者該傳送門不存在。");
            return;
        }

        if (!portal.isActive()) {
            p.sendMessage(prefix + " 這個傳送門已被刪除");
        }

        p.sendMessage(prefix + " " + ChatColor.AQUA + portal.getName() + ChatColor.YELLOW + " 的創建時間：（日/月/年 時:分:秒）");
        String pattern = "dd/MM/yyyy HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        String dateFormatted = dateFormat.format(portal.getDateCreated());
        p.sendMessage(dateFormatted);
    }
}
