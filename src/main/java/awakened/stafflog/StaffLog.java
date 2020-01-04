package awakened.stafflog;

import awakened.stafflog.commands.AdminCommand;
import awakened.stafflog.commands.StaffModeCommand;
import awakened.stafflog.listeners.GUIDetection;
import awakened.stafflog.listeners.RegisterChatCommands;
import awakened.stafflog.storage.Database;
import awakened.stafflog.storage.DatabaseInitializer;
import net.luckperms.api.LuckPerms;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public final class StaffLog extends JavaPlugin {
    public static LuckPerms api;
    private PluginManager pluginManager;

    private Connection connection;
    public String table = getConfig().getString("stafflog.database.main-table");
    public String log_table = getConfig().getString("stafflog.database.log-table");

    private static StaffLog instance;

    private AdminCommand adminCommand;

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this);
        pluginManager = getServer().getPluginManager();
        loadConfig();
        startLuckPerms();
        DatabaseInitializer databaseInitializer = new DatabaseInitializer(this);
        databaseInitializer.setupMySQL();
        adminCommand = new AdminCommand(this, new Database(this));
        registerCommands();
        registerEvents();
        instance = this;
    }

    @Override
    public void onDisable() {
        getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "StaffLog has been disabled" + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        startLuckPerms();
        DatabaseInitializer databaseInitializer = new DatabaseInitializer(this);
        databaseInitializer.reloadDatabase();
    }

    public static StaffLog getInstance() {
        return instance;
    }

    private void startLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms api = provider.getProvider();
            StaffLog.api = api;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommands() {
        getCommand("staff-mode").setExecutor(new StaffModeCommand(new Database(this), this));
        getCommand("staff-log").setExecutor(adminCommand);
        registerTabCompleters();
    }

    @SuppressWarnings("ConstantConditions")
    private void registerTabCompleters() {
        getCommand("staff-log").setTabCompleter(adminCommand);
    }

    private void registerEvents() {
        pluginManager.registerEvents(new GUIDetection(new Database(this)), this);
        pluginManager.registerEvents(new RegisterChatCommands(new Database(this)), this);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void disableWithException(Exception e) {
        getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "An error occurred and the plugin has been disabled." + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
        e.printStackTrace();
        this.setEnabled(false);
    }

    public void disableWithError(String error) {
        getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + error + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
        this.setEnabled(false);
    }

    public static String getError(String error) {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(instance.getConfig().getString("stafflog.messages.errors." + error)));
    }

}
