package gg.updown.backend.common.util;

public class StringUtil {
    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    public static String removeHtmlTags(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("<[^>]*>", "");
    }
}
