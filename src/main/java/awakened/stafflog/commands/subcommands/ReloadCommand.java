package awakened.stafflog.commands.subcommands;

import awakened.stafflog.StaffLog;
import awakened.stafflog.commands.StaffLogSubCommand;
import awakened.stafflog.exception.CommandException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReloadCommand extends StaffLogSubCommand {

    private StaffLog plugin;

    public ReloadCommand(StaffLog plugin) {
        super("reload");
        this.plugin = plugin;
        setPermission(getName());
    }

    @Override
    public String getPossibleArguments() {
        return null;
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) throws CommandException {
        plugin.reloadConfig();
        sender.sendMessage("§aStaffLog successfully reloaded.");
    }

    @Override
    public List<String> getTutorial() {
        return Arrays.asList("Reload the plugin.");
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.GENERIC;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
