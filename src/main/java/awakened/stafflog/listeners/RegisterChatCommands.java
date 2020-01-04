package awakened.stafflog.listeners;

import awakened.stafflog.storage.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class RegisterChatCommands implements Listener {

    private Database database;

    public RegisterChatCommands(Database database) {
        super();
        this.database = database;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!database.hasPlayer(e.getPlayer().getUniqueId())) return;
        if (!database.getMode(e.getPlayer().getUniqueId())) return;
        String command = e.getMessage();
        database.logCommand(e.getPlayer().getUniqueId(), e.getPlayer(), command);
    }
}
