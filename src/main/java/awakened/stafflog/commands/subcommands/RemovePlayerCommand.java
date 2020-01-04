package awakened.stafflog.commands.subcommands;

import awakened.stafflog.StaffLog;
import awakened.stafflog.commands.StaffLogSubCommand;
import awakened.stafflog.exception.CommandException;
import awakened.stafflog.storage.Database;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RemovePlayerCommand extends StaffLogSubCommand {

    private LuckPerms luckPerms = StaffLog.api;
    private UserManager userManager = luckPerms.getUserManager();
    private Database database;

    public RemovePlayerCommand(Database database) {
        super("remove", "rm");
        this.database = database;
        setPermission(getName());
    }

    @Override
    public String getPossibleArguments() {
        return "<player>";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) throws CommandException {
        if(!database.hasPlayer(args[0])) throw new CommandException("invalid player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(database.getPlayer(args[0]));
        database.removePlayer(target.getUniqueId());
        User lp_target = loadUser(args[0]);
        if(lp_target == null) throw new CommandException("invalid player");
        lp_target.data().remove(PermissionNode.builder("stafflog.commands.staff-mode").build());
        sender.sendMessage("§7Removed §b" + target.getName() + "§7.");
        luckPerms.getUserManager().saveUser(lp_target);
    }

    @Override
    public List<String> getTutorial() {
        return Arrays.asList("Removes a player.");
    }

    @Override
    public StaffLogSubCommand.SubCommandType getType() {
        return StaffLogSubCommand.SubCommandType.GENERIC;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<OfflinePlayer> players = new ArrayList<>(database.getPlayers());
            for (OfflinePlayer player : players) {if (player.getName().toLowerCase().contains(args[0].toLowerCase())) completions.add(player.getName());}
        }
        return completions;
    }

    private User loadUser(String username) {
        User user = userManager.getUser(username);
        return user != null ? user : loadUser(userManager.lookupUniqueId(username).join());
    }

    private User loadUser(UUID player) {
        if (player == null) return null;
        User user = userManager.getUser(player);
        return user != null ? user : userManager.loadUser(player).join();
    }
}
