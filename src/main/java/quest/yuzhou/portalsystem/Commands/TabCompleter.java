package quest.yuzhou.portalsystem.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.portalsystem.Modal.Portal;

import java.util.ArrayList;
import java.util.List;

import static quest.yuzhou.portalsystem.Commands.CommandManager.getSubCommands;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.findPortalsByAdmin;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.findPortalsByMember;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("porman")) {
            if (strings.length == 1) {
                List<String> subCommands = new ArrayList<>();
                for (SubCommand subCommand : getSubCommands()) {
                    subCommands.add(subCommand.getName());
                }
                return subCommands;
            } else {

                Player player = (Player) commandSender;

                switch (strings[1]) {
                    case "addmember":
                    case "removemember":
                    case "addadmin":
                    case "removeadmin":{
                        if (strings.length == 3) {
                            List<String> onlinePlayerNames = new ArrayList<>();
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                onlinePlayerNames.add(onlinePlayer.getName());
                            }
                            return onlinePlayerNames;
                        }

                        ArrayList<Portal> portals = findPortalsByAdmin(player.getUniqueId());
                        List<String> portalNames = new ArrayList<>();
                        for (Portal portal : portals) {
                            portalNames.add(portal.getName());
                        }
                        return portalNames;
                    }
                    case "listmember": {
                        ArrayList<Portal> portals = findPortalsByMember(player.getUniqueId());
                        List<String> portalNames = new ArrayList<>();
                        for (Portal portal : portals) {
                            portalNames.add(portal.getName());
                        }
                        return portalNames;
                    }
                    case "removeportal": {
                        ArrayList<Portal> portals = findPortalsByAdmin(player.getUniqueId());
                        List<String> portalNames = new ArrayList<>();
                        for (Portal portal : portals) {
                            portalNames.add(portal.getName());
                        }
                        return portalNames;
                    }

                }
            }
        }
        return null;
    }
}
