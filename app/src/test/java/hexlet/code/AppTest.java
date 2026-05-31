package hexlet.code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;

import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.Database;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AppTest {

    private Javalin app;
    private UrlRepository urlRepository;
    private UrlCheckRepository urlCheckRepository;

    @BeforeEach
    void setUp() throws Exception {
        app = App.getApp();
        urlRepository = new UrlRepository();
        urlCheckRepository = new UrlCheckRepository();
        cleanTables();
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
        var saved = urlRepository.save("https://example.com");

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
        var url = urlRepository.save("https://example.com");

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertEquals(200, response.code());

            var body = response.body().string();
            assertTrue(body.contains("https://example.com"));
            assertTrue(body.contains("data-test=\"url\""));
            assertTrue(body.contains("data-test=\"checks\""));
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

            var saved = urlRepository.findByName("https://www.example.com");
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
            assertTrue(urlRepository.findAll().isEmpty());
        });
    }

    @Test
    void testCreateExistingUrlRedirectsToShowPage() {
        var existing = urlRepository.save("https://example.com");

        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=https://example.com/path");

            assertEquals(302, response.code());
            assertEquals("/urls/" + existing.getId(), response.headers().get("Location").get(0));
            assertEquals(1, urlRepository.findAll().size());
            assertTrue(urlRepository.findByName("https://example.com").isPresent());
        });
    }

    @Test
    void testCreateUrlCheckSuccess() throws Exception {
        try (MockWebServer mockWebServer = new MockWebServer()) {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("""
                            <!doctype html>
                            <html>
                            <head>
                                <title>Awesome page</title>
                                <meta name="description" content="%s">
                            </head>
                            <body>
                                <h1>Do not expect a miracle, miracles yourself!</h1>
                            </body>
                            </html>
                            """.formatted("x".repeat(230))));
            mockWebServer.start();

            var saved = urlRepository.save(mockWebServer.url("/").toString());

            JavalinTest.test(app, (server, client) -> {
                var response = client.post("/urls/" + saved.getId() + "/checks");
                assertEquals(302, response.code());
                assertEquals("/urls/" + saved.getId(), response.headers().get("Location").get(0));

                var showResponse = client.get("/urls/" + saved.getId());
                assertEquals(200, showResponse.code());

                var body = showResponse.body().string();
                assertTrue(body.contains("Страница успешно проверена"));
                assertTrue(body.contains("data-test=\"checks\""));
                assertTrue(body.contains("200"));
                assertTrue(body.contains("Awesome page"));
                assertTrue(body.contains("Do not expect a miracle, miracles yourself!"));
                assertTrue(body.contains("..."));

                var check = urlCheckRepository.findLatestByUrlId(saved.getId());
                assertTrue(check.isPresent());
                assertEquals(200, check.get().getStatusCode());
                assertTrue(check.get().getDescription().length() > 200);
            });
        }
    }

    @Test
    void testCreateUrlCheckFailure() throws Exception {
        try (MockWebServer mockWebServer = new MockWebServer()) {
            mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal error"));
            mockWebServer.start();

            var saved = urlRepository.save(mockWebServer.url("/").toString());

            JavalinTest.test(app, (server, client) -> {
                var response = client.post("/urls/" + saved.getId() + "/checks");
                assertEquals(302, response.code());
                assertEquals("/urls/" + saved.getId(), response.headers().get("Location").get(0));

                var showResponse = client.get("/urls/" + saved.getId());
                assertEquals(200, showResponse.code());
                assertTrue(showResponse.body().string().contains("Произошла ошибка при проверке"));
                assertTrue(urlCheckRepository.findByUrlId(saved.getId()).isEmpty());
            });
        }
    }

    @Test
    void testUrlsPageShowsLatestCheck() throws Exception {
        try (MockWebServer mockWebServer = new MockWebServer()) {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("""
                            <!doctype html>
                            <html>
                            <head>
                                <title>List page</title>
                                <meta name="description" content="List page description">
                            </head>
                            <body><h1>List heading</h1></body>
                            </html>
                            """));
            mockWebServer.start();

            var saved = urlRepository.save(mockWebServer.url("/").toString());

            JavalinTest.test(app, (server, client) -> {
                var response = client.post("/urls/" + saved.getId() + "/checks");
                assertEquals(302, response.code());

                var check = urlCheckRepository.findLatestByUrlId(saved.getId()).orElseThrow();
                var urlsPage = client.get("/urls");
                assertEquals(200, urlsPage.code());

                var body = urlsPage.body().string();
                assertTrue(body.contains("data-test=\"urls\""));
                assertTrue(body.contains(String.valueOf(check.getStatusCode())));
                assertTrue(body.contains(check.getCreatedAt().toLocalDateTime().toLocalDate().toString()));
            });
        }
    }

    private void cleanTables() throws Exception {
        try (Connection connection = Database.getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM url_checks");
            statement.executeUpdate("DELETE FROM urls");
        }
    }
}
