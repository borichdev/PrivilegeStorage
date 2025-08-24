package pro.borich.privilegeStorage;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.logging.Logger;

public class PrivilegeLogic {
    public final Logger LOGGER;
    public final FileConfiguration CONFIG;
    public final Database DATABASE;
    public ArrayList<String> privilegeList;
    public ArrayList<String> allowedPrivilegeList;
    public final String TABLE;
    private final LuckPerms luckPerms;

    public PrivilegeLogic(){
        LOGGER = PrivilegeStorage.instance.LOGGER;
        CONFIG = PrivilegeStorage.instance.CONFIG;
        DATABASE = PrivilegeStorage.instance.DATABASE;

        luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();

        privilegeList = new ArrayList<>();
        allowedPrivilegeList = new ArrayList<>();

        if (luckPerms != null) {
            List<net.luckperms.api.model.group.Group> groups = new ArrayList<>();
            luckPerms.getGroupManager().getLoadedGroups().forEach(groups::add);

            groups.sort(Comparator.comparingInt(group -> group.getWeight().orElse(0)));

            for (net.luckperms.api.model.group.Group group : groups) {
                privilegeList.add(group.getName());
            }
        }
        Collections.reverse(privilegeList);
        privilegeList.remove("default");

        String exclude = CONFIG.getString("luckperms.exclude", "");
        allowedPrivilegeList.addAll(privilegeList);
        allowedPrivilegeList.removeAll(Arrays.asList(exclude.split(" ")));

        TABLE = CONFIG.getString("database.table", "privilege");
    }

    public boolean deletePlayer(String playername) {
        boolean rs = false;

        if (playerExists(playername)) {
            rs = DATABASE.executeUpdate("DELETE FROM " + TABLE + " WHERE playername = '" + playername + "'");
        }
        return rs;
    }

    public boolean registerPlayer(String playername) {
        boolean rs = false;

        if (!playerExists(playername)) {
            rs = DATABASE.executeUpdate("INSERT INTO " + TABLE + " VALUES ('" + playername +"', '')");
        }
        return rs;
    }

    public boolean playerExists(String playername) {
        ArrayList<String> rs = DATABASE.executeQuery("SELECT playername FROM " + TABLE, "playername");
        return rs.contains(playername);
    }

    public int getPrivilegeAmount(String playername, String type) {
        int amt = 0;

        ArrayList<String> rs = DATABASE.executeQuery("SELECT data FROM " + TABLE + " WHERE playername = '" + playername + "'", "data");

        if (rs.isEmpty()) {
            return 0;
        }

        String data = rs.get(0);
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        if (data != null && !data.equals("")) {
            String[] pairs = data.split(" ");

            for (String pair : pairs) {
                String[] keyValue = pair.split(":");

                if (keyValue.length == 2) {
                    String title = keyValue[0];
                    int count = 0;
                    try {
                        count = Integer.parseInt(keyValue[1]);
                    } catch (Exception e) {
                        count = 0;
                    }
                    map.put(title, count);
                }
            }
        }

        if (map.containsKey(type)) {
            amt = map.get(type);
        }

        return amt;
    }

    public boolean setPrivilegeAmount(String playername, String type, int count) {
        ArrayList<String> rs = DATABASE.executeQuery("SELECT data FROM " + TABLE + " WHERE playername = '" + playername + "'", "data");

        String data = "";
        if (!rs.isEmpty()) {
            data = rs.get(0);
        }

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        if (data != null && !data.isEmpty()) {
            String[] pairs = data.split(" ");

            for (String pair : pairs) {
                String[] keyValue = pair.split(":");

                if (keyValue.length == 2) {
                    String title = keyValue[0];
                    int amount = 0;
                    try {
                        amount = Integer.parseInt(keyValue[1]);
                    } catch (Exception e) {
                        amount = 0;
                    }
                    map.put(title, amount);
                }
            }
        }

        if (map.containsKey(type)) {
            if (count != 0) {
                map.replace(type, count);
            } else {
                map.remove(type);
            }
        } else if (count != 0) {
            map.put(type, count);
        }

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            result.append(entry.getKey()).append(':').append(entry.getValue()).append(' ');
        }

        String finalData = result.toString().trim();
        boolean success = DATABASE.executeUpdate("UPDATE " + TABLE + " SET data = '" + finalData + "' WHERE playername = '" + playername + "'");

        return success;
    }

    public String getHighestPrivilege(String playername){
        String result = "";

        for (String priv : privilegeList) {
            if (getPrivilegeAmount(playername, priv) > 0){
                result = priv;
                break;
            }
        }

        if (result.equals("")) {
            result = "default";
        }
        return result;
    }

    public String getHighestAllowedPrivilege(String playername){
        String result = "";

        for (String priv : allowedPrivilegeList) {
            if (getPrivilegeAmount(playername, priv) > 0){
                result = priv;
                break;
            }
        }

        if (result.equals("")) {
            result = "default";
        }
        return result;
    }

    public ArrayList<String> getPrivilegeList() {
        return privilegeList;
    }

    public ArrayList<String> getAllowedPrivilegeList() {
        return allowedPrivilegeList;
    }
}