package hexlet.code.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.javalin.http.Context;
import hexlet.code.repository.UrlRepository;

public class UrlController {
    private static final String FLASH_KEY = "flash";
    private final UrlRepository repository;

    public UrlController(UrlRepository repository) {
        this.repository = repository;
    }

    public void home(Context ctx) {
        renderIndex(ctx, "");
    }

    public void index(Context ctx) {
        renderUrls(ctx);
    }

    public void show(Context ctx) {
        Long id = parseId(ctx.pathParam("id"));
        if (id == null) {
            ctx.status(404);
            return;
        }

        repository.findById(id).ifPresentOrElse(url -> {
            var model = baseModel(ctx);
            model.put("url", url);
            ctx.render("url.jte", model);
        }, () -> {
            ctx.status(404);
        });
    }

    public void create(Context ctx) {
        var rawUrl = ctx.formParam("url");
        var normalizedUrl = normalizeUrl(rawUrl);
        if (normalizedUrl == null) {
            ctx.status(422);
            renderIndex(ctx, rawUrl == null ? "" : rawUrl.trim(), "Некорректный URL");
            return;
        }

        var existingUrl = repository.findByName(normalizedUrl);
        if (existingUrl.isPresent()) {
            ctx.sessionAttribute(FLASH_KEY, "Страница уже существует");
            ctx.redirect("/urls/" + existingUrl.get().getId());
            return;
        }

        var savedUrl = repository.save(normalizedUrl);
        ctx.sessionAttribute(FLASH_KEY, "Страница успешно добавлена");
        ctx.redirect("/urls/" + savedUrl.getId());
    }

    private void renderIndex(Context ctx, String urlValue) {
        renderIndex(ctx, urlValue, null);
    }

    private void renderIndex(Context ctx, String urlValue, String inlineFlash) {
        var model = baseModel(ctx);
        model.put("url", urlValue);
        if (inlineFlash != null) {
            model.put(FLASH_KEY, inlineFlash);
        }
        ctx.render("index.jte", model);
    }

    private void renderUrls(Context ctx) {
        var model = baseModel(ctx);
        model.put("urls", repository.findAll());
        ctx.render("urls.jte", model);
    }

    private Map<String, Object> baseModel(Context ctx) {
        var model = new HashMap<String, Object>();
        var flash = consumeFlash(ctx);
        if (flash != null) {
            model.put(FLASH_KEY, flash);
        }
        return model;
    }

    private String consumeFlash(Context ctx) {
        var flash = ctx.sessionAttribute(FLASH_KEY);
        if (flash != null) {
            ctx.sessionAttribute(FLASH_KEY, (String) null);
        }
        return flash;
    }

    private Long parseId(String rawId) {
        try {
            return Long.parseLong(rawId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalizeUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return null;
        }

        try {
            var url = URI.create(rawUrl.trim()).toURL();
            var host = url.getHost();
            if (host == null || host.isBlank()) {
                return null;
            }

            var normalized = new StringBuilder()
                    .append(url.getProtocol())
                    .append("://")
                    .append(host.toLowerCase(Locale.ROOT));

            if (url.getPort() != -1) {
                normalized.append(":").append(url.getPort());
            }

            return normalized.toString();
        } catch (IllegalArgumentException | MalformedURLException e) {
            return null;
        }
    }
}
