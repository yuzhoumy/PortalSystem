package quest.yuzhou.portalsystem.Commands.SubCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.portalsystem.Commands.SubCommand;
import quest.yuzhou.portalsystem.Menu.Menu;
import quest.yuzhou.portalsystem.Modal.Portal;

import java.util.ArrayList;

import static quest.yuzhou.portalsystem.PortalSystem.prefix;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.*;

public class menuCommand extends SubCommand {
    @Override
    public String getName() {
        return "menu";
    }

    @Override
    public String getDescription() {
        return "打開傳送門管理界面";
    }

    @Override
    public String getSyntax() {
        return "/porman menu <傳送門名字>";
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
        ArrayList<Portal> portals = findPortalsByAdmin(p.getUniqueId());
        Portal portal = findPortalByName(args[1]);

        if (portal == null || (!portals.contains(portal) && !p.isOp())) {
            p.sendMessage(prefix + " 你不是該傳送門的管理員，或者該傳送門不存在。");
            return;
        }

        if (!portal.isActive()) {
            p.sendMessage(prefix + " 這個傳送門已被刪除");
        }

        Menu.openMainMenu(p, portal);
    }
}
