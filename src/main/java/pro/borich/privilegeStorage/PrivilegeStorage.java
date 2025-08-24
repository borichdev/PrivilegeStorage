package pro.borich.privilegeStorage;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;


public final class PrivilegeStorage extends JavaPlugin {

    public static PrivilegeStorage instance;
    public Logger LOGGER;
    public FileConfiguration CONFIG;
    public LuckPerms luckperms;
    public Database DATABASE;
    public PrivilegeLogic PRIVILEGE;


    @Override
    public void onEnable() {

        instance = this;

        LOGGER = getLogger();
        LOGGER.log(Level.INFO, "Plugin enabled!");

        saveResource("config.yml", false);
        CONFIG = getConfig();
        LOGGER.log(Level.INFO, "CONFIG");


        DATABASE = new Database();

        PRIVILEGE = new PrivilegeLogic();


        try {
            LOGGER.log(Level.INFO, "Handshake with LuckPerms plugin...");
            luckperms = LuckPermsProvider.get();
            LOGGER.log(Level.INFO, "Done");
        } catch (IllegalStateException e) {
            LOGGER.log(Level.WARNING, "LuckPerms plugin not found!");
            LOGGER.log(Level.WARNING, "Be careful! Plugin won`t work correctly!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            LOGGER.log(Level.INFO, "Registering placeholders...");
            new PrivilegeExpansion(this).register();
            LOGGER.log(Level.INFO, "Done");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "PlaceholderAPI plugin not found!");
            LOGGER.log(Level.WARNING, "Be careful! Plugin won`t work correctly!");
            e.printStackTrace();
        }


        LOGGER.log(Level.INFO, "Registering commands...");
        this.getCommand("privilege").setExecutor(new PrivilegeCommand());
        this.getCommand("privilege").setTabCompleter(new PrivilegeCommand());
        LOGGER.log(Level.INFO, "Done");


        LOGGER.log(Level.INFO, "Registering listeners...");
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        LOGGER.log(Level.INFO, "Done");


        LOGGER.log(Level.INFO, "Plugin initialized succesfully!");
    }

    @Override
    public void onDisable() {
        LOGGER.log(Level.WARNING, ChatColor.RED + "Plugin is disabled");
    }

    public static PrivilegeStorage getInstance() {
        return instance;
    }

    public void reload() {
        saveResource("config.yml", false);
        CONFIG = getConfig();
        LOGGER.log(Level.INFO, "Successfully reloaded config.yml");
    }
}