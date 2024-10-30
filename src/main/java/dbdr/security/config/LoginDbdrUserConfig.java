package dbdr.security.config;

import dbdr.security.service.LoginDbdrUserArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginDbdrUserConfig implements WebMvcConfigurer {

    private final LoginDbdrUserArgumentResolver loginDbdrUserArgumentResolver;

    public LoginDbdrUserConfig(LoginDbdrUserArgumentResolver loginDbdrUserArgumentResolver) {
        this.loginDbdrUserArgumentResolver = loginDbdrUserArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginDbdrUserArgumentResolver);
    }

}
