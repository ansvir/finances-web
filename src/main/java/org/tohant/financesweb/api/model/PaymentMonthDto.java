package org.tohant.financesweb.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMonthDto {

    private LocalDate date;
    private BigDecimal total;
    private Integer displayValue;

}
