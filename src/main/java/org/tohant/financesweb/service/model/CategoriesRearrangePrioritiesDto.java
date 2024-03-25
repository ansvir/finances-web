package org.tohant.financesweb.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriesRearrangePrioritiesDto {

    private List<Integer> priorities;

}
