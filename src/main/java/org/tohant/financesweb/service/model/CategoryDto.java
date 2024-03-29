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

    public static CategoryDto create(Integer id, String name, Integer priority) {
        return new CategoryDto(String.valueOf(id), name, String.valueOf(priority));
    }

}
