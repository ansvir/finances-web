package org.tohant.financesweb.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tohant.financesweb.repository.entity.Payment;
import org.tohant.financesweb.repository.entity.User;
import org.tohant.financesweb.service.model.PaymentDto;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public Payment toEntity(PaymentDto paymentDto, User user) {
        Payment payment = new Payment();
        paymentDto.getCategory().setPriority(user.getProfile().getCategories()
                .stream()
                .filter(category -> category.getId().toString().equals(paymentDto.getCategory().getId()))
                .findFirst().orElseThrow(() -> new EntityNotFoundException("No such category with id: " + paymentDto.getCategory().getId()))
                .getPriority().toString());
        payment.setName(paymentDto.getName());
        payment.setAmount(paymentDto.getAmount());
        payment.setCategory(categoryMapper.toEntity(paymentDto.getCategory()));
        payment.setDateTime(LocalDateTime.now());
        payment.setUser(user);
        return payment;
    }

}
