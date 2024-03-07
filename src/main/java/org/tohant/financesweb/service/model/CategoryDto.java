package org.tohant.financesweb.service.model;

import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto extends HttpResponseDto {

    private Integer id;
    private String name;
    private Integer priority;

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

        public static List<CategoryDto> collectAll() {
            return Arrays.stream(values())
                    .map(type -> new CategoryDto(type.id, type.name, type.id))
                    .collect(Collectors.toList());
        }

    }

    public static CategoryDto create(Integer id, String name, Integer priority) {
        return new CategoryDto(id, name, priority);
    }

}
