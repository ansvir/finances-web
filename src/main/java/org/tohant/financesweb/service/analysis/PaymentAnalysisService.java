package org.tohant.financesweb.service.analysis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tohant.financesweb.service.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
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
        return sortDescendingByDate(paymentsByMonthsDtos);
    }

    public List<PaymentPeriodDto> getPaymentsByPeriod(List<PaymentDto> payments, LocalDate from, LocalDate to) {
        return payments.stream()
                .filter(payment -> {
                    LocalDate date = LocalDateTime.parse(payment.getDateTime()).toLocalDate();
                    return date.isAfter(from) && date.isBefore(to);
                })
                .collect(Collectors.groupingBy(payment -> LocalDateTime.parse(payment.getDateTime()).toLocalDate(),
                        Collectors.mapping(PaymentDto::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream().map((entry) -> new PaymentPeriodDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public SummaryStatDto getSummaryStats(List<PaymentDto> payments) {
        BigDecimal totalExpenses = BigDecimal.ZERO;
        Map<CategoryDto, BigDecimal> categoryExpenses = new HashMap<>();
        Map<YearMonth, BigDecimal> monthlyExpenses = new HashMap<>();
        SummaryStatDto summary = new SummaryStatDto();

        YearMonth mostExpensiveMonth = null;
        BigDecimal maxMonthlyExpense = BigDecimal.ZERO;
        CategoryDto mostExpensiveCategory = null;
        BigDecimal maxCategoryExpense = BigDecimal.ZERO;

        for (PaymentDto payment : payments) {
            BigDecimal amount = payment.getAmount();
            totalExpenses = totalExpenses.add(amount);

            CategoryDto category = payment.getCategory();
            BigDecimal categoryTotal = categoryExpenses.getOrDefault(category, BigDecimal.ZERO);
            categoryExpenses.put(category, categoryTotal.add(amount));

            LocalDate date = LocalDate.parse(payment.getDateTime(), PaymentDto.DATE_TIME_FORMATTER);
            YearMonth yearMonth = YearMonth.from(date);
            BigDecimal monthlyTotal = monthlyExpenses.getOrDefault(yearMonth, BigDecimal.ZERO);
            monthlyExpenses.put(yearMonth, monthlyTotal.add(amount));

            if (monthlyTotal.compareTo(maxMonthlyExpense) > 0) {
                maxMonthlyExpense = monthlyTotal;
                mostExpensiveMonth = yearMonth;
            }

            if (categoryTotal.compareTo(maxCategoryExpense) > 0) {
                maxCategoryExpense = categoryTotal;
                mostExpensiveCategory = category;
            }
        }
        if (mostExpensiveMonth == null) {
            summary.setDateYear(null);
        } else {
            summary.setDateYear(mostExpensiveMonth);
        }
        if (mostExpensiveCategory == null) {
            summary.setCategoryName("Нет данных");
        } else {
            summary.setCategoryName(mostExpensiveCategory.getName());
        }
        summary.setAllExpenses(totalExpenses);
        return summary;
    }

    private List<PaymentMonthDto> sortDescendingByDate(List<PaymentMonthDto> payments) {
        return payments.stream().sorted((payment1, payment2) -> payment1.getDate().isBefore(payment2.getDate())
                        ? 1 : payment1.getDate().isEqual(payment2.getDate())
                        ? 0 : -1)
                .collect(Collectors.toList());
    }

}
