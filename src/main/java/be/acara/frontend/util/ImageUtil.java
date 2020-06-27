package be.acara.frontend.util;

import java.util.Base64;
import java.util.Objects;

public interface ImageUtil {
    /**
     * Converts an image byte-array to the Base64 equivalent
     * @param image the given image
     * @return a Base64 equivalent String
     */
    static String convertToBase64(byte[] image){
        return !Objects.isNull(image)
                ? Base64.getEncoder().encodeToString(image)
                : "";
    }
}
