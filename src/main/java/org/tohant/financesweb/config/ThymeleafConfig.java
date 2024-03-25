package org.tohant.financesweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tohant.financesweb.util.FinancesDialect;

@Configuration
public class ThymeleafConfig {

    @Bean
    public FinancesDialect financesDialect() {
        return new FinancesDialect();
    }

}
