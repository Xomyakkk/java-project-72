package hexlet.code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;

import hexlet.code.repository.UrlRepository;
import hexlet.code.util.Database;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AppTest {

    private Javalin app;
    private UrlRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        app = App.getApp();
        repository = new UrlRepository();
        cleanUrls();
    }

    @Test
    void testHomePage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertEquals(200, response.code());

            var body = response.body().string();
            assertTrue(body.contains("action=\"/urls\""));
            assertTrue(body.contains("name=\"url\""));
            assertTrue(body.contains("placeholder=\"https://www.example.com\""));
        });
    }

    @Test
    void testUrlsPage() {
        var saved = repository.save("https://example.com");

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertEquals(200, response.code());

            var body = response.body().string();
            assertTrue(body.contains("data-test=\"urls\""));
            assertTrue(body.contains("https://example.com"));
            assertTrue(body.contains("/urls/" + saved.getId()));
        });
    }

    @Test
    void testShowUrlPage() {
        var url = repository.save("https://example.com");

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertEquals(200, response.code());

            var body = response.body().string();
            assertTrue(body.contains("https://example.com"));
            assertTrue(body.contains("data-test=\"url\""));
            assertTrue(body.contains(url.getCreatedAt().toLocalDateTime().toLocalDate().toString()));
        });
    }

    @Test
    void testShowUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");
            assertEquals(404, response.code());
        });
    }

    @Test
    void testShowUrlInvalidId() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/abc");
            assertEquals(404, response.code());
        });
    }

    @Test
    void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=https://www.Example.com/path?query=1");

            assertEquals(302, response.code());
            var location = response.headers().get("Location");
            assertFalse(location.isEmpty());

            var saved = repository.findByName("https://www.example.com");
            assertTrue(saved.isPresent());
            assertEquals("/urls/" + saved.get().getId(), location.get(0));
        });
    }

    @Test
    void testCreateUrlWithInvalidValue() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=not-a-url");
            assertEquals(422, response.code());
            assertTrue(response.body().string().contains("action=\"/urls\""));
            assertTrue(repository.findAll().isEmpty());
        });
    }

    @Test
    void testCreateExistingUrlRedirectsToShowPage() {
        var existing = repository.save("https://example.com");

        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=https://example.com/path");

            assertEquals(302, response.code());
            assertEquals("/urls/" + existing.getId(), response.headers().get("Location").get(0));
            assertEquals(1, repository.findAll().size());
            assertTrue(repository.findByName("https://example.com").isPresent());
        });
    }

    private void cleanUrls() throws Exception {
        try (Connection connection = Database.getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM urls");
        }
    }
}
