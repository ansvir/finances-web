package org.tohant.financesweb.service.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class UserDto extends User {

    private String googleId;
    private List<CategoryDto> categories;

    public UserDto(String googleId, String username, String password, Collection<? extends GrantedAuthority> authorities, List<CategoryDto> categories) {
        super(username, password, authorities);
        this.googleId = googleId;
        this.categories = categories;
    }

}
