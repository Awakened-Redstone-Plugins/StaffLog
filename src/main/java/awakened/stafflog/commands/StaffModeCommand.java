package awakened.stafflog.commands;

import awakened.stafflog.StaffLog;
import awakened.stafflog.storage.Database;
import awakened.stafflog.util.Errors;
import awakened.stafflog.exception.CommandException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class StaffModeCommand implements CommandExecutor, TabCompleter {

    private StaffLog plugin;
    private Errors error = Errors.NONE;
    private Database database;

    public StaffModeCommand(Database database, StaffLog plugin) {
        super();
        this.database = database;
        this.plugin = plugin;
    }

    /* Old code.
    private String translateError() {
        String translation;
        String errorS1 = error.toString().toLowerCase();
        String errorS2 = errorS1.replace('_', '-');
        translation = plugin.getConfig().getString("stafflog.messages.errors." + errorS2);
        return translation;
    }*/

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {sendError(sender, "not-a-user"); return true;}
            Player player = (Player) sender;
            if (!player.hasPermission("stafflog.commands.staff-mode")) {sendError(sender,"no-permission"); return true;}
            database.updateMode(player.getUniqueId());
            String stats = database.getMode(player.getUniqueId()) ? "on" : "off";
            player.sendMessage("Staff mode: " + stats);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }

    private void sendError(@NotNull CommandSender sender, String error) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', StaffLog.getError(error)));
    }
}
