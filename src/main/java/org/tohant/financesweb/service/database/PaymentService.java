package org.tohant.financesweb.service.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tohant.financesweb.mapper.CategoryMapper;
import org.tohant.financesweb.mapper.PaymentMapper;
import org.tohant.financesweb.repository.db.CategoryRepository;
import org.tohant.financesweb.repository.db.PaymentRepository;
import org.tohant.financesweb.repository.db.UserRepository;
import org.tohant.financesweb.repository.entity.Category;
import org.tohant.financesweb.repository.entity.Payment;
import org.tohant.financesweb.service.IService;
import org.tohant.financesweb.service.model.CategoryDto;
import org.tohant.financesweb.service.model.PaymentCategoryPeriodDto;
import org.tohant.financesweb.service.model.PaymentDto;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService implements IService<PaymentDto, Long> {

    private final PaymentRepository paymentRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public List<PaymentDto> findAll() {
        String currentUser = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return paymentRepository.findAllByUsername(currentUser)
                .stream().map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDto save(PaymentDto paymentDto) {
        String currentUser = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByUsername(currentUser)
                .map(user -> {
                    CategoryDto categoryDto = categoryMapper.toDto(categoryRepository
                            .findById(Long.parseLong(paymentDto.getCategoryId()))
                            .stream()
                            .filter(category -> category.getId().toString().equals(paymentDto.getCategoryId()))
                            .findFirst().orElseThrow(() -> new EntityNotFoundException("No such category with id: " + paymentDto.getCategoryId())));
                    return paymentMapper.toDto(paymentRepository.save(paymentMapper.toEntity(paymentDto, categoryDto, user)));
                })
                .orElseThrow(() -> new EntityNotFoundException("No user found for username: " + currentUser));
    }

    @Override
    public void updateAll(List<PaymentDto> entities) {
        String currentUser = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        userRepository.findByUsername(currentUser)
                .map(user -> paymentRepository.saveAll(entities.stream()
                        .map(payment -> {
                            CategoryDto categoryDto = categoryMapper.toDto(categoryRepository
                                    .findById(Long.parseLong(payment.getCategoryId()))
                                    .stream()
                                    .filter(category -> category.getId().toString().equals(payment.getCategoryId()))
                                    .findFirst().orElseThrow(() -> new EntityNotFoundException("No such category with id: " + payment.getCategoryId())));
                            return paymentMapper.toEntity(payment, categoryDto, user);
                        })
                        .collect(Collectors.toList())))
                .orElseThrow(() -> new EntityNotFoundException("No user found for username: " + currentUser));
    }

    @Override
    public long count() {
        return paymentRepository.count();
    }

    public List<PaymentCategoryPeriodDto> findAllByPeriodGroupedByCategories(LocalDate from, LocalDate to) {
        String currentUser = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        List<Category> categories = userRepository.findByUsername(currentUser)
                .map(user -> categoryRepository.findAllByUsernameOrderByPriority(currentUser))
                .orElseThrow(() -> new EntityNotFoundException("No user found for username: " + currentUser));
        List<Payment> payments = paymentRepository.findAllByUsername(currentUser);
        Map<CategoryDto, BigDecimal> paymentsByPeriod = payments.stream()
                .filter(payment -> {
                    LocalDate date = payment.getDateTime().toLocalDate();
                    return date.isAfter(from) && date.isBefore(to)
                            || date.isEqual(from) || date.isEqual(to);
                })
                .collect(Collectors.groupingBy(payment -> categoryMapper.toDto(payment.getCategory()),
                        Collectors.mapping(Payment::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        return categories.stream()
                .map(category -> new PaymentCategoryPeriodDto(categoryMapper.toDto(category), paymentsByPeriod.getOrDefault(categoryMapper.toDto(category), BigDecimal.ZERO)))
                .collect(Collectors.toList());
    }

}
