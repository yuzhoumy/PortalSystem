package quest.yuzhou.portalsystem.Commands.SubCommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.portalsystem.Commands.SubCommand;
import quest.yuzhou.portalsystem.Modal.Portal;

import java.util.ArrayList;
import java.util.UUID;

import static quest.yuzhou.portalsystem.PortalSystem.getPlugin;
import static quest.yuzhou.portalsystem.PortalSystem.prefix;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.*;

public class listmemberCommand extends SubCommand {

    @Override
    public String getName() {
        return "listmember";
    }

    @Override
    public String getDescription() {
        return " 列出所有傳送門成員以及管理員";
    }

    @Override
    public String getSyntax() {
        return "/porman listmember <傳送門名字>";
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

        p.sendMessage(prefix + " " + ChatColor.AQUA + args[1] + " 的所有可進出成員：");
        for (String name : portal.getAllMembers()) {
            p.sendMessage(ChatColor.GREEN + getPlugin().getServer().getOfflinePlayer(UUID.fromString(name)).getName());
        }

        p.sendMessage(prefix + " " + ChatColor.AQUA + args[1] + " 的所有管理員：");
        for (String name : portal.getAllAdmin()) {
            p.sendMessage(ChatColor.YELLOW + getPlugin().getServer().getOfflinePlayer(UUID.fromString(name)).getName());
        }
    }
}
