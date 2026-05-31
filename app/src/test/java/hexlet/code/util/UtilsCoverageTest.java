package hexlet.code.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class UtilsCoverageTest {

    @Test
    void truncateHandlesNullShortAndLongStrings() {
        assertEquals("", TextUtils.truncate(null));
        assertEquals("short", TextUtils.truncate("short"));

        var longValue = "x".repeat(201);
        assertEquals("x".repeat(200) + "...", TextUtils.truncate(longValue));
    }

    @Test
    void expandEnvVariablesReplacesKnownVariablesAndRemovesMissingOnes() {
        var result = Database.expandEnvVariables(
                "jdbc:postgresql://${HOST}:${PORT}/app?sslmode=${SSL_MODE}&missing=${MISSING}",
                name -> Map.of("HOST", "localhost", "PORT", "5432", "SSL_MODE", "require").get(name)
        );

        assertEquals("jdbc:postgresql://localhost:5432/app?sslmode=require&missing=", result);
    }

    @Test
    void stripQuotesRemovesMatchingQuotesOnly() {
        assertEquals("value", Database.stripQuotes("'value'"));
        assertEquals("value", Database.stripQuotes("\"value\""));
        assertEquals("value", Database.stripQuotes("value"));
    }

    @Test
    void resolveConnectionSettingsCoversDefaultJdbcAndPostgresUrls() {
        var defaultSettings = Database.resolveConnectionSettings(null, null);
        assertEquals("jdbc:h2:mem:_project_;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
                defaultSettings.jdbcUrl());
        assertEquals("sa", defaultSettings.username());
        assertEquals("", defaultSettings.password());
        assertFalse(Database.needsDefaultH2Credentials(defaultSettings));

        var bareH2Settings = new Database.ConnectionSettings("jdbc:h2:mem:test", null, null);
        assertTrue(Database.needsDefaultH2Credentials(bareH2Settings));

        var jdbcSettings = Database.resolveConnectionSettings("jdbc:h2:mem:test", null);
        assertEquals("jdbc:h2:mem:test", jdbcSettings.jdbcUrl());
        assertEquals(null, jdbcSettings.username());
        assertEquals(null, jdbcSettings.password());
        assertTrue(Database.needsDefaultH2Credentials(jdbcSettings));

        var postgresSettings = Database.resolveConnectionSettings(
                null,
                "postgres://user:secret@localhost:5432/my_db?sslmode=require"
        );
        assertEquals("jdbc:postgresql://localhost:5432/my_db?sslmode=require", postgresSettings.jdbcUrl());
        assertEquals("user", postgresSettings.username());
        assertEquals("secret", postgresSettings.password());
        assertFalse(Database.needsDefaultH2Credentials(postgresSettings));

        var postgresWithoutAuth = Database.resolveConnectionSettings(null, "postgresql://db.example.com/app");
        assertEquals("jdbc:postgresql://db.example.com/app", postgresWithoutAuth.jdbcUrl());
        assertEquals(null, postgresWithoutAuth.username());
        assertEquals(null, postgresWithoutAuth.password());
    }
}
