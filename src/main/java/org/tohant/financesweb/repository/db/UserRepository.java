package org.tohant.financesweb.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tohant.financesweb.repository.entity.Payment;
import org.tohant.financesweb.repository.entity.User;
import org.tohant.financesweb.service.model.UserDto;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
