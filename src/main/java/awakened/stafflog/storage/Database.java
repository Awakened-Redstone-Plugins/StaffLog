package awakened.stafflog.storage;

import awakened.stafflog.StaffLog;
import awakened.stafflog.util.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class Database {

    private StaffLog plugin;
    private Permissions permissions = new Permissions(this);

    public Database(StaffLog plugin) {
        super();
        this.plugin = plugin;
    }

    private boolean playerExists(UUID uuid) {
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM `" + plugin.table + "` WHERE UUID=?");
            statement.setString(1, uuid.toString());

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createPlayer(final UUID uuid, OfflinePlayer player) {
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM `" + plugin.table + "` WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            if (!playerExists(uuid)) {
                PreparedStatement insert = plugin.getConnection()
                        .prepareStatement("INSERT INTO `" + plugin.table + "` (UUID,NAME,MODE,LEVEL) VALUES (?,?,?,?)");
                insert.setString(1, uuid.toString());
                insert.setString(2, player.getName());
                insert.setBoolean(3, false);
                insert.setString(4, "null");
                insert.executeUpdate();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMode(UUID uuid) {
        boolean newMode = !getMode(uuid);
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("UPDATE `" + plugin.table + "` SET MODE=? WHERE UUID=?");
            statement.setBoolean(1, newMode);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            permissions.setPermissions(uuid, getMode(uuid));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getMode(UUID uuid) {
        boolean staff = false;
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM `" + plugin.table + "` WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();

            staff = (results.getBoolean("MODE"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public void setLevel(UUID uuid, String group) {
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("UPDATE `" + plugin.table + "` SET LEVEL=? WHERE UUID=?");
            statement.setString(1, group);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getLevel(UUID uuid) {
        String level = null;
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM `" + plugin.table + "` WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();

            level = (results.getString("LEVEL"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return level;
    }

    public void removePlayer(UUID uuid) {
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("DELETE FROM `" + plugin.table + "` WHERE UUID=?");
            statement.setString(1, uuid.toString());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasPlayer(String name) {
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM `" + plugin.table + "` WHERE NAME=?");
            statement.setString(1, name);

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasPlayer(UUID uuid) {
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM `" + plugin.table + "` WHERE UUID=?");
            statement.setString(1, uuid.toString());

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public UUID getPlayer(String name) {
        UUID uuid = null;
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM `" + plugin.table + "` WHERE NAME=?");
            statement.setString(1, name);

            ResultSet results = statement.executeQuery();
            results.next();

            uuid = UUID.fromString(results.getString("UUID"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uuid;
    }

    public String getPlayer(UUID uuid) {
        String name = null;
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM `" + plugin.table + "` WHERE UUID=?");
            statement.setString(1, uuid.toString());

            ResultSet results = statement.executeQuery();
            results.next();

            name = results.getString("NAME");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    public void logCommand(final UUID uuid, Player player, String command) {
        try {
            PreparedStatement insert = plugin.getConnection()
                        .prepareStatement("INSERT INTO `" + plugin.log_table + "` (NAME,UUID,COMMAND,TIME) VALUES (?,?,?,?)");
            insert.setString(1, player.getName());
            insert.setString(2, uuid.toString());
            insert.setString(3, command);
            Date date = new Date();
            insert.setTimestamp(4, new Timestamp(date.getTime()));
                insert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<OfflinePlayer> getPlayers() {
        List<OfflinePlayer> names = new ArrayList<>();
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM `" + plugin.table + "`");

            ResultSet results = statement.executeQuery();

            while(results.next()){
                names.add(Bukkit.getOfflinePlayer(getPlayer(results.getString("NAME"))));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    public List<String> getLog(String player) {
         List<String> log = new ArrayList<>();
         String command = null;
         String time = null;
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM `" + plugin.log_table + "` WHERE NAME=?");
            statement.setString(1, player);
            ResultSet results = statement.executeQuery();

            while(results.next()){
                command = results.getString("COMMAND");
                time = results.getTimestamp("TIME").toString();
                log.add("§b" + command + " §3> §c" + time);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return log;
    }

}
