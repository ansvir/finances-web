package org.tohant.financesweb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.tohant.financesweb.service.analysis.PaymentAnalysisService;
import org.tohant.financesweb.service.model.HttpResponseDto;
import org.tohant.financesweb.service.sheet.SheetService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private static final String PROFILE_PAGE_NAME = "profile";

    private final SheetService localGoogleSheetsService;
    private final PaymentAnalysisService paymentAnalysisService;

    @GetMapping(value = "/profile")
    public String getMainPage(Model model) {
        model.addAttribute("categories", localGoogleSheetsService.getCategories());
        return PROFILE_PAGE_NAME;
    }

    @PostMapping(value = "/profile/category/rearrange")
    public String updatePriorities(List<Integer> categories, Model model) {
        HttpResponseDto response = localGoogleSheetsService.rearrangeCategories(categories);
        if (response.getStatusCode() != HttpStatus.OK.value()) {
            log.warn("Http status of rearranging categories response is not OK (200). Status is: "
                    + response.getStatusCode() + ". Body: " + response.getContent());
        }
        return getMainPage(model);
    }

}
