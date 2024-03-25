package org.tohant.financesweb.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeriodDto {
    private String prioritiesDateFrom;
    private String prioritiesDateTo;
}
