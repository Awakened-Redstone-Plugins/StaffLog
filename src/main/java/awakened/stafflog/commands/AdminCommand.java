package awakened.stafflog.commands;

import awakened.stafflog.StaffLog;
import awakened.stafflog.commands.subcommands.*;
import awakened.stafflog.exception.CommandException;
import awakened.stafflog.storage.Database;
import awakened.stafflog.util.Errors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private Errors error = Errors.NONE;

    private StaffLog plugin;

    private List<StaffLogSubCommand> subCommands;
    private Map<Class<? extends StaffLogSubCommand>, StaffLogSubCommand> subCommandsByClass;

    public AdminCommand(StaffLog plugin, Database database) {
        this.plugin = plugin;
        subCommands = new ArrayList<>();
        subCommandsByClass = new HashMap<>();

        registerSubCommand(new SetLevelCommand(database));
        registerSubCommand(new RemovePlayerCommand(database));
        registerSubCommand(new HelpCommand());
        registerSubCommand(new ViewLogCommand());
        registerSubCommand(new ReloadCommand(plugin));
    }

    /*
     * This code has entirely been copied from HolographicDisplays.
     * The code has been extracted from the GitHub.
     * https://github.com/filoghost/HolographicDisplays
     * http://dev.bukkit.org/bukkit-plugins/holographic-displays
     *
     * Some modifications were made.
     */

    public void registerSubCommand(StaffLogSubCommand subCommand) {
        subCommands.add(subCommand);
        subCommandsByClass.put(subCommand.getClass(), subCommand);
    }

    private String versionMessage() {
        return ChatColor.DARK_AQUA + "Server is running " + ChatColor.AQUA + "Staff Control " + ChatColor.DARK_AQUA + "v" + StaffLog.getInstance().getDescription().getVersion() + " by " + ChatColor.AQUA + "Awakened Redstone";
    }

    public List<StaffLogSubCommand> getSubCommands() {
        return new ArrayList<>(subCommands);
    }

    public StaffLogSubCommand getSubCommand(Class<? extends StaffLogSubCommand> subCommandClass) {
        return subCommandsByClass.get(subCommandClass);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> scommands = new ArrayList<>();
        for(int i = 0; i < getSubCommands().size(); i++) {
            StaffLogSubCommand scommand = getSubCommands().get(i);
            scommands.addAll(scommand.getArgs());
        }

        scommands.add("version");
        scommands.add("v");

        if (!(sender instanceof Player)) {sendError(sender, "not-a-user"); return true;}
        if (args.length == 0) {if(plugin.getConfig().getBoolean("stafflog.messages.send-no-args-error")) sendError(sender, "no-args"); else sender.sendMessage(versionMessage()); return true;}
        if (!scommands.contains(args[0])) {sendError(sender, "invalid-arg"); return true;}
        if (args[0].equals("version") || args[0].equals("v")) {
            sender.sendMessage(versionMessage());
            if (sender.hasPermission("stafflog.help")) {
                sender.sendMessage(ChatColor.DARK_AQUA + "Commands: " + ChatColor.AQUA + "/" + label + " help");
            }
            return true;
        }

        for (StaffLogSubCommand subCommand : subCommands) {
            if (subCommand.isValidTrigger(args[0])) {

                if (!subCommand.hasPermission(sender)) {
                    sendError(sender, "no-permission");
                    return true;
                }

                if (args.length - 1 >= subCommand.getMinimumArguments()) {
                    try {
                        subCommand.execute(sender, label, Arrays.copyOfRange(args, 1, args.length));
                    } catch (CommandException e) {
                        sender.sendMessage(ChatColor.RED + e.getMessage());
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + subCommand.getName() + " " + subCommand.getPossibleArguments());
                }

                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Unknown sub-command. Type \"/" + label + " help\" for a list of commands.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        for (StaffLogSubCommand subCommand : subCommands) {
            if(subCommand.getArgs().contains(args[0]) && subCommand.hasPermission(sender))
                return subCommand.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
        }

        if(args.length <= 1) {
            List<String> subCommandsList = new ArrayList<>();
            subCommandsList.add("version");
            for (StaffLogSubCommand subCommand : subCommands) {
                if (subCommand.hasPermission(sender))
                    subCommandsList.add(subCommand.getName());
            }
            return subCommandsList;
        }
        return Collections.emptyList();
    }

    private void sendError(@NotNull CommandSender sender, String error) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', StaffLog.getError(error)));
    }
}
