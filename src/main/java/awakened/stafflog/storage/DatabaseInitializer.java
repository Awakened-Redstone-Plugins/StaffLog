package awakened.stafflog.storage;

import awakened.stafflog.StaffLog;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.fusesource.jansi.Ansi;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseInitializer{

    private StaffLog plugin;

    public DatabaseInitializer(StaffLog plugin) {
        this.plugin = plugin;
    }

    public void reloadDatabase() {
        if (plugin.getConnection() != null) {
            try {plugin.getConnection().close();} catch (SQLException ignored) {}
            setupMySQL();
        }
    }

    public void setupMySQL() {
        String host = plugin.getConfig().getString("stafflog.database.host");
        int port = plugin.getConfig().getInt("stafflog.database.port");
        String database = plugin.getConfig().getString("stafflog.database.database");
        String username = plugin.getConfig().getString("stafflog.database.username");
        String password = plugin.getConfig().getString("stafflog.database.password");

        try {

            synchronized (this) {
                if (plugin.getConnection() != null && !plugin.getConnection().isClosed()) {
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                plugin.setConnection(
                        DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false",
                                username, password));

                plugin.getConnection().prepareStatement("");

                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MYSQL CONNECTED");
            }
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().info(e.getMessage() + "\n");
            if (e.getMessage().contains("Unknown database")) {
                plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "Unable to find database!" + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
                if (plugin.getConfig().getBoolean("stafflog.options.database.auto-create-database")) {
                    plugin.getLogger().info(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "Creating database." + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
                    try {
                        plugin.setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/mysql" + "?useSSL=false",
                                username, password));

                        PreparedStatement statement = plugin.getConnection().prepareStatement("CREATE DATABASE IF NOT EXISTS `" + database + "`");
                        statement.execute();

                        plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString() + "Creating table." + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
                        plugin.setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false",
                                username, password));

                        statement = plugin.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `" + plugin.table +
                                "`(UUID TEXT NOT NULL, " +
                                "NAME TEXT NOT NULL, " +
                                "MODE BOOLEAN NOT NULL, " +
                                "LEVEL TEXT NOT NULL)");

                        statement.execute();

                        statement = plugin.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `" + plugin.log_table +
                                "`(UUID TEXT NOT NULL, " +
                                "NAME TEXT NOT NULL, " +
                                "MODE BOOLEAN NOT NULL, " +
                                "LEVEL TEXT NOT NULL)");

                        statement.execute();
                        return;
                    }
                    catch (SQLException exception) {plugin.disableWithException(exception); return;}
                }
                plugin.disableWithError("Unable to initialize MySql database");
                return;
            }
            plugin.disableWithException(e);
        }
    }
}
