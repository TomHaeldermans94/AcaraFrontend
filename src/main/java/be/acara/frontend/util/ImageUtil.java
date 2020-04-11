package be.acara.frontend.util;

import java.util.Base64;
import java.util.Objects;

public interface ImageUtil {
    static String convertToBase64(byte[] image) {
        return !Objects.isNull(image)
                ? Base64.getEncoder().encodeToString(image)
                : "";
    }
}
