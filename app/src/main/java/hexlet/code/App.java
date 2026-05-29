package hexlet.code;

import hexlet.code.util.Database;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.apibuilder.ApiBuilder.get;

public class App {
    private static final int DEFAULT_PORT = 7070;
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static Javalin getApp() {
        return Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.routes.apiBuilder(() -> {
                get("/", ctx -> ctx.result("Hello World"));
            });
        });
    }

    public static void main(String[] args) {
        var port = getPort();
        Database.getDataSource();
        var app = getApp();
        LOGGER.info("Starting application on port {}", port);
        app.start(port);
    }

    private static int getPort() {
        var port = System.getenv("PORT");
        if (port == null || port.isBlank()) {
            return DEFAULT_PORT;
        }

        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid PORT value '{}', using default {}", port, DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }
}
