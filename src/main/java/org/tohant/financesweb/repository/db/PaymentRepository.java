package org.tohant.financesweb.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.tohant.financesweb.repository.entity.Category;
import org.tohant.financesweb.repository.entity.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p JOIN Category c ON p.category.id = c.id" +
            " WHERE p.user.username LIKE :username ORDER BY p.dateTime")
    List<Payment> findAllByUsername(String username);

    @Query("SELECT p FROM Payment p JOIN Category c ON p.category.id = c.id" +
            " WHERE p.user.username LIKE :username AND c.id = :categoryId ORDER BY p.dateTime")
    List<Payment> findAllByUsernameAndCategory(String username, Long categoryId);

}
