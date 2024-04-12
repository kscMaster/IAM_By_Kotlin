package nancal.iam.base.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class AdminEnConfig {

    @Bean
    public ResourceBundle getBundle() {
        ResourceBundle bundle;
        bundle = ResourceBundle.getBundle("i18n.message", Locale.US);
        return bundle;
    }

    @Bean("adminBizBundle")
    public ResourceBundle getBundleBizLog() {
        ResourceBundle bundle;
        bundle = ResourceBundle.getBundle("i18n.bizLog", Locale.US);
        return bundle;
    }

}