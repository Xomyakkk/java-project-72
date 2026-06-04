package hexlet.code.model;

import java.time.LocalDateTime;

public class Url {
    private final Long id;
    private final String name;
    private LocalDateTime createdAt;

    public Url(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
