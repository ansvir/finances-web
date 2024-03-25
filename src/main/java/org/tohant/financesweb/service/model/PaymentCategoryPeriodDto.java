package org.tohant.financesweb.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCategoryPeriodDto {

    private CategoryDto category;
    private BigDecimal amount;

}
