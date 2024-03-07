package org.tohant.financesweb.mapper;

import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.tohant.financesweb.repository.entity.Category;
import org.tohant.financesweb.service.model.CategoryDto;

import java.util.List;

@Component
public class UserMapper {

    public User toUserDetails(org.tohant.financesweb.repository.entity.User user) {
        return new User(user.getUsername(), user.getPassword(),
                List.of(new SimpleGrantedAuthority("USER")));
    }

}
