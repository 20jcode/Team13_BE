package dbdr.testhelper;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public TestHelperFactory testHelperFactory() {
        return new TestHelperFactory();
    }
}
