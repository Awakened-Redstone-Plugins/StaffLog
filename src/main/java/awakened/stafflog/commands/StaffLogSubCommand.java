package awakened.stafflog.commands;

import awakened.stafflog.exception.CommandException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class StaffLogSubCommand implements TabCompleter {

    /*
     * This code has entirely been copied from HolographicDisplays.
     * The code has been extracted from the GitHub.
     * https://github.com/filoghost/HolographicDisplays
     * http://dev.bukkit.org/bukkit-plugins/holographic-displays
     *
     * Some modifications were made.
     */

    private String name;
    private String permission;
    private String[] aliases;

    public StaffLogSubCommand(String name) {
        this(name, new String[0]);
    }

    public StaffLogSubCommand(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public String getName() {
        return name;
    }

    public List<String> getArgs() {
        List<String> args = new ArrayList<>(Arrays.asList(name));
        args.addAll(Arrays.asList(aliases));
        return args;
    }

    public void setPermission(String permission) {
        this.permission = "stafflog.admin.commands." + permission;
    }

    public String getPermission() {
        return permission;
    }

    public final boolean hasPermission(CommandSender sender) {
        if (permission == null) return true;
        return sender.hasPermission(permission);
    }

    public abstract String getPossibleArguments();

    public abstract int getMinimumArguments();

    public abstract void execute(CommandSender sender, String label, String[] args) throws CommandException;

    public abstract List<String> getTutorial();

    public abstract SubCommandType getType();

    public enum SubCommandType {
        GENERIC, VISUAL, HIDDEN
    }


    public final boolean isValidTrigger(String name) {
        if (this.name.equalsIgnoreCase(name)) {
            return true;
        }

        if (aliases != null) {
            for (String alias : aliases) {
                if (alias.equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public abstract List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args);
}
