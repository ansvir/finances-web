package org.tohant.financesweb.api.analysis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tohant.financesweb.api.model.PaymentDto;
import org.tohant.financesweb.api.model.PaymentMonthDto;
import org.tohant.financesweb.api.sheet.SheetsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentAnalysisService {

    private final SheetsService localGoogleSheetsService;

    public List<PaymentMonthDto> getPaymentsByMonths() {
        List<PaymentDto> payments = localGoogleSheetsService.getPaymentsFromCache();
        Map<YearMonth, BigDecimal> paymentsByMonths = payments.stream()
                .collect(Collectors.groupingBy(payment -> YearMonth.from(payment.getDateTime()),
                        Collectors.mapping(PaymentDto::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        List<PaymentMonthDto> paymentsByMonthsDtos = new ArrayList<>();
        BigDecimal previousTotal = BigDecimal.ZERO;

        for (Map.Entry<YearMonth, BigDecimal> entry : paymentsByMonths.entrySet()) {
            YearMonth monthYear = entry.getKey();
            BigDecimal totalExpenses = entry.getValue();
            LocalDate date = monthYear.atDay(1); // Assuming the day is set to the first day of the month

            BigDecimal percentageDelta = BigDecimal.ZERO;
            if (!previousTotal.equals(BigDecimal.ZERO)) {
                BigDecimal delta = totalExpenses.subtract(previousTotal);
                percentageDelta = delta.divide(previousTotal, 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            PaymentMonthDto paymentMonthDto = new PaymentMonthDto(date, totalExpenses);
            paymentMonthDto.setTotal(percentageDelta);
            paymentsByMonthsDtos.add(paymentMonthDto);

            previousTotal = totalExpenses;
        }
        return paymentsByMonthsDtos;
    }

}
