package org.tohant.financesweb.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.tohant.financesweb.repository.entity.Profile;
import org.tohant.financesweb.service.model.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final CategoryMapper categoryMapper;

    public UserDto toDto(org.tohant.financesweb.repository.entity.User user) {
        return new UserDto(user.getUsername(), user.getPassword(),
                List.of(new SimpleGrantedAuthority("USER")), user.getProfile().getCategories().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList()));
    }

    public org.tohant.financesweb.repository.entity.User toEntity(UserDto user) {
        org.tohant.financesweb.repository.entity.User userEntity = new org.tohant.financesweb.repository.entity.User();
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(user.getPassword());
        return userEntity;
    }

}
