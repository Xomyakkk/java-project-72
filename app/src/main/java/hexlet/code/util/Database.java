package hexlet.code.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class Database {
    private static final String DEFAULT_H2_URL =
            "jdbc:h2:mem:_project_;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
    private static final HikariDataSource DATA_SOURCE = createDataSource();

    private Database() {
    }

    public static HikariDataSource getDataSource() {
        return DATA_SOURCE;
    }

    private static HikariDataSource createDataSource() {
        var settings = resolveConnectionSettings();
        var config = new HikariConfig();
        config.setJdbcUrl(settings.jdbcUrl());
        if (settings.username() != null) {
            config.setUsername(settings.username());
        }
        if (settings.password() != null) {
            config.setPassword(settings.password());
        }

        if (needsDefaultH2Credentials(settings)) {
            config.setUsername("sa");
            config.setPassword("");
        }

        var dataSource = new HikariDataSource(config);
        runSchema(dataSource);
        return dataSource;
    }

    private static ConnectionSettings resolveConnectionSettings() {
        var url = System.getenv("JDBC_DATABASE_URL");
        if (url == null || url.isBlank()) {
            url = System.getenv("DATABASE_URL");
        }

        if (url == null || url.isBlank()) {
            return new ConnectionSettings(DEFAULT_H2_URL, "sa", "");
        }

        if (url.startsWith("jdbc:")) {
            return new ConnectionSettings(url, null, null);
        }

        if (url.startsWith("postgresql://") || url.startsWith("postgres://")) {
            return parsePostgresUrl(url);
        }

        return new ConnectionSettings(url, null, null);
    }

    private static ConnectionSettings parsePostgresUrl(String url) {
        var uri = URI.create(url);
        var jdbcUrl = new StringBuilder("jdbc:postgresql://");
        jdbcUrl.append(uri.getHost());

        if (uri.getPort() > 0) {
            jdbcUrl.append(":").append(uri.getPort());
        }

        if (uri.getPath() != null) {
            jdbcUrl.append(uri.getPath());
        }

        if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
            jdbcUrl.append("?").append(uri.getQuery());
        }

        String username = null;
        String password = null;
        if (uri.getUserInfo() != null && !uri.getUserInfo().isBlank()) {
            var parts = uri.getUserInfo().split(":", 2);
            username = parts[0];
            if (parts.length > 1) {
                password = parts[1];
            }
        }

        return new ConnectionSettings(jdbcUrl.toString(), username, password);
    }

    private static boolean needsDefaultH2Credentials(ConnectionSettings settings) {
        return settings.jdbcUrl().startsWith("jdbc:h2:")
                && settings.username() == null
                && settings.password() == null;
    }

    private static void runSchema(HikariDataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             InputStream inputStream = Database.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (inputStream == null) {
                throw new IllegalStateException("schema.sql resource was not found");
            }

            var schema = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (!schema.isBlank()) {
                statement.execute(schema);
            }
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Unable to initialize database schema", e);
        }
    }

    private record ConnectionSettings(String jdbcUrl, String username, String password) {
    }
}
