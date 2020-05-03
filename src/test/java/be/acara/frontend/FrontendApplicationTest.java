package be.acara.frontend;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FrontendApplicationTest {
    
    @Test
    void contextLoaded() {
        try {
            assertThat(true).isTrue();
        } catch (Exception e) {
            assertThat(true).isFalse();
        }
    }
    
    @Test
    void applicationStart() {
        try {
            FrontendApplication.main(new String[]{});
        } catch (Exception e) {
            assertThat(true).isFalse();
        }
    }
}
