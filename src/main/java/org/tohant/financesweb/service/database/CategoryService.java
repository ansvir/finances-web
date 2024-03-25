package org.tohant.financesweb.service.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tohant.financesweb.mapper.CategoryMapper;
import org.tohant.financesweb.repository.db.CategoryRepository;
import org.tohant.financesweb.repository.entity.Category;
import org.tohant.financesweb.service.IService;
import org.tohant.financesweb.service.model.CategoryDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService implements IService<CategoryDto, Long> {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll()
                .stream().map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void save(CategoryDto categoryDto) {
        categoryRepository.save(categoryMapper.toEntity(categoryDto));
    }

    @Override
    public void updateAll(List<CategoryDto> entities) {
        categoryRepository.saveAll(entities.stream()
                .map(categoryMapper::toEntity)
                .collect(Collectors.toList()));
    }

    @Override
    public long count() {
        return categoryRepository.count();
    }

    public List<CategoryDto> findAllByCurrentUserOrderByPriority() {
        String currentUser = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return categoryRepository.findAllByUsernameOrderByPriority(currentUser)
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public void rearrangeCategories(List<Long> ids) {
        List<CategoryDto> categories = categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());

        int priority = 1;
        for (Long id : ids) {
            Optional<CategoryDto> categoryOptional = categories.stream()
                    .filter(category -> Long.valueOf(category.getId())
                            .equals(id))
                    .findFirst();

            if (categoryOptional.isPresent()) {
                CategoryDto category = categoryOptional.get();
                category.setPriority(String.valueOf(priority));
                priority++;
            }
        }

        // Save the updated categories
        List<Category> updatedCategories = categories.stream()
                .map(categoryMapper::toEntity)
                .collect(Collectors.toList());
        categoryRepository.saveAll(updatedCategories);
    }

}
