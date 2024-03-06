package org.tohant.financesweb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tohant.financesweb.api.model.HttpResponseDto;
import org.tohant.financesweb.api.model.PaymentDto;
import org.tohant.financesweb.api.sheet.SheetsService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private static final String HOME_PAGE_NAME = "home";

    private final SheetsService localGoogleSheetsService;

    @RequestMapping(value = "/home", method = { RequestMethod.GET, RequestMethod.POST })
    public String getMainPage(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("payments", localGoogleSheetsService.getPayments(page));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", localGoogleSheetsService.countCached());
        return HOME_PAGE_NAME;
    }

    @PostMapping(value = "/payment")
    public String addPayment(@RequestParam(defaultValue = "0") int page, PaymentDto payment, Model model) {
        HttpResponseDto response = localGoogleSheetsService.addPayment(payment);
        if (response.getStatusCode() != HttpStatus.OK.value()) {
            log.warn("Http status of adding payment response is not OK (200). Status is: "
                    + response.getStatusCode() + ". Body: " + response.getContent());
        }
        return getMainPage(page, model);
    }

}
