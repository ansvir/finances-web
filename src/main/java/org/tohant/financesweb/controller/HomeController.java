package org.tohant.financesweb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.tohant.financesweb.service.analysis.PaymentAnalysisService;
import org.tohant.financesweb.service.database.PaymentService;
import org.tohant.financesweb.service.database.UserService;
import org.tohant.financesweb.service.model.CategoryDto;
import org.tohant.financesweb.service.model.PaymentDto;
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
    public ModelAndView getMainPage(Model model) {
        populateModel(model);
        return FinancesThymeleafUtil.buildMav(HOME_PAGE_NAME, model);
    }

    @PostMapping(value = "/" + HOME_PAGE_NAME)
    public ModelAndView addPayment(PaymentDto payment, Model model) {
        paymentService.save(payment);
        populateModel(model);
        return FinancesThymeleafUtil.buildMav(HOME_PAGE_NAME, model);
    }

    private void populateModel(Model model) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PaymentDto> payments = paymentService.findAll();
        model.addAttribute("payments", payments);
        model.addAttribute("paymentsByMonth", paymentAnalysisService.getPaymentsByMonths(payments));
        List<CategoryDto> categories = userService.findByUsername(currentUser).getCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("categoriesMap", categories.stream()
                .collect(Collectors.toMap(CategoryDto::getId, value -> value)));
        model.addAttribute("summaryStat", paymentAnalysisService.getSummaryStats(payments, categories));
    }

}
