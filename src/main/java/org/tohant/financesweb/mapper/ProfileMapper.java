package org.tohant.financesweb.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tohant.financesweb.repository.entity.Profile;
import org.tohant.financesweb.service.model.ProfileDto;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfileMapper {

    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    public ProfileDto toDto(Profile profile) {
        return new ProfileDto(profile.getId(), userMapper.toDto(profile.getUser()),
                profile.getCategories().stream()
                        .map(categoryMapper::toDto)
                        .collect(Collectors.toList()));
    }

    public Profile toEntity(ProfileDto profileDto) {
        return new Profile(profileDto.getId(),
                userMapper.toEntity(profileDto.getUserDto()), profileDto.getCategories().stream()
                .map(categoryMapper::toEntity)
                .collect(Collectors.toList()));
    }

}
