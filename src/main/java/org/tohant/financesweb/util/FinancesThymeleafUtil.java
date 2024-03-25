package org.tohant.financesweb.util;

import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.tohant.financesweb.service.model.PaymentDto;
import org.tohant.financesweb.service.model.PaymentMonthDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

@NoArgsConstructor
public class FinancesThymeleafUtil {

    public Comparator<PaymentMonthDto> getPaymentMonthDateComparator() {
        return Comparator.comparing(PaymentMonthDto::getDate,
                (o1, o2) -> o1.isBefore(o2) ? 1 : o1.isEqual(o2)
                        ? 0 : -1);
    }

    public Comparator<PaymentDto> getPaymentDateComparator() {
        return Comparator.comparing(PaymentDto::getDateTime,
                (o1, o2) -> {
                    LocalDateTime first = LocalDateTime.parse(o1);
                    LocalDateTime second = LocalDateTime.parse(o2);
                    return first.isBefore(second) ? 1 : first.isEqual(second)
                            ? 0 : -1;
                });
    }

    public static ModelAndView buildMav(String viewName, Model model) {
        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addAllObjects(model.asMap());
        return modelAndView;
    }

}
