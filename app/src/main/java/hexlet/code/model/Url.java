package hexlet.code.model;

import java.sql.Timestamp;

public class Url {
    private final Long id;
    private final String name;
    private final Timestamp createdAt;

    public Url(Long id, String name, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
