package quest.yuzhou.portalsystem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.portalsystem.Commands.SubCommands.*;

import java.util.ArrayList;

public class CommandManager implements CommandExecutor {
    private static final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public CommandManager() {
        subCommands.add(new saveportalCommand());
        subCommands.add(new addmemberCommand());
        subCommands.add(new removememberCommand());
        subCommands.add(new deleteCommand());
        subCommands.add(new addadminCommand());
        subCommands.add(new removeadminCommand());
        subCommands.add(new listportalCommand());
        subCommands.add(new listmemberCommand());
        subCommands.add(new listallportalsCommand());
        subCommands.add(new reloadCommand());
        subCommands.add(new newCommand());
        subCommands.add(new datecreatedCommand());
        subCommands.add(new menuCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (args.length > 0) {

            for (int i = 0; i < getSubCommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {
                    getSubCommands().get(i).perform(commandSender, args);
                    return true;
                }
            }
        }

        commandSender.sendMessage(ChatColor.GREEN + "------傳送門系統------");
        for (SubCommand subCommand : getSubCommands()) {
            if (subCommand instanceof AdminSubCommand && commandSender instanceof Player && !commandSender.isOp()) {
                continue;
            }

            commandSender.sendMessage(subCommand.getSyntax() + " " + ChatColor.AQUA + subCommand.getDescription());
        }


        return true;
    }

    public static ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }

}
