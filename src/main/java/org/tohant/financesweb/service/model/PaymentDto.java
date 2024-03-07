package org.tohant.financesweb.service.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto extends HttpResponseDto {


    private String name;
    private BigDecimal amount;
    private CategoryDto category;
    private LocalDateTime dateTime = LocalDateTime.now();

    public static PaymentDto create(String name, BigDecimal amount, CategoryDto category, LocalDateTime dateTime) {
        return new PaymentDto(name, amount, category, dateTime);
    }

}
