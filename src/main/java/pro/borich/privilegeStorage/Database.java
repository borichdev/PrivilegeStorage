package pro.borich.privilegeStorage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    private final FileConfiguration CONFIG = PrivilegeStorage.instance.CONFIG;
    private final HikariDataSource dataSource;
    private final String table;

    public Database() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + CONFIG.get("database.host") + ":" + CONFIG.get("database.port") + "/" + CONFIG.get("database.database"));
        config.setUsername(CONFIG.get("database.user").toString());
        config.setPassword(CONFIG.get("database.password").toString());
        this.table = CONFIG.get("database.table").toString();

        config.setMaximumPoolSize(Integer.valueOf(CONFIG.get("database.maximumPoolSize").toString()));
        config.setConnectionTimeout(Integer.valueOf(CONFIG.get("database.connectionTimeout").toString()));
        config.setIdleTimeout(Integer.valueOf(CONFIG.get("database.connectionTimeout").toString()));
        config.setMaxLifetime(Integer.valueOf(CONFIG.get("database.maxLifetime").toString()));

        this.dataSource = new HikariDataSource(config);

        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " (" +
                "playername VARCHAR(16) PRIMARY KEY, " +
                "data TEXT)";
        executeUpdate(sql);
    }

    public ArrayList<String> executeQuery(String query, String column) {
        ArrayList<String> resultList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                resultList.add(result.getString(column));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public boolean executeUpdate(String query) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}