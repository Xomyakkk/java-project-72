package hexlet.code.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Function;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class Database {
    private static final String DEFAULT_H2_URL =
            "jdbc:h2:mem:_project_;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
    private static final Pattern ENV_PATTERN = Pattern.compile("\\$\\{([A-Z0-9_]+)\\}");
    private static final HikariDataSource DATA_SOURCE = createDataSource();

    private Database() {
    }

    public static HikariDataSource getDataSource() {
        return DATA_SOURCE;
    }

    private static HikariDataSource createDataSource() {
        var settings = resolveConnectionSettings(System.getenv("JDBC_DATABASE_URL"), System.getenv("DATABASE_URL"));
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

    static ConnectionSettings resolveConnectionSettings(String jdbcDatabaseUrl, String databaseUrl) {
        var url = jdbcDatabaseUrl;
        if (url == null || url.isBlank()) {
            url = databaseUrl;
        }

        if (url == null || url.isBlank()) {
            return new ConnectionSettings(DEFAULT_H2_URL, "sa", "");
        }

        url = expandEnvVariables(url, System::getenv);
        url = stripQuotes(url);

        if (url.startsWith("jdbc:")) {
            return new ConnectionSettings(url, null, null);
        }

        if (url.startsWith("postgresql://") || url.startsWith("postgres://")) {
            return parsePostgresUrl(url);
        }

        return new ConnectionSettings(url, null, null);
    }

    static String expandEnvVariables(String value, Function<String, String> envLookup) {
        var matcher = ENV_PATTERN.matcher(value);
        var buffer = new StringBuffer();

        while (matcher.find()) {
            var envValue = envLookup.apply(matcher.group(1));
            if (envValue == null) {
                envValue = "";
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(envValue));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    static String stripQuotes(String value) {
        if (value.length() >= 2
                && ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'")))) {
            return value.substring(1, value.length() - 1);
        }

        return value;
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

    static boolean needsDefaultH2Credentials(ConnectionSettings settings) {
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

    record ConnectionSettings(String jdbcUrl, String username, String password) {
    }
}
