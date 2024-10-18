package quest.yuzhou.portalsystem.Commands.SubCommands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.portalsystem.Commands.SubCommand;
import quest.yuzhou.portalsystem.Modal.Portal;

import java.util.ArrayList;
import java.util.UUID;

import static quest.yuzhou.portalsystem.PortalSystem.getPlugin;
import static quest.yuzhou.portalsystem.PortalSystem.prefix;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.getPortals;

public class listallportalsCommand extends SubCommand {

    @Override
    public String getName() {
        return "listallportals";
    }

    @Override
    public String getDescription() {
        return "查看所有的傳送門，僅管理員可用";
    }

    @Override
    public String getSyntax() {
        return "/porman listallportals";
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(prefix + " 只有玩家可以執行這個指令。");
            return;
        }

        Player p = (Player) commandSender;

        if (!p.isOp()) {
            p.sendMessage(prefix + " 只有工作人員可以使用這個指令。");
            return;
        }

         for (Portal portal : getPortals()) {

             if (!portal.isActive()) continue;

             Location location = portal.getLocation();
             ArrayList<String> playersArrayList = new ArrayList<>(); // Initialize the ArrayList

             for (String memberUUID : portal.getAllMembers()) {
                 String playerName = getPlugin().getServer().getOfflinePlayer(UUID.fromString(memberUUID)).getName();
                 if (playerName != null) {
                     playersArrayList.add(playerName);
                 }
             }

             String players = String.join(", ", playersArrayList); // Join the player names into a single string
             p.sendMessage(ChatColor.AQUA + players + " " + ChatColor.GREEN + portal.getName() + " " + ChatColor.YELLOW + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
        }

    }

}
