package org.tohant.financesweb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tohant.financesweb.api.sheet.SheetsService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SheetsService localGoogleSheetsService;

    @RequestMapping(value = "/home", method = { RequestMethod.GET, RequestMethod.POST })
    public String getMainPage() {
        return "home";
    }

    @PostMapping(value = "payment")
    public String addPayment(Model model) {
        model.asMap().values().forEach(System.out::println);
        localGoogleSheetsService.getPayments()
                .forEach(p -> System.out.println(p.getName() + " " + p.getType().getName()));
        return "home";
    }

}
