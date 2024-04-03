package org.tohant.financesweb.service.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tohant.financesweb.mapper.UserMapper;
import org.tohant.financesweb.repository.db.UserRepository;
import org.tohant.financesweb.service.IService;
import org.tohant.financesweb.service.model.UserDto;
import org.tohant.financesweb.service.model.UserLoginDto;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements IService<UserDto, Long> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream().map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto save(UserDto user) {
        return userMapper.toDto(userRepository.save(userMapper.toEntity(user)));
    }

    @Override
    public void updateAll(List<UserDto> entities) {
        userRepository.saveAll(entities.stream()
                .map(userMapper::toEntity)
                .collect(Collectors.toList()));
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    public UserDto findByUsername(String username) {
        return userMapper.toDto(userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("No such user: " + username)));
    }

    public UserDto findByGoogleId(String googleId) {
        return userMapper.toDto(userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new EntityNotFoundException("No such user found by google id")));
    }

    public UserDto getUserOrNull(UserLoginDto userLoginDto) {
        try {
            UserDto userByGoogleId = findByGoogleId(userLoginDto.getGoogleId());
            if (userByGoogleId != null) {
                return userByGoogleId;
            }
            return findByUsername(userLoginDto.getUsername());
        } catch (EntityNotFoundException e) {
            log.warn("User not found. Username: " + userLoginDto.getUsername());
            return null;
        }
    }

}
