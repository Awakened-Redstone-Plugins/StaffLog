package awakened.stafflog.listeners;

import awakened.stafflog.StaffLog;
import awakened.stafflog.storage.Database;
import awakened.stafflog.util.Staff;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class GUIDetection implements Listener {

    private Database database;
    private LuckPerms luckPerms = StaffLog.api;
    private HashMap<Player, Integer> pages = new HashMap<>();
    private HashMap<Player, Integer> log_pages = new HashMap<>();
    private HashMap<String, Staff> staff = new HashMap<>();

    public GUIDetection(Database database) {
        super();
        this.database = database;
    }

    private ItemStack backHead() {

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.setDisplayName("§cBack");

        meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("5ca62fac-d094-4346-8361-e1dfdd970607")));

        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void helpGUI(@NotNull InventoryClickEvent e) {
        Player player = Bukkit.getPlayer(e.getWhoClicked().getUniqueId());
        if (player != null && player.getOpenInventory().getTitle().equals("§3Staff Log help menu") && e.getClickedInventory().getHolder() == null) {
            ItemStack itemStack = e.getCurrentItem();
            Material material = itemStack.getType();
            e.setCancelled(true);

            if (material == Material.COMMAND_BLOCK) {
                if (e.getClickedInventory().getSize() == 45) player.openInventory(commandsInv());
                else e.getClickedInventory().setContents(commandsInv().getContents());
            } else if (material == Material.LECTERN) {
                if (e.getClickedInventory().getSize() == 45) player.openInventory(aboutInv());
                else e.getClickedInventory().setContents(aboutInv().getContents());
            } else if (material == Material.EMERALD) {
                if (e.getClickedInventory().getSize() == 45) player.openInventory(permissionsInv());
                else e.getClickedInventory().setContents(permissionsInv().getContents());
            } else if (material == Material.BOOK) {
                CommandSender sender = Bukkit.getConsoleSender();
                Bukkit.getServer().dispatchCommand(sender, "tellraw " + player.getName() + " [\"\",{\"text\":\"\u00a7aClick here\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/authors/marcos_13.439368/\"}}]");
            }
        }
    }

    @EventHandler
    public void logGUI(@NotNull InventoryClickEvent e) {
        Player player = Bukkit.getPlayer(e.getWhoClicked().getUniqueId());
        if (player != null && player.getOpenInventory().getTitle().equals("§3Staff Log command log menu") && e.getClickedInventory().getHolder() == null) {
            ItemStack itemStack = e.getCurrentItem();
            Material material = itemStack.getType();
            e.setCancelled(true);

            if (material == Material.COMMAND_BLOCK) {
                player.sendMessage("§7§l[§3§lSL§7§l] §aGetting players from database...");
                player.openInventory(playersInv(player));
                player.sendMessage("§7§l[§3§lSL§7§l] §aDone.");
            }
            if (material == Material.PLAYER_HEAD && e.getSlot() != 46 && e.getSlot() != 52 && e.getSlot() != 49) {
                player.sendMessage("§7§l[§3§lSL§7§l] §aGetting log from database...");
                player.openInventory(logInv(itemStack.getItemMeta().getDisplayName().replace("§b", ""), player));
                player.sendMessage("§7§l[§3§lSL§7§l] §aDone.");
            }
            if (e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equals("§aPrevious page")) {
                pages.put(player, pages.get(player)-1);
                e.getClickedInventory().setContents(playersInv(player).getContents());
            }
            if (e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equals("§aNext page")) {
                pages.put(player, pages.get(player)+1);
                e.getClickedInventory().setContents(playersInv(player).getContents());
            }
            if (e.getCurrentItem().equals(backHead())) {
                player.openInventory(mainLogInv());
            }
        }
    }

    @EventHandler
    public void mainLogGUI(@NotNull InventoryClickEvent e) {
        Player player = Bukkit.getPlayer(e.getWhoClicked().getUniqueId());
        if (player != null && player.getOpenInventory().getTitle().equals("§3Command log") && e.getClickedInventory().getHolder() == null) {
            ItemStack itemStack = e.getCurrentItem();
            Material material = itemStack.getType();
            e.setCancelled(true);

            if (e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equals("§aPrevious page")) {
                log_pages.put(player, log_pages.get(player)-1);
                e.getClickedInventory().setContents(playersInv(player).getContents());
            }
            if (e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equals("§aNext page")) {
                log_pages.put(player, log_pages.get(player)+1);
                e.getClickedInventory().setContents(playersInv(player).getContents());
            }
            if (e.getCurrentItem().equals(backHead())) {
                player.openInventory(playersInv(player));
            }
        }
    }


    @NotNull
    private Inventory mainHelpInv() {
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
        lore_permissions.add("§7Show all permissions.");
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
        return inventory;
    }

    private Inventory mainLogInv() {
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

    @NotNull
    private Inventory commandsInv() {
        Inventory inventory = Bukkit.createInventory(null, 54, "§3Staff Log help menu");

        ItemStack about = new ItemStack(Material.LECTERN);
        ItemStack permissions = new ItemStack(Material.EMERALD);


        ItemStack setLevel = new ItemStack(Material.NETHER_STAR);
        ItemStack remove = new ItemStack(Material.TNT);
        ItemStack staffMode = new ItemStack(Material.ENDER_PEARL);



        ItemMeta about_meta = about.getItemMeta();
        ItemMeta permissions_meta = permissions.getItemMeta();


        ItemMeta setLevel_meta = setLevel.getItemMeta();
        ItemMeta remove_meta = remove.getItemMeta();
        ItemMeta staffMode_meta = staffMode.getItemMeta();




        setLevel_meta.setDisplayName("§bSetLevel");
        List<String> lore_setLevel = new ArrayList<>();
        lore_setLevel.add("§7Command:");
        lore_setLevel.add("§7  /staff-log setLevel <player> <group>");
        lore_setLevel.add("§7  /sl setLevel <player> <group>");
        lore_setLevel.add(" ");
        lore_setLevel.add("§7Aliases:");
        lore_setLevel.add("/sl add <player> <group>");
        setLevel_meta.setLore(lore_setLevel);

        remove_meta.setDisplayName("§bRemove");
        List<String> lore_remove = new ArrayList<>();
        lore_remove.add("§7Command:");
        lore_remove.add("§7  /staff-log remove <player>");
        lore_remove.add("§7  /sl remove <player>");
        lore_remove.add(" ");
        lore_remove.add("§7Aliases:");
        lore_remove.add("§7  /sl rm <player>");
        remove_meta.setLore(lore_remove);

        staffMode_meta.setDisplayName("§bStaffMode");
        List<String> lore_staffMode = new ArrayList<>();
        lore_staffMode.add("§7Command:");
        lore_staffMode.add("§7  /staff-mode");
        lore_staffMode.add(" ");
        lore_staffMode.add("§7Aliases:");
        lore_staffMode.add("§7  /sm");
        lore_staffMode.add("§7  /toggle-staff-mode");
        lore_staffMode.add("§7  /tsm");
        staffMode_meta.setLore(lore_staffMode);



        about_meta.setDisplayName("§bAbout");
        List<String> lore_about = new ArrayList<>();
        lore_about.add("§7Show about the plugin");
        about_meta.setLore(lore_about);

        permissions_meta.setDisplayName("§bPermissions");
        List<String> lore_permissions = new ArrayList<>();
        lore_permissions.add("§7Show all permissions");
        permissions_meta.setLore(lore_permissions);


        setLevel.setItemMeta(setLevel_meta);
        remove.setItemMeta(remove_meta);
        staffMode.setItemMeta(staffMode_meta);

        about.setItemMeta(about_meta);
        permissions.setItemMeta(permissions_meta);

        inventory.setItem(20, setLevel);
        inventory.setItem(22, remove);
        inventory.setItem(24, staffMode);

        inventory.setItem(40, about);
        inventory.setItem(41, permissions);

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

    @NotNull
    private Inventory aboutInv() {
        Inventory inventory = Bukkit.createInventory(null, 54, "§3Staff Log help menu");

        ItemStack commands = new ItemStack(Material.COMMAND_BLOCK);
        ItemStack permissions = new ItemStack(Material.EMERALD);

        ItemStack about = new ItemStack(Material.PAPER);
        ItemStack creator = new ItemStack(Material.PLAYER_HEAD);
        ItemStack resources = new ItemStack(Material.BOOK);


        ItemMeta commands_meta = commands.getItemMeta();
        ItemMeta permissions_meta = permissions.getItemMeta();

        ItemMeta about_meta = about.getItemMeta();
        SkullMeta creator_meta = (SkullMeta) creator.getItemMeta();
        ItemMeta resources_meta = resources.getItemMeta();



        about_meta.setDisplayName("§bAbout");
        List<String> lore_about = new ArrayList<>();
        lore_about.add("§7Command:");
        about_meta.setLore(lore_about);

        creator_meta.setDisplayName("§bCreator");

        List<String> lore_creator = new ArrayList<>();
        lore_creator.add("§7This plugin has been created by");
        lore_creator.add("§bAwakened Redstone§7, a developer");
        lore_creator.add("§7that always makes plugins to improve");
        lore_creator.add("§7the servers experience");
        creator_meta.setLore(lore_creator);
        creator_meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("2e7c2349-94ec-4862-8b68-344d049840d2")));

        resources_meta.setDisplayName("§bMore plugins");
        List<String> lore_resources = new ArrayList<>();
        lore_resources.add("§7Click here to go the Awakened's resource page");
        resources_meta.setLore(lore_resources);



        commands_meta.setDisplayName("§bCommands");
        List<String> lore_commands = new ArrayList<>();
        lore_commands.add("§7Show all commands");
        commands_meta.setLore(lore_commands);

        permissions_meta.setDisplayName("§bPermissions");
        List<String> lore_permissions = new ArrayList<>();
        lore_permissions.add("§7Show all permissions");
        permissions_meta.setLore(lore_permissions);



        about.setItemMeta(about_meta);
        creator.setItemMeta(creator_meta);
        resources.setItemMeta(resources_meta);

        commands.setItemMeta(commands_meta);
        permissions.setItemMeta(permissions_meta);

        inventory.setItem(20, about);
        inventory.setItem(22, creator);
        inventory.setItem(24, resources);

        inventory.setItem(39, commands);
        inventory.setItem(41, permissions);


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

    @NotNull
    private Inventory permissionsInv() {
        Inventory inventory = Bukkit.createInventory(null, 54, "§3Staff Log help menu");

        ItemStack commands = new ItemStack(Material.COMMAND_BLOCK);
        ItemStack about = new ItemStack(Material.LECTERN);

        ItemStack permissions = new ItemStack(Material.ENCHANTING_TABLE);

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


        permissions_meta.setDisplayName("§aPermissions");
        List<String> lore_permissions = new ArrayList<>();
        lore_permissions.add("§7§l[§b§lL§3§lP§7§l] §b§lLuck§3§lPerms");
        lore_permissions.add(" ");
        lore_permissions.add("§b- stafflog.admin.commands.setlevel");
        lore_permissions.add("§b- stafflog.admin.commands.remove");
        lore_permissions.add("§b- stafflog.admin.commands.help");
        lore_permissions.add("§b- stafflog.commands.staff-mode");
        permissions_meta.setLore(lore_permissions);


        commands.setItemMeta(commands_meta);
        about.setItemMeta(about_meta);
        permissions.setItemMeta(permissions_meta);

        inventory.setItem(22, permissions);

        inventory.setItem(39, commands);
        inventory.setItem(40, about);

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

    @NotNull
    private Inventory playersInv(Player viewer) {
        Inventory inventory = Bukkit.createInventory(null, 54, "§3Staff Log command log menu");

        if(!pages.containsKey(viewer)) pages.put(viewer, 1);

        List<ItemStack> items = new ArrayList<>();

        for(int i = 0; i < inventory.getSize(); i++) {
            boolean sec1 = i > 9 && i < 17;
            boolean sec2 = i > 18 && i < 26;
            boolean sec3 = i > 27 && i < 35;
            boolean sec4 = i > 36 && i < 44;
            if(!sec1 && !sec2 && !sec3 && !sec4) {
                ItemStack glass_pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta glass_pane_meta = glass_pane.getItemMeta();

                glass_pane_meta.setDisplayName(" ");
                glass_pane.setItemMeta(glass_pane_meta);
                inventory.setItem(i, glass_pane);
            }
        }

        for (OfflinePlayer player : database.getPlayers()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);

            Group group = luckPerms.getGroupManager().getGroup(database.getLevel(player.getUniqueId()));
            assert group != null;

            String groupName = group.getName();

            SkullMeta head_meta = (SkullMeta) head.getItemMeta();

            head_meta.setDisplayName("§b" + player.getName());
            List<String> lore_head = new ArrayList<>();
            lore_head.add("§7Show " + player.getName() + "'s log");
            lore_head.add("§7Group: §r" + groupName);
            head_meta.setLore(lore_head);

            head_meta.setOwningPlayer(player);

            head.setItemMeta(head_meta);

            int weight = group.getWeight().isPresent() ? group.getWeight().getAsInt() : 0;

            if (!staff.containsKey(player.getName()))
            staff.put(player.getName(), new Staff(head, weight));
        }

        items = staff.values().stream().sorted(Comparator.comparingInt(Staff::getWeight)).map(Staff::getItem).collect(Collectors.toList());
        Collections.reverse(items);

        List<ItemStack> pageItems = items.stream().skip((pages.get(viewer)-1)*36).collect(Collectors.toList());
        for(int i = 0; i < 36; i++) {
            if(pageItems.size() > i)
                inventory.addItem(pageItems.get(i));
            ;
        }

        boolean firstPage = pages.get(viewer) == 1;
        boolean lastPage = pageItems.size() <= 36;
        ItemStack nextPageItem = new ItemStack(firstPage?Material.PLAYER_HEAD:Material.PLAYER_HEAD);
        SkullMeta nextPageItemMeta = (SkullMeta) nextPageItem.getItemMeta();
        nextPageItemMeta.setDisplayName(firstPage?"§cYou're on the first page.":"§aPrevious page");
        if (nextPageItem.getItemMeta().getDisplayName().equals("§aPrevious page")) {
            nextPageItemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("e8627b92-1dcb-4733-810c-a2b47833c451")));
        } else {
            nextPageItemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("03d2d48e-5d26-4460-8eaf-26b434b3f349")));
        }

        nextPageItem.setItemMeta(nextPageItemMeta);

        ItemStack lastPageItem = new ItemStack(lastPage?Material.PLAYER_HEAD:Material.PLAYER_HEAD);
        SkullMeta lastPageItemMeta = (SkullMeta) lastPageItem.getItemMeta();
        lastPageItemMeta.setDisplayName(lastPage?"§cYou're on the last page.":"§aNext page");
        if (lastPageItem.getItemMeta().getDisplayName().equals("§aNext page")) {
            lastPageItemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("74d11297-0fd9-4d43-91d9-8e5216a63efa")));
        } else {
            lastPageItemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("03d2d48e-5d26-4460-8eaf-26b434b3f349")));
        }

        lastPageItem.setItemMeta(lastPageItemMeta);

        inventory.setItem(49, backHead());
        inventory.setItem(46, nextPageItem);
        inventory.setItem(52, lastPageItem);

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

    @NotNull
    private Inventory logInv(String logPlayer, Player viewer) {
        Inventory inventory = Bukkit.createInventory(null, 54, "§3Command log");

        if (!log_pages.containsKey(viewer)) log_pages.put(viewer, 1);

        List<String> logs = new ArrayList<>();
        List<ItemStack> items = new ArrayList<>();

        for(int i = 0; i < inventory.getSize(); i++) {
            boolean sec1 = i > 9 && i < 17;
            boolean sec2 = i > 18 && i < 26;
            boolean sec3 = i > 27 && i < 35;
            boolean sec4 = i > 36 && i < 44;
            if(!sec1 && !sec2 && !sec3 && !sec4) {
                ItemStack glass_pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta glass_pane_meta = glass_pane.getItemMeta();

                glass_pane_meta.setDisplayName(" ");
                glass_pane.setItemMeta(glass_pane_meta);
                inventory.setItem(i, glass_pane);
            }
        }

        logs = new ArrayList<>(database.getLog(logPlayer));
        Collections.reverse(logs);

        double size = logs.size();
        double maxSize = size/10;
        maxSize = Math.ceil(maxSize);
        for (int i = 0; i < maxSize; i++)
            items.add(new ItemStack(Material.PAPER));

        List<ItemStack> pageItems = items.stream().skip((log_pages.get(viewer) - 1) * 36).collect(Collectors.toList());
        int lores = 1;
        for (int i = 0; i < 34; i++) {
            if (pageItems.size() > i) {
                List<String> logItems = logs.stream().skip((lores - 1) * 10).collect(Collectors.toList());
                for (int l = 0; l < 10; l++) {
                    if (logItems.size() > l) {
                        ItemStack pageItem = pageItems.get(i);
                        ItemMeta pageItemMeta = pageItem.getItemMeta();
                        pageItemMeta.setDisplayName("§a" + logPlayer + "§a's log");
                        List<String> lore = new ArrayList<>();
                        if (pageItemMeta.hasLore())
                            lore = pageItemMeta.getLore();
                        lore.add(logItems.get(l));
                        pageItemMeta.setLore(lore);
                        pageItem.setItemMeta(pageItemMeta);
                        pageItems.set(i, pageItem);
                    }
                }
                lores++;
                inventory.addItem(pageItems.get(i));
            }
        }

        boolean firstPage = log_pages.get(viewer) == 1;
        boolean lastPage = pageItems.size() <= 36;
        ItemStack nextPageItem = new ItemStack(firstPage ? Material.PLAYER_HEAD : Material.PLAYER_HEAD);
        ItemMeta nextPageItemMeta = nextPageItem.getItemMeta();
        nextPageItemMeta.setDisplayName(firstPage ? "§cYou're on the first page." : "§aPrevious page");
        nextPageItem.setItemMeta(nextPageItemMeta);
        if (nextPageItem.getItemMeta().getDisplayName().equals("§aPrevious page")) {
            SkullMeta head_meta = (SkullMeta) nextPageItemMeta;
            head_meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("e8627b92-1dcb-4733-810c-a2b47833c451")));
            nextPageItemMeta = head_meta;
        } else {
            SkullMeta head_meta = (SkullMeta) nextPageItemMeta;
            head_meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("03d2d48e-5d26-4460-8eaf-26b434b3f349")));
            nextPageItemMeta = head_meta;
        }

        nextPageItem.setItemMeta(nextPageItemMeta);

        ItemStack lastPageItem = new ItemStack(lastPage ? Material.PLAYER_HEAD : Material.PLAYER_HEAD);
        ItemMeta lastPageItemMeta = lastPageItem.getItemMeta();
        lastPageItemMeta.setDisplayName(lastPage ? "§cYou're on the last page." : "§aNext Page");
        if (lastPageItem.getItemMeta().getDisplayName().equals("§aNext page")) {
            SkullMeta head_meta = (SkullMeta) lastPageItemMeta;
            head_meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("74d11297-0fd9-4d43-91d9-8e5216a63efa")));
            lastPageItemMeta = head_meta;
        } else {
            SkullMeta head_meta = (SkullMeta) lastPageItemMeta;
            head_meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("03d2d48e-5d26-4460-8eaf-26b434b3f349")));
            lastPageItemMeta = head_meta;
        }

        lastPageItem.setItemMeta(lastPageItemMeta);

        inventory.setItem(49, backHead());
        inventory.setItem(46, nextPageItem);
        inventory.setItem(52, lastPageItem);

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
