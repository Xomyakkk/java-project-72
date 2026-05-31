package hexlet.code.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class UrlController {
    private static final String FLASH_KEY = "flash";
    private static final String FLASH_TYPE_KEY = "flashType";

    private final UrlRepository repository;
    private final UrlCheckRepository checkRepository;

    public UrlController(UrlRepository repository, UrlCheckRepository checkRepository) {
        this.repository = repository;
        this.checkRepository = checkRepository;
    }

    public void home(Context ctx) {
        renderIndex(ctx, "");
    }

    public void index(Context ctx) {
        renderUrls(ctx);
    }

    public void show(Context ctx) {
        Long id = parseId(ctx.pathParam("id"));
        loadUrl(id).ifPresentOrElse(url -> renderUrl(ctx, url), () -> ctx.status(404));
    }

    public void createCheck(Context ctx) {
        Long id = parseId(ctx.pathParam("id"));
        var url = loadUrl(id);
        if (url.isEmpty()) {
            ctx.status(404);
            return;
        }

        try {
            HttpResponse<String> response = Unirest.get(url.get().getName()).asString();
            if (response.getStatus() >= 400) {
                redirectWithFlash(ctx, url.get().getId(), "Произошла ошибка при проверке", "danger");
                return;
            }

            var document = Jsoup.parse(response.getBody() == null ? "" : response.getBody());
            var h1 = extractText(document.selectFirst("h1"));
            var title = document.title();
            var description = extractDescription(document);
            checkRepository.save(url.get().getId(), response.getStatus(), h1, title, description);
            redirectWithFlash(ctx, url.get().getId(), "Страница успешно проверена", "success");
        } catch (RuntimeException e) {
            redirectWithFlash(ctx, url.get().getId(), "Произошла ошибка при проверке", "danger");
        }
    }

    public void create(Context ctx) {
        var rawUrl = ctx.formParam("url");
        var normalizedUrl = normalizeUrl(rawUrl);
        if (normalizedUrl == null) {
            ctx.status(422);
            renderIndex(ctx, rawUrl == null ? "" : rawUrl.trim(), "Некорректный URL", "danger");
            return;
        }

        var existingUrl = repository.findByName(normalizedUrl);
        if (existingUrl.isPresent()) {
            ctx.sessionAttribute(FLASH_KEY, "Страница уже существует");
            ctx.sessionAttribute(FLASH_TYPE_KEY, "warning");
            ctx.redirect("/urls/" + existingUrl.get().getId());
            return;
        }

        var savedUrl = repository.save(normalizedUrl);
        ctx.sessionAttribute(FLASH_KEY, "Страница успешно добавлена");
        ctx.sessionAttribute(FLASH_TYPE_KEY, "success");
        ctx.redirect("/urls/" + savedUrl.getId());
    }

    private void renderIndex(Context ctx, String urlValue) {
        renderIndex(ctx, urlValue, null, null);
    }

    private void renderIndex(Context ctx, String urlValue, String inlineFlash, String inlineFlashType) {
        var model = baseModel(ctx);
        model.put("url", urlValue);
        if (inlineFlash != null) {
            model.put(FLASH_KEY, inlineFlash);
            model.put(FLASH_TYPE_KEY, inlineFlashType);
        }
        ctx.render("index.jte", model);
    }

    private void renderUrls(Context ctx) {
        var model = baseModel(ctx);
        var urls = repository.findAll();
        var latestChecks = new HashMap<Long, UrlCheck>();
        for (Url url : urls) {
            checkRepository.findLatestByUrlId(url.getId()).ifPresent(check -> latestChecks.put(url.getId(), check));
        }
        model.put("urls", urls);
        model.put("latestChecks", latestChecks);
        ctx.render("urls.jte", model);
    }

    private void renderUrl(Context ctx, Url url) {
        var model = baseModel(ctx);
        model.put("url", url);
        model.put("checks", checkRepository.findByUrlId(url.getId()));
        ctx.render("url.jte", model);
    }

    private Map<String, Object> baseModel(Context ctx) {
        var model = new HashMap<String, Object>();
        var flash = consumeFlash(ctx);
        if (flash != null) {
            model.put(FLASH_KEY, flash);
            var flashType = consumeFlashType(ctx);
            if (flashType != null) {
                model.put(FLASH_TYPE_KEY, flashType);
            }
        }
        return model;
    }

    private String consumeFlash(Context ctx) {
        String flash = ctx.sessionAttribute(FLASH_KEY);
        if (flash != null) {
            ctx.sessionAttribute(FLASH_KEY, (String) null);
        }
        return flash;
    }

    private String consumeFlashType(Context ctx) {
        String flashType = ctx.sessionAttribute(FLASH_TYPE_KEY);
        if (flashType != null) {
            ctx.sessionAttribute(FLASH_TYPE_KEY, (String) null);
        }
        return flashType;
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

    private void redirectWithFlash(Context ctx, Long id, String message, String flashType) {
        ctx.sessionAttribute(FLASH_KEY, message);
        ctx.sessionAttribute(FLASH_TYPE_KEY, flashType);
        ctx.redirect("/urls/" + id);
    }

    private Optional<Url> loadUrl(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return repository.findById(id);
    }

    private String extractText(Element element) {
        return element == null ? "" : element.text().trim();
    }

    private String extractDescription(Document document) {
        var description = document.selectFirst("meta[name=description]");
        if (description == null) {
            return "";
        }

        return description.attr("content").trim();
    }
}
