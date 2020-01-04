package awakened.stafflog.commands.subcommands;

import awakened.stafflog.commands.StaffLogSubCommand;
import awakened.stafflog.exception.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewLogCommand extends StaffLogSubCommand {

    public ViewLogCommand() {
        super("log");
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
        Player player = (Player) sender;
        player.openInventory(main());
    }

    @Override
    public List<String> getTutorial() {
        return null;
    }

    @Override
    public SubCommandType getType() {
        return null;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }

    private Inventory main() {
        Inventory inventory = Bukkit.createInventory(null, 45, "§3Staff Log command log menu");

        ItemStack commands = new ItemStack(Material.COMMAND_BLOCK);

        ItemMeta commands_meta = commands.getItemMeta();

        commands_meta.setDisplayName("§bCommands");
        List<String> lore_commands = new ArrayList<>();
        lore_commands.add("§7Show commands log");
        commands_meta.setLore(lore_commands);


        commands.setItemMeta(commands_meta);

        inventory.setItem(22, commands);

        for (int i = 0; i < inventory.getSize(); i++) {

            ItemStack glass_pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta glass_pane_meta = glass_pane.getItemMeta();

            glass_pane_meta.setDisplayName(" ");
            glass_pane.setItemMeta(glass_pane_meta);

            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null) {
                inventory.setItem(i, glass_pane);
            }
        }
        return inventory;
    }
}
