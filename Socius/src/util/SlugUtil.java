package util;

public final class SlugUtil {

    private SlugUtil() {
    }

    public static String toSlug(String value) {
        if (value == null) {
            return "";
        }

        String slug = value.trim().toLowerCase();
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");
        slug = slug.replaceAll("\\s+", "-");
        slug = slug.replaceAll("-{2,}", "-");
        return slug;
    }
}
