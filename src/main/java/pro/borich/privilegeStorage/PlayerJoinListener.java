package pro.borich.privilegeStorage;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.logging.Level;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String server = PrivilegeStorage.instance.CONFIG.get("server").toString();
        String player = event.getPlayer().getName();
        if (PrivilegeStorage.instance.PRIVILEGE.playerExists(player)) {
            String group = PrivilegeStorage.instance.PRIVILEGE.getHighestPrivilege(player);
            if (!group.equals("")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player + " parent add " + group + " server=" + server);
                PrivilegeStorage.instance.LOGGER.log(Level.INFO, "Setting " + player + " group '" + group + "'");
            } else {
                PrivilegeStorage.instance.LOGGER.log(Level.INFO, "Setting " + player + " group 'default'");
            }
        } else {
            PrivilegeStorage.instance.PRIVILEGE.registerPlayer(player);
            PrivilegeStorage.instance.LOGGER.log(Level.INFO, "Setting " + player + " group 'default'");
        }
    }
}
