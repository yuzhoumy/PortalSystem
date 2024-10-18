package quest.yuzhou.portalsystem.Commands.SubCommands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.portalsystem.Commands.SubCommand;
import quest.yuzhou.portalsystem.Modal.Portal;

import java.util.ArrayList;

import static quest.yuzhou.portalsystem.PortalSystem.prefix;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.findPortalsByAdmin;

public class listportalCommand extends SubCommand {

    @Override
    public String getName() {
        return "listportal";
    }

    @Override
    public String getDescription() {
        return "查看自己所管理的所有傳送門。";
    }

    @Override
    public String getSyntax() {
        return "/porman listportal";
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(prefix + " 只有玩家可以執行這個指令。");
            return;
        }

        Player p = (Player) commandSender;
        ArrayList<Portal> portals = findPortalsByAdmin(p.getUniqueId());

        if (portals.isEmpty()) {
            p.sendMessage(prefix + " 你不是任何傳送門的管理員。");
        } else {
            p.sendMessage(prefix + " 您是以下傳送門的管理員。");
            for (Portal portal : portals) {
                Location location = portal.getLocation();
                p.sendMessage(ChatColor.GREEN + portal.getName() + " " + ChatColor.YELLOW + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
            }
        }
    }
}
