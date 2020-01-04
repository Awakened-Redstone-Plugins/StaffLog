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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HelpCommand extends StaffLogSubCommand {

    public HelpCommand() {
        super("help");
        setPermission("help");
    }

    @Override
    public String getPossibleArguments() {
        return null;
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override @SuppressWarnings("ConstantConditions")
    public void execute(CommandSender sender, String label, String[] args) throws CommandException {
        Player player = (Player) sender;
        Inventory inventory = Bukkit.createInventory(null, 45, "§3Staff Log help menu");

        ItemStack commands = new ItemStack(Material.COMMAND_BLOCK);
        ItemStack about = new ItemStack(Material.LECTERN);
        ItemStack permissions = new ItemStack(Material.EMERALD);

        ItemMeta commands_meta = commands.getItemMeta();
        ItemMeta about_meta = about.getItemMeta();
        ItemMeta permissions_meta = permissions.getItemMeta();

        commands_meta.setDisplayName("§bCommands");
        List<String> lore_commands = new ArrayList<>();
        lore_commands.add("§7Show all commands");
        commands_meta.setLore(lore_commands);


        about_meta.setDisplayName("§bAbout");
        List<String> lore_about = new ArrayList<>();
        lore_about.add("§7Show about the plugin");
        about_meta.setLore(lore_about);


        permissions_meta.setDisplayName("§bPermissions");
        List<String> lore_permissions = new ArrayList<>();
        lore_permissions.add("§7Show all permissions");
        permissions_meta.setLore(lore_permissions);


        commands.setItemMeta(commands_meta);
        about.setItemMeta(about_meta);
        permissions.setItemMeta(permissions_meta);

        inventory.setItem(20, commands);
        inventory.setItem(22, about);
        inventory.setItem(24, permissions);

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

        player.openInventory(inventory);
    }

    @Override
    public List<String> getTutorial() {
        return Arrays.asList("Shows help GUI.");
    }

    @Override
    public SubCommandType getType() {
        return SubCommandType.VISUAL;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
