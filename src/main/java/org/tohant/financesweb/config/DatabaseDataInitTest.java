package org.tohant.financesweb.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tohant.financesweb.repository.db.CategoryRepository;
import org.tohant.financesweb.repository.db.PaymentRepository;
import org.tohant.financesweb.repository.db.ProfileRepository;
import org.tohant.financesweb.repository.db.UserRepository;
import org.tohant.financesweb.repository.entity.Category;
import org.tohant.financesweb.repository.entity.Payment;
import org.tohant.financesweb.repository.entity.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@Slf4j
@Profile("dev")
@RequiredArgsConstructor
public class DatabaseDataInitTest implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Executing DatabaseDataInitTest test data initialization...");
//        initData();
    }

    private void initData() {
        if (checkNoUsersExist()) {
            User admin = userRepository.save(createUser("admin", "toha2456"));
            User nastysha = userRepository.save(createUser("nastysha", "aniasti"));
            List<Category> adminCategories = categoryRepository.saveAll(insertCategoryData());
            List<Category> nastyshaCategories = categoryRepository.saveAll(insertCategoryData());;
            admin.setProfile(createProfile(adminCategories));
            nastysha.setProfile(createProfile(nastyshaCategories));
            paymentRepository.saveAll(generatePaymentsData(nastysha, nastyshaCategories));
            paymentRepository.saveAll(generatePaymentsData(admin, adminCategories));
        }
    }

    private boolean checkNoUsersExist() {
        return userRepository.count() == 0L;
    }

    private User createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEnabled(true);
        return user;
    }

    private org.tohant.financesweb.repository.entity.Profile createProfile(List<Category> categories) {
        org.tohant.financesweb.repository.entity.Profile profile = new org.tohant.financesweb.repository.entity.Profile();
        profile.setCategories(categories);
        return profile;
    }

    private List<Category> insertCategoryData() {
        Category category1 = new Category(null, "Долг", 1);
        Category category2 = new Category(null, "Другое", 2);
        Category category3 = new Category(null, "Квартплата", 3);
        Category category4 = new Category(null, "Развлечения", 4);
        Category category5 = new Category(null, "Связь", 5);
        return List.of(category1, category2, category3, category4, category5);
    }

    private List<Payment> generatePaymentsData(User user, List<Category> categories) {
        List<Payment> payments = new ArrayList<>();
        for (int i = 1; i <= new Random().nextInt(100); i++) {
            for (Category category : categories) {
                Payment payment = new Payment();
                payment.setName("Payment " + ((i - 1) * 10 + category.getPriority()));
                payment.setAmount(BigDecimal.valueOf(Math.random() * 1000 + 1).setScale(2, RoundingMode.HALF_UP));
                payment.setCategory(category);
                payment.setUser(user);
                payment.setDateTime(LocalDateTime.now().minusMonths(i));
                payments.add(payment);
            }
        }
        return payments;
    }

}
