package hexlet.code.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.UrlCheck;

public class UrlCheckRepository extends BaseRepository {
    public UrlCheckRepository() {
        super();
    }

    public UrlCheckRepository(HikariDataSource dataSource) {
        super(dataSource);
    }

    public List<UrlCheck> findByUrlId(Long urlId) {
        var sql = """
            SELECT id, url_id, status_code, h1, title, description, created_at
            FROM url_checks
            WHERE url_id = ?
            ORDER BY created_at DESC, id DESC
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, urlId);
            try (ResultSet resultSet = statement.executeQuery()) {
                var checks = new ArrayList<UrlCheck>();
                while (resultSet.next()) {
                    checks.add(map(resultSet));
                }
                return checks;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load URL checks", e);
        }
    }

    public Optional<UrlCheck> findLatestByUrlId(Long urlId) {
        var sql = """
            SELECT id, url_id, status_code, h1, title, description, created_at
            FROM url_checks
            WHERE url_id = ?
            ORDER BY created_at DESC, id DESC
            LIMIT 1
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, urlId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load latest URL check", e);
        }
    }

    public Map<Long, UrlCheck> findLatestChecks() {
        var sql = """
            SELECT id, url_id, status_code, h1, title, description, created_at
            FROM (
                SELECT
                    id,
                    url_id,
                    status_code,
                    h1,
                    title,
                    description,
                    created_at,
                    ROW_NUMBER() OVER (
                        PARTITION BY url_id
                        ORDER BY created_at DESC, id DESC
                    ) AS rn
                FROM url_checks
            ) AS ranked_checks
            WHERE rn = 1
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            var latestChecks = new HashMap<Long, UrlCheck>();
            while (resultSet.next()) {
                var check = map(resultSet);
                latestChecks.put(check.getUrlId(), check);
            }
            return latestChecks;
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load latest URL checks", e);
        }
    }

    public UrlCheck save(Long urlId, int statusCode, String h1, String title, String description) {
        var sql = """
            INSERT INTO url_checks (url_id, status_code, h1, title, description, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        var createdAt = Timestamp.valueOf(LocalDateTime.now());

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, urlId);
            statement.setInt(2, statusCode);
            statement.setString(3, h1);
            statement.setString(4, title);
            statement.setString(5, description);
            statement.setTimestamp(6, createdAt);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    var urlCheck = new UrlCheck(
                            generatedKeys.getLong(1),
                            urlId,
                            statusCode,
                            h1,
                            title,
                            description
                    );
                    urlCheck.setCreatedAt(createdAt.toLocalDateTime());
                    return urlCheck;
                }
            }

            throw new IllegalStateException("Unable to obtain generated URL check id");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to save URL check", e);
        }
    }

    private UrlCheck map(ResultSet resultSet) throws SQLException {
        var urlCheck = new UrlCheck(
                resultSet.getLong("id"),
                resultSet.getLong("url_id"),
                resultSet.getInt("status_code"),
                resultSet.getString("h1"),
                resultSet.getString("title"),
                resultSet.getString("description")
        );
        urlCheck.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        return urlCheck;
    }
}
