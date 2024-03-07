package org.tohant.financesweb.service.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tohant.financesweb.mapper.PaymentMapper;
import org.tohant.financesweb.repository.db.PaymentRepository;
import org.tohant.financesweb.repository.db.UserRepository;
import org.tohant.financesweb.service.IService;
import org.tohant.financesweb.service.model.PaymentDto;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService implements IService<PaymentDto, Long> {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public List<PaymentDto> findAll() {
        return paymentRepository.findAll()
                .stream().map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void save(PaymentDto paymentDto) {
        String currentUser = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        userRepository.findByUsername(currentUser)
                .map(user -> paymentRepository.save(paymentMapper.toEntity(paymentDto, user)))
                .orElseThrow(() -> new EntityNotFoundException("No user found for username: " + currentUser));
    }

    @Override
    public void updateAll(List<PaymentDto> entities) {
        String currentUser = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        userRepository.findByUsername(currentUser)
                .map(user -> paymentRepository.saveAll(entities.stream()
                        .map(payment -> paymentMapper.toEntity(payment, user))
                        .collect(Collectors.toList())))
                .orElseThrow(() -> new EntityNotFoundException("No user found for username: " + currentUser));
    }

    @Override
    public long count() {
        return paymentRepository.count();
    }

}
