package hexlet.code.model;

import java.time.LocalDateTime;

public class UrlCheck {
    private final Long id;
    private final Long urlId;
    private final int statusCode;
    private final String h1;
    private final String title;
    private final String description;
    private LocalDateTime createdAt;

    public UrlCheck(Long id, Long urlId, int statusCode, String h1, String title,
                    String description) {
        this.id = id;
        this.urlId = urlId;
        this.statusCode = statusCode;
        this.h1 = h1;
        this.title = title;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Long getUrlId() {
        return urlId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getH1() {
        return h1;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
