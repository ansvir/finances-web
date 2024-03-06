package org.tohant.financesweb.api.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {


    private String name;
    private BigDecimal amount;
    private HttpResponseDto response;
    private Type type;

    @Getter
    @RequiredArgsConstructor
    public enum Type {

        DEBT(1, "Долг"), OTHER(2, "Другое"), TAXES(3, "Квартплата"),
        ENTERTAINMENT(4, "Развлечения"), CONNECTION(5, "Связь");

        private final int id;
        private final String name;

        public static Type fromId(int id) {
            return Arrays.stream(values())
                    .filter(value -> value.id == id).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No such Payment type with id: " + id));
        }

    }

}
