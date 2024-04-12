package nancal.iam.base.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.ResourceBundle;

@Configuration
public class EnConfig {

    @Bean
    public ResourceBundle getBundle() {
        ResourceBundle bundle;
        bundle = ResourceBundle.getBundle("i18n.message", Locale.US);
        return bundle;
    }

}