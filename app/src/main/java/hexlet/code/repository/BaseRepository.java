package hexlet.code.repository;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.util.Database;

public abstract class BaseRepository {
    private final HikariDataSource dataSource;

    protected BaseRepository() {
        this(Database.getDataSource());
    }

    protected BaseRepository(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
