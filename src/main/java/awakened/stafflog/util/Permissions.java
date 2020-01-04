package awakened.stafflog.util;

import awakened.stafflog.StaffLog;
import awakened.stafflog.storage.Database;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;

import java.util.UUID;

public class Permissions {

    private LuckPerms luckPerms = StaffLog.api;
    private Database database;

    public Permissions(Database database) {
        super();
        this.database = database;
    }

    public void setPermissions(UUID uuid, boolean mode) {
        User user = luckPerms.getUserManager().getUser(uuid);
        String level = database.getLevel(uuid);
        Group RawGroup = luckPerms.getGroupManager().getGroup(level);

        assert user != null;
        assert RawGroup != null;

        String group = RawGroup.getName();

        if (mode) {
            user.data().add(InheritanceNode.builder(group).build());
        } else {
            user.data().remove(InheritanceNode.builder(group).build());
        }

        luckPerms.getUserManager().saveUser(user);
    }
}
