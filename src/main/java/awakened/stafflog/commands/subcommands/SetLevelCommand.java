package awakened.stafflog.commands.subcommands;

import awakened.stafflog.StaffLog;
import awakened.stafflog.commands.StaffLogSubCommand;
import awakened.stafflog.exception.CommandException;
import awakened.stafflog.storage.Database;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
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

public class SetLevelCommand extends StaffLogSubCommand {

    private Database database;
    private LuckPerms luckPerms = StaffLog.api;
    private UserManager userManager = luckPerms.getUserManager();

    public SetLevelCommand(Database database) {
        super("setlevel", "add");
        this.database = database;
        setPermission(getName());
    }

    @Override
    public String getPossibleArguments() {
        return "<player> <group>";
    }

    @Override
    public int getMinimumArguments() {
        return 2;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) throws CommandException {
        User lp_target = loadUser(args[0]);
        if(lp_target == null) throw new CommandException("invalid player");
        OfflinePlayer target = Bukkit.getOfflinePlayer(lp_target.getUniqueId());
        database.createPlayer(lp_target.getUniqueId(), target);
        Group group = luckPerms.getGroupManager().getGroup(args[1]);
        if(group == null) throw new CommandException("invalid group");
        database.setLevel(lp_target.getUniqueId(), args[1]);
        lp_target.data().add(PermissionNode.builder("stafflog.commands.staff-mode").build());
        sender.sendMessage("§7Defined §b" + target.getName() + "§7 as §b" + args[1]);
        luckPerms.getUserManager().saveUser(lp_target);
    }

    @Override
    public List<String> getTutorial() {
        return Arrays.asList("Sets the staff rank of a player.", "");
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.GENERIC;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            for (Player player : players) {if (player.getName().toLowerCase().contains(args[0].toLowerCase())) completions.add(player.getName());}
        } else if (args.length == 2) {
            List<Group> groups = new ArrayList<>(luckPerms.getGroupManager().getLoadedGroups());
            for (Group group : groups) {if (group.getName().toLowerCase().contains(args[1].toLowerCase())) completions.add(group.getName());}
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
