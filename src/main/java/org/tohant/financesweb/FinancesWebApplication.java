package org.tohant.financesweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FinancesWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancesWebApplication.class, args);
    }

}
