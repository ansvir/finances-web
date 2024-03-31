package org.tohant.financesweb.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto extends HttpResponseDto {


    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    public static final DateTimeFormatter SUMMARY_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM uuuu", Locale.forLanguageTag("ru"));

    private String name;
    private BigDecimal amount;
    private String categoryId;
    private String dateTime;

    public static PaymentDto create(String name, BigDecimal amount, CategoryDto category, String dateTime) {
        return new PaymentDto(name, amount, category.getId(), dateTime);
    }

}
