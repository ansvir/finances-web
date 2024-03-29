package org.tohant.financesweb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.tohant.financesweb.service.analysis.PaymentAnalysisService;
import org.tohant.financesweb.service.database.CategoryService;
import org.tohant.financesweb.service.database.PaymentService;
import org.tohant.financesweb.service.database.UserService;
import org.tohant.financesweb.service.model.CategoryDto;
import org.tohant.financesweb.service.model.PaymentDto;
import org.tohant.financesweb.service.model.PeriodDto;
import org.tohant.financesweb.util.FinancesThymeleafUtil;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private static final String HOME_PAGE_NAME = "home";

    private final UserService userService;
    private final PaymentService paymentService;
    private final PaymentAnalysisService paymentAnalysisService;

    @GetMapping(value = "/" + HOME_PAGE_NAME)
    public ModelAndView getMainPage(PeriodDto periodDto, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return FinancesThymeleafUtil.buildMav(HOME_PAGE_NAME, model);
        }
        populateModel(periodDto, model);
        return FinancesThymeleafUtil.buildMav(HOME_PAGE_NAME, model);
    }

    @PostMapping(value = "/" + HOME_PAGE_NAME)
    public ModelAndView addPayment(PeriodDto periodDto, @Valid PaymentDto payment, Model model) {
        paymentService.save(payment);
        populateModel(periodDto, model);
        return FinancesThymeleafUtil.buildMav(HOME_PAGE_NAME, model);
    }

    private void populateModel(PeriodDto periodDto, Model model) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PaymentDto> payments = paymentService.findAll();
        model.addAttribute("payments", payments);
        model.addAttribute("paymentsByMonth", paymentAnalysisService.getPaymentsByMonths(payments));
        List<CategoryDto> categories = userService.findByUsername(currentUser).getCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("categoriesMap", categories.stream()
                .collect(Collectors.toMap(CategoryDto::getId, value -> value)));
        model.addAttribute("summaryStat", paymentAnalysisService.getSummaryStats(payments, categories));
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
