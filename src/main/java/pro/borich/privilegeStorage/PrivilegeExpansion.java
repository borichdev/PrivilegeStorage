package pro.borich.privilegeStorage;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PrivilegeExpansion extends PlaceholderExpansion {
    private final PrivilegeStorage plugin;
    private final PrivilegeLogic PRIVILEGE;
    private final LuckPerms luckPerms;
    private final FileConfiguration CONFIG;
    private final Logger LOGGER;
    private final List<String> privilegeList;
    private final List<String> allowedPrivilegeList;

    // Кэш для привилегий (10 секунд)
    private final ConcurrentHashMap<String, CachedPrivilege> privilegeCache = new ConcurrentHashMap<>();
    private final long PRIVILEGE_CACHE_DURATION = TimeUnit.SECONDS.toMillis(10);

    // Кэш для количества привилегий (10 секунд)
    private final ConcurrentHashMap<String, CachedPrivilegeAmount> privilegeAmountCache = new ConcurrentHashMap<>();
    private final long AMOUNT_CACHE_DURATION = TimeUnit.SECONDS.toMillis(10);

    // Класс для кэширования привилегий
    private static class CachedPrivilege {
        private final String privilege;
        private final long timestamp;

        public CachedPrivilege(String privilege) {
            this.privilege = privilege;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isExpired(long cacheDuration) {
            return System.currentTimeMillis() - timestamp > cacheDuration;
        }

        public String getPrivilege() {
            return privilege;
        }
    }

    // Класс для кэширования количества привилегий
    private static class CachedPrivilegeAmount {
        private final int amount;
        private final long timestamp;

        public CachedPrivilegeAmount(int amount) {
            this.amount = amount;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isExpired(long cacheDuration) {
            return System.currentTimeMillis() - timestamp > cacheDuration;
        }

        public int getAmount() {
            return amount;
        }
    }

    public PrivilegeExpansion(PrivilegeStorage plugin) {
        this.plugin = plugin;
        this.PRIVILEGE = PrivilegeStorage.instance.PRIVILEGE;
        this.luckPerms = PrivilegeStorage.instance.luckperms;
        this.CONFIG = PrivilegeStorage.instance.CONFIG;
        this.LOGGER = PrivilegeStorage.instance.LOGGER;
        this.privilegeList = PRIVILEGE.privilegeList;
        this.allowedPrivilegeList = PRIVILEGE.allowedPrivilegeList;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "borich";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "privilege";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || player.getName() == null) {
            return null;
        }

        String[] args = params.split("_");

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("highest")) {
                return handleHighestPrivilegeRequest(player.getName(), allowedPrivilegeList);
            }
            return handlePrivilegeAmountRequest(player.getName(), args[0]);
        }

        return null;
    }

    private String getCachedHighestPrivilege(String playerName) {
        String cacheKey = "highest_" + playerName.toLowerCase();

        CachedPrivilege cached = privilegeCache.get(cacheKey);
        if (cached != null && !cached.isExpired(PRIVILEGE_CACHE_DURATION)) {
            return cached.getPrivilege();
        }

        String privilege = PRIVILEGE.getHighestAllowedPrivilege(playerName);
        privilegeCache.put(cacheKey, new CachedPrivilege(privilege));

        return privilege;
    }

    private int getCachedPrivilegeAmount(String playerName, String privilegeType) {
        String cacheKey = "amount_" + playerName.toLowerCase() + "_" + privilegeType.toLowerCase();

        CachedPrivilegeAmount cached = privilegeAmountCache.get(cacheKey);
        if (cached != null && !cached.isExpired(AMOUNT_CACHE_DURATION)) {
            return cached.getAmount();
        }

        int amount = PRIVILEGE.getPrivilegeAmount(playerName, privilegeType);
        privilegeAmountCache.put(cacheKey, new CachedPrivilegeAmount(amount));

        return amount;
    }

    public void invalidatePlayerCache(String playerName) {
        String playerKey = playerName.toLowerCase();

        privilegeCache.keySet().removeIf(key -> key.contains(playerKey));
        privilegeAmountCache.keySet().removeIf(key -> key.contains(playerKey));
    }

    public void clearCache() {
        privilegeCache.clear();
        privilegeAmountCache.clear();
    }

    private String handleHighestPrivilegeRequest(String playerName, List<String> groupNames) {
        String highestPrivilege = getCachedHighestPrivilege(playerName);
        return getGroupPrefix(highestPrivilege);
    }

    private String getGroupPrefix(String groupName) {
        try {
            Group group = luckPerms.getGroupManager().getGroup(groupName);
            if (group != null) {
                String prefix = group.getCachedData().getMetaData().getPrefix();
                if (prefix != null && !prefix.isEmpty()) {
                    prefix = ChatColor.translateAlternateColorCodes('&', prefix);
                    return prefix;
                }
            }
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
        }
        return groupName;
    }

    private String handlePrivilegeAmountRequest(String playerName, String privilegeType) {
        int amount = getCachedPrivilegeAmount(playerName, privilegeType);
        return Integer.toString(amount);
    }
}