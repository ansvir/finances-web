package org.tohant.financesweb.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tohant.financesweb.repository.entity.Payment;
import org.tohant.financesweb.repository.entity.User;
import org.tohant.financesweb.service.model.CategoryDto;
import org.tohant.financesweb.service.model.PaymentDto;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

import static org.tohant.financesweb.service.model.PaymentDto.DATE_TIME_FORMATTER;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    private final CategoryMapper categoryMapper;

    public PaymentDto toDto(Payment payment) {
        return PaymentDto.create(payment.getName(), payment.getAmount(),
                categoryMapper.toDto(payment.getCategory()),
                payment.getDateTime().format(DATE_TIME_FORMATTER));
    }

    public Payment toEntity(PaymentDto paymentDto, CategoryDto categoryDto, User user) {
        Payment payment = new Payment();
        payment.setName(paymentDto.getName());
        payment.setAmount(paymentDto.getAmount());
        payment.setCategory(categoryMapper.toEntity(categoryDto));
        payment.setDateTime(LocalDateTime.now());
        payment.setUser(user);
        return payment;
    }

}
