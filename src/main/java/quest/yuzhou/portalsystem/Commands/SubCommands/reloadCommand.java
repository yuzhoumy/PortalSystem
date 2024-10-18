package quest.yuzhou.portalsystem.Commands.SubCommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.portalsystem.Commands.SubCommand;

import static quest.yuzhou.portalsystem.PortalSystem.getPlugin;
import static quest.yuzhou.portalsystem.PortalSystem.prefix;

public class reloadCommand extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "重新讀取插件設定檔。";
    }

    @Override
    public String getSyntax() {
        return "/porman reload";
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!player.isOp()) {
                player.sendMessage(prefix + ChatColor.RED + " 你沒有權限");
                return;
            }
        }
        commandSender.sendMessage("開始讀取……");
        getPlugin().reloadConfig();
        commandSender.sendMessage(ChatColor.YELLOW + "讀取完畢。本插件由宇宙製作。");
    }
}
