package hexlet.code;

import hexlet.code.util.Database;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.apibuilder.ApiBuilder.get;

public class App {
    private static final int DEFAULT_PORT = 7070;
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static Javalin getApp() {
        return Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
            config.routes.apiBuilder(() -> {
                get("/", ctx -> ctx.render("index.jte"));
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

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
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
