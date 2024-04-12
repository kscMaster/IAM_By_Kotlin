package nancal.iam.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class BaseEnConfig {

    @Bean("industryBundle")
    public ResourceBundle getBundle() {
        ResourceBundle bundle;
        bundle = ResourceBundle.getBundle("i18n.industry", Locale.US);
        return bundle;
    }


    public String getCn(String en) {
        Set<String> keys = this.getBundle().keySet();

        List<String> collect =
                keys.stream().filter(a -> this.getBundle().getString(a).equals(en)).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(collect)) {
            return en;
        }
        return collect.get(0);

    }


    public String getEn(String cn) {
        try {
            return this.getBundle().getString(cn);
        } catch (Exception e) {
            return cn;
        }

    }

}