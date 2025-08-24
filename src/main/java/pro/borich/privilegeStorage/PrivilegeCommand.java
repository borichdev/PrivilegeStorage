package pro.borich.privilegeStorage;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getServer;

public class PrivilegeCommand implements CommandExecutor, TabCompleter {

    private final PrivilegeLogic PRIVILEGE;
    private FileConfiguration CONFIG;
    private final Logger LOGGER;
    private final List<String> privilegeList;
    private final List<String> allowedPrivilegeList;
    private final LuckPerms luckPerms;

    public PrivilegeCommand() {
        PRIVILEGE = PrivilegeStorage.instance.PRIVILEGE;
        CONFIG = PrivilegeStorage.instance.CONFIG;
        LOGGER = PrivilegeStorage.instance.LOGGER;
        luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        privilegeList = PRIVILEGE.privilegeList;
        allowedPrivilegeList = PRIVILEGE.allowedPrivilegeList;
    }

    private void send(String target, String messagePath, String playerName, String priv, int count) {
        if (Bukkit.getOfflinePlayer(target).isOnline()) {
            Player player = Bukkit.getPlayer(target);
            String message = prepareMessage(CONFIG.getString(messagePath, ""), playerName, priv, count);

            if (CONFIG.isString(messagePath)) {
                player.sendMessage(message);
            } else {
                List<String> messages = CONFIG.getStringList(messagePath);
                for (String msg : messages) {
                    player.sendMessage(prepareMessage(msg, playerName, priv, count));
                }
            }
        }
    }

    private void send(CommandSender sender, String messagePath, String playerName, String priv, int count) {
        String message = prepareMessage(CONFIG.getString(messagePath, ""), playerName, priv, count);

        if (CONFIG.isString(messagePath)) {
            sender.sendMessage(message);
        } else {
            List<String> messages = CONFIG.getStringList(messagePath);
            for (String msg : messages) {
                sender.sendMessage(prepareMessage(msg, playerName, priv, count));
            }
        }
    }

    private String prepareMessage(String input, String playerName, String priv, int count) {
        if (input == null || input.isEmpty()) return "";

        String maskedPriv = getGroupPrefix(priv);

        String prefixPlaceholder = CONFIG.getString("messages.placeholders.prefix", "%PREFIX%");
        String playerPlaceholder = CONFIG.getString("messages.placeholders.player", "%PLAYER%");
        String privPlaceholder = CONFIG.getString("messages.placeholders.privilege", "%PRIV%");
        String countPlaceholder = CONFIG.getString("messages.placeholders.count", "%COUNT%");

        String prefix = CONFIG.getString("messages.prefix", "&bПривилегии &8>>");

        return ChatColor.translateAlternateColorCodes('&', input
                .replace(prefixPlaceholder, prefix)
                .replace(playerPlaceholder, playerName != null ? playerName : "")
                .replace(privPlaceholder, maskedPriv)
                .replace(countPlaceholder, String.valueOf(count)));
    }

