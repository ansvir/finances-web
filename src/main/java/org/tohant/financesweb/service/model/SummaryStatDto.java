package org.tohant.financesweb.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryStatDto {
    private YearMonth dateYear;
    private String categoryName;
    private BigDecimal allExpenses;
}
