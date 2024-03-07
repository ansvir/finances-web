package org.tohant.financesweb.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tohant.financesweb.repository.entity.Payment;
import org.tohant.financesweb.repository.entity.User;
import org.tohant.financesweb.service.model.PaymentDto;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    private final CategoryMapper categoryMapper;

    public PaymentDto toDto(Payment payment) {
        return PaymentDto.create(payment.getName(), payment.getAmount(),
                categoryMapper.toDto(payment.getCategory()), payment.getDateTime());
    }

    public Payment toEntity(PaymentDto paymentDto, User user) {
        Payment payment = new Payment();
        payment.setName(paymentDto.getName());
        payment.setAmount(paymentDto.getAmount());
        payment.setCategory(categoryMapper.toEntity(paymentDto.getCategory()));
        payment.setDateTime(payment.getDateTime());
        payment.setUser(user);
        return payment;
    }

}
