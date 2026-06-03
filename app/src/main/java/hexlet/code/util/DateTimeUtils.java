package hexlet.code.util;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateTimeUtils {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.forLanguageTag("ru"));

    private DateTimeUtils() {
    }

    public static String format(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }

        return timestamp.toLocalDateTime().format(FORMATTER);
    }
}
