package quest.yuzhou.portalsystem.Commands.SubCommands;

import org.bukkit.command.CommandSender;
import quest.yuzhou.portalsystem.Commands.SubCommand;

import java.io.IOException;

import static quest.yuzhou.portalsystem.PortalSystem.getPlugin;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.savePortals;

public class saveportalCommand extends SubCommand {
    @Override
    public String getName() {
        return "saveportal";
    }

    @Override
    public String getDescription() {
        return "僅管理員可用，保存所有更動";
    }

    @Override
    public String getSyntax() {
        return "/porman saveportal";
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {

        int maxTries = getPlugin().getConfig().getInt("savePortalMaxTries");
        int count = 0;

        while (true) {
            try {
                if (count <= maxTries) {
                    getPlugin().getLogger().info("STARTING TO SAVE PORTAL. TRIES COUNT:" + count);
                    savePortals();
                    commandSender.sendMessage("[PortalSystem] Portal saved successfully");
                } else {
                    commandSender.sendMessage("[PortalSystem] Failed to save portals.");
                    getPlugin().getLogger().info("STOPPING TO SAVE PORTAL DUE TO MAX TRIES WAS REACHED");
                }
                return;
            } catch (IOException e) {
                getPlugin().getLogger().info("FAILED TO SAVE PORTALS");
                e.printStackTrace();
                count++;
            }
        }
    }
}
