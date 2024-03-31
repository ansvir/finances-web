package org.tohant.financesweb.service.analysis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tohant.financesweb.service.database.CategoryService;
import org.tohant.financesweb.service.model.*;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static org.tohant.financesweb.service.model.PaymentDto.SUMMARY_DATE_TIME_FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentAnalysisService {

    public List<PaymentMonthDto> getPaymentsByMonths(List<PaymentDto> payments) {
        Map<YearMonth, BigDecimal> paymentsByMonths = payments.stream()
                .collect(Collectors.groupingBy(payment -> YearMonth.from(LocalDateTime.parse(payment.getDateTime())),
                        Collectors.mapping(PaymentDto::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        Map<YearMonth, BigDecimal> sortedPaymentsByMonths = paymentsByMonths.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        List<PaymentMonthDto> paymentsByMonthsDtos = new ArrayList<>();
        BigDecimal previousTotal = BigDecimal.ZERO;

        for (Map.Entry<YearMonth, BigDecimal> entry : sortedPaymentsByMonths.entrySet()) {
            YearMonth monthYear = entry.getKey();
            BigDecimal totalExpenses = entry.getValue();
            LocalDate date = monthYear.atDay(1); // Assuming the day is set to the first day of the month

            BigDecimal percentageDelta = BigDecimal.ZERO;
            if (!previousTotal.equals(BigDecimal.ZERO)) {
                BigDecimal delta = totalExpenses.subtract(previousTotal);
                percentageDelta = delta.divide(previousTotal, 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            PaymentMonthDto paymentMonthDto = new PaymentMonthDto(date, totalExpenses,
                    percentageDelta.setScale(0, RoundingMode.HALF_UP).intValue());
            paymentMonthDto.setTotal(percentageDelta);
            paymentsByMonthsDtos.add(paymentMonthDto);

            previousTotal = totalExpenses;
        }
        return paymentsByMonthsDtos.stream()
                .sorted(Comparator.comparing(PaymentMonthDto::getDate, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public SummaryStatDto getSummaryStats(List<PaymentDto> payments, List<CategoryDto> categories) {
        SummaryStatDto summary = new SummaryStatDto();
        Optional<Map.Entry<YearMonth, BigDecimal>> mostExpensiveMonth = payments.stream()
                .collect(Collectors.groupingBy(payment -> YearMonth.from(LocalDateTime.parse(payment.getDateTime())),
                        Collectors.mapping(PaymentDto::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream().max(Map.Entry.comparingByValue());
        Optional<Map.Entry<CategoryDto, BigDecimal>> mostExpensiveCategory = payments.stream()
                .collect(Collectors.groupingBy(payment -> categories.stream()
                                .filter(category -> category.getId().equals(payment.getCategoryId()))
                                .findFirst().orElseThrow(),
                        Collectors.mapping(PaymentDto::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue());
        BigDecimal totalExpenses = BigDecimal.valueOf(payments.stream()
                .mapToDouble(payment -> payment.getAmount().doubleValue())
                .sum());
        mostExpensiveMonth.ifPresentOrElse(dateYear -> summary.setDateYear(dateYear.getKey().format(SUMMARY_DATE_TIME_FORMATTER)),
                () -> summary.setDateYear("Нет данных"));
        mostExpensiveCategory.ifPresentOrElse(category -> summary.setCategoryName(category.getKey().getName()),
                () -> summary.setCategoryName("Нет данных"));
        summary.setAllExpenses(totalExpenses);
        return summary;
    }

}
