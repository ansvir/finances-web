package org.tohant.financesweb.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tohant.financesweb.repository.db.CategoryRepository;
import org.tohant.financesweb.repository.db.PaymentRepository;
import org.tohant.financesweb.repository.db.UserRepository;
import org.tohant.financesweb.repository.entity.Category;
import org.tohant.financesweb.repository.entity.Payment;
import org.tohant.financesweb.repository.entity.User;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Configuration
@Slf4j
@Profile("dev")
@RequiredArgsConstructor
public class DatabaseDataInitTest implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Executing DatabaseDataInitTest test data initialization...");
        insertCategoryData();
        insertUsersAndPaymentsData();
    }

    private void insertCategoryData() {
        Category category1 = new Category(null, "Долг", 1);
        Category category2 = new Category(null, "Другое", 2);
        Category category3 = new Category(null, "Квартплата", 3);
        Category category4 = new Category(null, "Развлечения", 4);
        Category category5 = new Category(null, "Связь", 5);
        List<Category> categories = List.of(category1, category2, category3, category4, category5);
        categoryRepository.saveAll(categories);
    }

    private void insertUsersAndPaymentsData() {
        User admin = new User(null, "admin", "toha2456");
        User nastysha = new User(null, "nastysha", "aniasti");
        userRepository.saveAll(Arrays.asList(admin, nastysha));
        paymentRepository.saveAll(generatePaymentsData(admin));
        paymentRepository.saveAll(generatePaymentsData(nastysha));
    }

    private List<Payment> generatePaymentsData(User user) {
        List<Payment> payments = new ArrayList<>();
        List<Category> categories = categoryRepository.findAll();
        for (int i = 1; i <= new Random().nextInt(100); i++) {
            for (Category category : categories) {
                Payment payment = new Payment();
                payment.setName("Payment " + ((i - 1) * 10 + category.getPriority()));
                payment.setAmount(BigDecimal.valueOf(Math.random() * 1000 + 1).setScale(2, RoundingMode.HALF_UP));
                payment.setCategory(category);
                payment.setUser(user);
                payment.setDateTime(LocalDateTime.now().minusDays(i));
                payments.add(payment);
            }
        }
        return payments;
    }

}
