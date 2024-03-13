package org.tohant.financesweb.mapper;

import org.springframework.stereotype.Component;
import org.tohant.financesweb.repository.entity.Category;
import org.tohant.financesweb.repository.entity.Payment;
import org.tohant.financesweb.service.model.CategoryDto;
import org.tohant.financesweb.service.model.PaymentDto;

@Component
public class CategoryMapper {

    public CategoryDto toDto(Category category) {
        return CategoryDto.create(category.getId().intValue(), category.getName(),
                category.getPriority());
    }

    public Category toEntity(CategoryDto categoryDto) {
        return new Category(Long.parseLong(categoryDto.getId()),
                categoryDto.getName(), Integer.parseInt(categoryDto.getPriority()));
    }

}
