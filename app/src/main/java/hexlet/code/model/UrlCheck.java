package hexlet.code.model;

import java.sql.Timestamp;

public class UrlCheck {
    private final Long id;
    private final Long urlId;
    private final int statusCode;
    private final String h1;
    private final String title;
    private final String description;
    private final Timestamp createdAt;

    public UrlCheck(Long id, Long urlId, int statusCode, String h1, String title,
                    String description, Timestamp createdAt) {
        this.id = id;
        this.urlId = urlId;
        this.statusCode = statusCode;
        this.h1 = h1;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
