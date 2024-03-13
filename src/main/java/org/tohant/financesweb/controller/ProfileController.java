package org.tohant.financesweb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.tohant.financesweb.service.analysis.PaymentAnalysisService;
import org.tohant.financesweb.service.database.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private static final String PROFILE_PAGE_NAME = "profile";

    private final CategoryService categoryService;

    @GetMapping(value = "/profile")
    public String getMainPage(Model model) {
        model.addAttribute("categories", categoryService.findAllOrderedByPriority());
        return PROFILE_PAGE_NAME;
    }

    @PostMapping(value = "/profile/category/rearrange")
    public String updatePriorities(List<Integer> categories, Model model) {
        categoryService.rearrangeCategories(categories.stream()
                .map(Integer::longValue)
                .collect(Collectors.toList()));
        return getMainPage(model);
    }

}