    private void broadcastGive(String playerName, String playerName2, String priv) {
        String maskedPriv = getGroupPrefix(priv);

        String prefixPlaceholder = CONFIG.getString("messages.placeholders.prefix", "%PREFIX%");
        String playerPlaceholder = CONFIG.getString("messages.placeholders.player", "%PLAYER%");
        String player2Placeholder = CONFIG.getString("messages.placeholders.player2", "%PLAYER2%");
        String privPlaceholder = CONFIG.getString("messages.placeholders.privilege", "%PRIV%");

        String prefix = CONFIG.getString("messages.prefix", "&bПривилегии &8>>");

        List<String> messages = CONFIG.getStringList("messages.give");
        for (String messageTemplate : messages) {
            String message = ChatColor.translateAlternateColorCodes('&', messageTemplate
                    .replace(prefixPlaceholder, prefix)
                    .replace(playerPlaceholder, playerName)
                    .replace(player2Placeholder, playerName2)
                    .replace(privPlaceholder, maskedPriv));

            for (Player target : Bukkit.getOnlinePlayers()) {
                target.sendMessage(message);
            }
        }
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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            send(sender, "messages.help", "", "", 0);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                if (sender.isOp()) {
                    PrivilegeStorage.instance.saveResource("config.yml", false);
                    PrivilegeStorage.instance.CONFIG = PrivilegeStorage.instance.getConfig();
                    CONFIG = PrivilegeStorage.instance.CONFIG;
                    send(sender, "messages.reloaded", "", "", 0);
                }
                break;

            case "help":
                send(sender, "messages.help", "", "", 0);
                break;

            case "list":
                handleListCommand(sender, args);
                break;

            case "give":
                handleGiveCommand(sender, args);
                break;

            case "add":
                handleAddCommand(sender, args);
                break;

            case "take":
                handleTakeCommand(sender, args);
                break;

            case "set":
                handleSetCommand(sender, args);
                break;

            case "reset":
                handleResetCommand(sender, args);
                break;

            default:
                send(sender, "messages.unknown-command", "", "", 0);
                break;
        }
        return true;
    }

    private void handleListCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            send(sender, "messages.you-have-header", sender.getName(), "", 0);
            for (String priv : allowedPrivilegeList) {
                int amount = PRIVILEGE.getPrivilegeAmount(sender.getName(), priv);
                send(sender, "messages.you-have-privilege", sender.getName(), priv, amount);
            }
        } else if (sender.isOp() || sender.hasPermission("privilege.list.others")) {
            String target = args[1];
            if (PRIVILEGE.playerExists(target)) {
                send(sender, "messages.player-has-header", target, "", 0);
                for (String priv : allowedPrivilegeList) {
                    int amount = PRIVILEGE.getPrivilegeAmount(target, priv);
                    send(sender, "messages.player-has-privilege", target, priv, amount);
                }
            } else {
                send(sender, "messages.player-not-found", "", "", 0);
            }
        } else {
            send(sender, "messages.no-permission", "", "", 0);
        }
    }

    private void handleGiveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("privilege.give")) {
            send(sender, "messages.no-permission", "", "", 0);
            return;
        }

        if (args.length != 3) {
            send(sender, "messages.invalid-arguments", "", "", 0);
            send(sender, "messages.usage-give", "", "", 0);
            return;
        }

        String target = args[1];
        String type = args[2];

        if (!PRIVILEGE.playerExists(target) || target.equalsIgnoreCase(sender.getName())) {
            send(sender, "messages.player-not-found", "", "", 0);
            return;
        }

        if (!allowedPrivilegeList.contains(type)) {
            send(sender, "messages.unknown-privilege-type", "", type, 0);
            return;
        }

        int senderAmount = PRIVILEGE.getPrivilegeAmount(sender.getName(), type);
        if (senderAmount <= 0) {
            send(sender, "messages.you-have-not-enough-privileges", sender.getName(), type, 0);
            return;
        }

        if (PRIVILEGE.getHighestAllowedPrivilege(sender.getName()).equalsIgnoreCase(type) &&
                PRIVILEGE.getPrivilegeAmount(sender.getName(), type) == 1) {
            send(sender, "messages.you-cant-give-used-privilege", "", "", 0);
            return;
        }

        int targetAmount = PRIVILEGE.getPrivilegeAmount(target, type);
        PRIVILEGE.setPrivilegeAmount(sender.getName(), type, senderAmount - 1);
        PRIVILEGE.setPrivilegeAmount(target, type, targetAmount + 1);

        broadcastGive(sender.getName(), target, type);

        String server = CONFIG.getString("server", "global");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "lp user " + target + " parent add " + PRIVILEGE.getHighestPrivilege(target) + " server=" + server);

        send(sender, "messages.you-gave-privilege", target, type, 1);
        send(sender, "messages.you-now-have-privileges", sender.getName(), type, senderAmount - 1);
        send(target, "messages.player-gave-privilege", sender.getName(), type, 1);
        send(target, "messages.you-now-have-privileges", target, type, targetAmount + 1);

        LOGGER.log(Level.INFO, sender.getName() + ": GIVE '" + type + "' to " + target);
    }

    private void handleAddCommand(CommandSender sender, String[] args) {
        if (!sender.isOp() && sender instanceof Player) {
            send(sender, "messages.no-permission", "", "", 0);
            return;
        }

        if (args.length != 4) {
            send(sender, "messages.invalid-arguments", "", "", 0);
            send(sender, "messages.usage-add", "", "", 0);
            return;
        }

        String target = args[1];
        String type = args[2];
        int count;

        try {
            count = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            send(sender, "messages.invalid-count", "", "", 0);
            return;
        }

        if (count <= 0) {
            send(sender, "messages.invalid-count", "", "", 0);
            return;
        }

        if (!PRIVILEGE.playerExists(target)) {
            send(sender, "messages.player-not-found", "", "", 0);
            return;
        }

        int targetAmount = PRIVILEGE.getPrivilegeAmount(target, type);
        PRIVILEGE.setPrivilegeAmount(target, type, targetAmount + count);

        String server = CONFIG.getString("server", "global");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "lp user " + target + " parent add " + PRIVILEGE.getHighestPrivilege(target) + " server=" + server);

        send(sender, "messages.you-added-privilege", target, type, count);
        send(target, "messages.admin-added-privilege", sender.getName(), type, count);

        LOGGER.log(Level.INFO, sender.getName() + ": ADD '" + type + "' : " + count + " to " + target);
    }

    private void handleTakeCommand(CommandSender sender, String[] args) {
        if (!sender.isOp() && sender instanceof Player) {
            send(sender, "messages.no-permission", "", "", 0);
            return;
        }

        if (args.length != 4) {
            send(sender, "messages.invalid-arguments", "", "", 0);
            send(sender, "messages.usage-take", "", "", 0);
            return;
        }

        String target = args[1];
        String type = args[2];
        int count;

        try {
            count = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            send(sender, "messages.invalid-count", "", "", 0);
            return;
        }

        if (count <= 0) {
            send(sender, "messages.invalid-count", "", "", 0);
            return;
        }

        if (!PRIVILEGE.playerExists(target)) {
            send(sender, "messages.player-not-found", "", "", 0);
            return;
        }

        int targetAmount = PRIVILEGE.getPrivilegeAmount(target, type);
        if (targetAmount < count) {
            send(sender, "messages.player-has-not-enough-privileges", target, type, 0);
            return;
        }

        String server = CONFIG.getString("server", "global");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "lp user " + target + " parent remove " + PRIVILEGE.getHighestPrivilege(target) + " server=" + server);

        PRIVILEGE.setPrivilegeAmount(target, type, targetAmount - count);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "lp user " + target + " parent add " + PRIVILEGE.getHighestPrivilege(target) + " server=" + server);

        send(sender, "messages.you-took-privilege", target, type, count);
        send(target, "messages.admin-took-privilege", sender.getName(), type, count);

        LOGGER.log(Level.INFO, sender.getName() + ": TAKE '" + type + "' : " + count + " from " + target);
    }

    private void handleSetCommand(CommandSender sender, String[] args) {
        if (!sender.isOp() && sender instanceof Player) {
            send(sender, "messages.no-permission", "", "", 0);
            return;
        }

        if (args.length != 4) {
            send(sender, "messages.invalid-arguments", "", "", 0);
            send(sender, "messages.usage-set", "", "", 0);
            return;
        }

        String target = args[1];
        String type = args[2];
        int count;

        try {
            count = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            send(sender, "messages.invalid-count", "", "", 0);
            return;
        }

        if (count < 0) {
            send(sender, "messages.invalid-count", "", "", 0);
            return;
        }

        if (!PRIVILEGE.playerExists(target)) {
            send(sender, "messages.player-not-found", "", "", 0);
            return;
        }

        PRIVILEGE.setPrivilegeAmount(target, type, count);

        String server = CONFIG.getString("server", "global");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "lp user " + target + " parent add " + PRIVILEGE.getHighestPrivilege(target) + " server=" + server);

        send(sender, "messages.you-set-privilege", target, type, count);
        send(target, "messages.admin-set-privilege", sender.getName(), type, count);

        LOGGER.log(Level.INFO, sender.getName() + ": SET '" + type + "' : " + count + " to " + target);
    }

    private void handleResetCommand(CommandSender sender, String[] args) {
        if (!sender.isOp() && sender instanceof Player) {
            send(sender, "messages.no-permission", "", "", 0);
            return;
        }

        if (args.length != 2) {
            send(sender, "messages.invalid-arguments", "", "", 0);
            return;
        }

        String target = args[1];
        if (!PRIVILEGE.playerExists(target)) {
            send(sender, "messages.player-not-found", "", "", 0);
            return;
        }

        PRIVILEGE.deletePlayer(target);
        PRIVILEGE.registerPlayer(target);

        String server = CONFIG.getString("server", "global");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "lp user " + target + " parent set default server=" + server);

        send(sender, "messages.you-reset-privilege", target, "default", 0);

        LOGGER.log(Level.INFO, sender.getName() + ": RESET " + target);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("privilege") && !cmd.getName().equalsIgnoreCase("priv")) {
            return null;
        }

        if (args.length == 1) {
            if (sender.isOp()) {
                return suggestCompletions(args[0], "help", "list", "give", "add", "take", "set", "reload", "reset");
            } else {
                return suggestCompletions(args[0], "help", "list", "give");
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if ((sender.isOp() || sender.hasPermission("privilege.list.others") || sender.hasPermission("privilege.give")) &&
                    (subCommand.equals("list") || subCommand.equals("give") || subCommand.equals("add") ||
                            subCommand.equals("take") || subCommand.equals("set") || subCommand.equals("reset"))) {
                return suggestPlayers(args[1]);
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if ((sender.isOp() || sender.hasPermission("privilege.give")) &&
                    (subCommand.equals("give") || subCommand.equals("add") || subCommand.equals("take") || subCommand.equals("set"))) {
                return suggestPrivileges(args[2]);
            }
        } else if (args.length == 4) {
            String subCommand = args[0].toLowerCase();
            if (sender.isOp() && (subCommand.equals("add") || subCommand.equals("take") || subCommand.equals("set"))) {
                return suggestCompletions(args[3], "1", "0");
            }
        }

        return Collections.emptyList();
    }

    private List<String> suggestCompletions(String partial, String... completions) {
        List<String> matches = new ArrayList<>();
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(partial.toLowerCase())) {
                matches.add(completion);
            }
        }
        return matches;
    }

    private List<String> suggestPlayers(String partial) {
        List<String> players = new ArrayList<>();
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            String playerName = onlinePlayer.getName();
            if (playerName.toLowerCase().startsWith(partial.toLowerCase())) {
                players.add(playerName);
            }
        }
        return players;
    }

    private List<String> suggestPrivileges(String partial) {
        List<String> matches = new ArrayList<>();
        for (String privilege : allowedPrivilegeList) {
            if (privilege.toLowerCase().startsWith(partial.toLowerCase())) {
                matches.add(privilege);
            }
        }
        return matches;
    }
}