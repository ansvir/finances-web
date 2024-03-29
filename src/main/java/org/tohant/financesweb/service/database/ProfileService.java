package org.tohant.financesweb.service.database;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tohant.financesweb.mapper.ProfileMapper;
import org.tohant.financesweb.repository.db.CategoryRepository;
import org.tohant.financesweb.repository.db.PaymentRepository;
import org.tohant.financesweb.repository.db.ProfileRepository;
import org.tohant.financesweb.repository.db.UserRepository;
import org.tohant.financesweb.repository.entity.Category;
import org.tohant.financesweb.repository.entity.Payment;
import org.tohant.financesweb.repository.entity.Profile;
import org.tohant.financesweb.service.IService;
import org.tohant.financesweb.service.model.ProfileDto;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService implements IService<ProfileDto, Long> {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentRepository paymentRepository;
    private final ProfileMapper profileMapper;

    @Override
    public List<ProfileDto> findAll() {
        return profileRepository.findAll()
                .stream()
                .map(profileMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProfileDto save(ProfileDto entity) {
        return profileMapper.toDto(profileRepository.save(profileMapper.toEntity(entity)));
    }

    @Override
    public void updateAll(List<ProfileDto> entities) {
        List<Profile> profiles = entities
                .stream()
                .map(profileMapper::toEntity)
                .collect(Collectors.toList());
        profileRepository.saveAll(profiles);
    }

    @Override
    public long count() {
        return profileRepository.count();
    }

    @CacheEvict("categoriesCache")
    public void deleteCategoryFromProfile(Long reassignedCategoryId, Long deletedCategoryId) {
        String currentUser = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Category category = userRepository.findByUsername(currentUser)
                .map(user -> categoryRepository.findById(reassignedCategoryId)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("No category found for id: " + reassignedCategoryId)))
                .orElseThrow(() -> new EntityNotFoundException("No user found for username: " + currentUser));
        List<Payment> payments = paymentRepository.findAllByUsernameAndCategory(currentUser, deletedCategoryId)
                .stream().map(payment -> {
                    payment.setCategory(category);
                    return payment;
                }).collect(Collectors.toList());
        paymentRepository.saveAll(payments);
        Category deletedCategory = userRepository.findByUsername(currentUser)
                .map(user -> categoryRepository.findById(deletedCategoryId).stream()
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("No category found for id: " + deletedCategoryId)))
                .orElseThrow(() -> new EntityNotFoundException("No user found for username: " + currentUser));
        Profile profile = profileRepository.findByCategoryId(deletedCategoryId)
                .map(p -> {
                    p.getCategories().remove(deletedCategory);
                    return p;
                }).orElseThrow(() -> new EntityNotFoundException("No profile found with category id: " + deletedCategoryId));
        profileRepository.save(profile);
        categoryRepository.delete(deletedCategory);
    }

}
