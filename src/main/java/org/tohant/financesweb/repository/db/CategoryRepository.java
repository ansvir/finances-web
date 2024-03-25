package org.tohant.financesweb.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.tohant.financesweb.repository.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM User u JOIN Profile p ON p.user.username = u.username JOIN p.categories c" +
            " WHERE p.user.username = :username GROUP BY c ORDER BY c.priority")
    List<Category> findAllByUsernameOrderByPriority(String username);
}
