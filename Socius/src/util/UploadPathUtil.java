package util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class UploadPathUtil {
    public static final String POST_IMAGE_URL_PREFIX = "/uploads/post-images/";

    private UploadPathUtil() {
    }

    public static Path getPostImageDirectory() {
        return Paths.get(System.getProperty("user.home"), "socius-uploads", "post-images");
    }
}
