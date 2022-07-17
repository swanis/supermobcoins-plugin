package me.swanis.mobcoins.storage.impl;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.storage.Storable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLStorage implements Storable {

    private final MobCoins instance;
    private final MysqlDataSource dataSource;

    public MySQLStorage(MobCoins instance) {
        this.instance = instance;
        this.dataSource = new MysqlConnectionPoolDataSource();
    }

    @Override
    public boolean init() {
        dataSource.setServerName(Configuration.MYSQL_HOST);
        dataSource.setPort(Configuration.MYSQL_PORT);
        dataSource.setDatabaseName(Configuration.MYSQL_DATABASE);
        dataSource.setUser(Configuration.MYSQL_USER);
        dataSource.setPassword(Configuration.MYSQL_PASSWORD);

        String sql = "CREATE TABLE IF NOT EXISTS `supermobcoins` ( `uuid` CHAR(36) NOT NULL , `mobcoins` BIGINT DEFAULT 0 NOT NULL, PRIMARY KEY (uuid))";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {;
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void loadProfile(UUID uuid) {
        Profile profile = new Profile(uuid);

        String sql = "SELECT mobcoins FROM supermobcoins WHERE uuid = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                profile.setMobCoins(resultSet.getLong("mobcoins"));
            } else {
                profile.setMobCoins(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        instance.getProfileManager().load(profile);
    }

    @Override
    public void saveProfile(UUID uuid) {
        Profile profile = instance.getProfileManager().getProfile(uuid);

        if (profile == null) return;

        String sql = "REPLACE supermobcoins(uuid, mobcoins) VALUES (?, ?);";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setLong(2, profile.getMobCoins());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void set(UUID uuid, long mobCoins) {
        String sql = "REPLACE supermobcoins(uuid, mobcoins) VALUES (?, ?);";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setLong(2, mobCoins);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
