package hexlet.code.util;

public final class TextUtils {
    private static final int MAX_LENGTH = 200;

    private TextUtils() {
    }

    public static String truncate(String value) {
        if (value == null) {
            return "";
        }

        if (value.length() <= MAX_LENGTH) {
            return value;
        }

        return value.substring(0, MAX_LENGTH) + "...";
    }
}
