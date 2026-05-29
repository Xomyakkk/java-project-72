package hexlet.code.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;

public class UrlRepository extends BaseRepository {
    public UrlRepository() {
        super();
    }

    public UrlRepository(HikariDataSource dataSource) {
        super(dataSource);
    }

    public List<Url> findAll() {
        var sql = """
            SELECT id, name, created_at
            FROM urls
            ORDER BY created_at DESC, id DESC
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            var urls = new ArrayList<Url>();
            while (resultSet.next()) {
                urls.add(map(resultSet));
            }
            return urls;
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load URLs", e);
        }
    }

    public Optional<Url> findById(Long id) {
        var sql = """
            SELECT id, name, created_at
            FROM urls
            WHERE id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load URL by id", e);
        }
    }

    public Optional<Url> findByName(String name) {
        var sql = """
            SELECT id, name, created_at
            FROM urls
            WHERE name = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load URL by name", e);
        }
    }

    public Url save(String name) {
        var sql = """
            INSERT INTO urls (name, created_at)
            VALUES (?, ?)
            """;

        var createdAt = Timestamp.valueOf(LocalDateTime.now());

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setTimestamp(2, createdAt);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Url(generatedKeys.getLong(1), name, createdAt);
                }
            }

            throw new IllegalStateException("Unable to obtain generated URL id");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to save URL", e);
        }
    }

    private Url map(ResultSet resultSet) throws SQLException {
        return new Url(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("created_at")
        );
    }
}
