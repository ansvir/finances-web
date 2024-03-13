package org.tohant.financesweb.service.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto extends HttpResponseDto {


    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private String name;
    private BigDecimal amount;
    private CategoryDto category;
    private String dateTime;

    public static PaymentDto create(String name, BigDecimal amount, CategoryDto category, String dateTime) {
        return new PaymentDto(name, amount, category, dateTime);
    }

}
