package org.tohant.financesweb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tohant.financesweb.service.analysis.PaymentAnalysisService;
import org.tohant.financesweb.service.database.CategoryService;
import org.tohant.financesweb.service.database.PaymentService;
import org.tohant.financesweb.service.database.UserService;
import org.tohant.financesweb.service.model.HttpResponseDto;
import org.tohant.financesweb.service.model.PaymentDto;
import org.tohant.financesweb.service.sheet.SheetService;

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
    public String getMainPage(Model model) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PaymentDto> payments = paymentService.findAll();
        model.addAttribute("payments", payments);
        model.addAttribute("paymentsByMonth", paymentAnalysisService.getPaymentsByMonths(payments));
        model.addAttribute("categories", userService.findByUsername(currentUser).getCategories());
        return HOME_PAGE_NAME;
    }

    @PostMapping(value = "/payment")
    public String addPayment(PaymentDto payment, Model model) {
        paymentService.save(payment);
        return getMainPage(model);
    }

}
