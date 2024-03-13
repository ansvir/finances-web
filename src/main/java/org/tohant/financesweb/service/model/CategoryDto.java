package org.tohant.financesweb.service.model;

import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private String id;
    private String name;
    private String priority;

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
                    .map(type -> new CategoryDto(String.valueOf(type.id), type.name, String.valueOf(type.id)))
                    .collect(Collectors.toList());
        }

    }

    public static CategoryDto create(Integer id, String name, Integer priority) {
        return new CategoryDto(String.valueOf(id), name, String.valueOf(priority));
    }

}
