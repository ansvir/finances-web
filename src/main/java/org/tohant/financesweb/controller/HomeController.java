package org.tohant.financesweb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.tohant.financesweb.service.analysis.PaymentAnalysisService;
import org.tohant.financesweb.service.database.PaymentService;
import org.tohant.financesweb.service.database.UserService;
import org.tohant.financesweb.service.model.PaymentDto;
import org.tohant.financesweb.service.model.PeriodDto;
import org.tohant.financesweb.util.FinancesThymeleafUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private static final String HOME_PAGE_NAME = "home";

    private final UserService userService;
    private final PaymentService paymentService;
    private final PaymentAnalysisService paymentAnalysisService;

    @RequestMapping(value = "/home", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView getMainPage(PeriodDto periodDto, Model model) {
        populateModel(periodDto, model);
        return FinancesThymeleafUtil.buildMav(HOME_PAGE_NAME, model);
    }

    @PostMapping(value = "/payment")
    public ModelAndView addPayment(PeriodDto periodDto, PaymentDto payment, Model model) {
        paymentService.save(payment);
        populateModel(periodDto, model);
        return FinancesThymeleafUtil.buildMav(HOME_PAGE_NAME, model);
    }

    private void populateModel(PeriodDto periodDto, Model model) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PaymentDto> payments = paymentService.findAll();
        model.addAttribute("payments", payments);
        model.addAttribute("paymentsByMonth", paymentAnalysisService.getPaymentsByMonths(payments));
        model.addAttribute("categories", userService.findByUsername(currentUser).getCategories());
        List<LocalDate> extremumDates = extractExtremumDates(payments);
        if (periodDto.getPrioritiesDateFrom() == null
                || periodDto.getPrioritiesDateTo() == null) {
            LocalDate from = extremumDates.get(0);
            LocalDate to = extremumDates.get(1);
            model.addAttribute("paymentsByPeriod", paymentService.findAllByPeriodGroupedByCategories(from, to));
            model.addAttribute("period", new PeriodDto(from.toString(), to.toString()));
            model.addAttribute("allPaymentsByPeriod", paymentAnalysisService.getPaymentsByPeriod(payments, from, to));
        } else {
            model.addAttribute("paymentsByPeriod", paymentService.findAllByPeriodGroupedByCategories(LocalDate.parse(periodDto.getPrioritiesDateFrom()),
                    LocalDate.parse(periodDto.getPrioritiesDateTo())));
            model.addAttribute("period", periodDto);
            model.addAttribute("allPaymentsByPeriod", paymentAnalysisService.getPaymentsByPeriod(payments,
                    LocalDate.parse(periodDto.getPrioritiesDateFrom()), LocalDate.parse(periodDto.getPrioritiesDateTo())));
        }
    }

    private List<LocalDate> extractExtremumDates(List<PaymentDto> payments) {
        List<LocalDate> dates = new ArrayList<>();
        dates.add(payments.stream()
                .min(Comparator.comparing(PaymentDto::getDateTime))
                .map(payment -> LocalDate.parse(payment.getDateTime().split("T")[0]))
                .orElse(LocalDate.parse("2000-01-01")));
        dates.add(payments.stream()
                .max(Comparator.comparing(PaymentDto::getDateTime))
                .map(payment -> LocalDate.parse(payment.getDateTime().split("T")[0]))
                .orElse(LocalDate.parse("2025-12-01")));
        return dates;
    }

}
