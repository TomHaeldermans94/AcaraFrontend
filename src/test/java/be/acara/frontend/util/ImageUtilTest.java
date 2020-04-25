package be.acara.frontend.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageUtilTest {
    
    @Test
    void convertToBase64() {
        String answer = ImageUtil.convertToBase64(EventUtil.getImage1AsBytes());
        
        assertThat(answer).isNotNull();
        assertThat(answer).isNotBlank();
    }
    
    @Test
    void convertToBase64_withNullImage() {
        String answer = ImageUtil.convertToBase64(null);
        
        assertThat(answer).isNotNull();
        assertThat(answer).isBlank();
    }
}
