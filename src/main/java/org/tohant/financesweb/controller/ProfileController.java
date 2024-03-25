package org.tohant.financesweb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.tohant.financesweb.service.database.CategoryService;
import org.tohant.financesweb.service.model.CategoriesRearrangePrioritiesDto;
import org.tohant.financesweb.util.FinancesThymeleafUtil;

import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private static final String PROFILE_PAGE_NAME = "profile";

    private final CategoryService categoryService;

    @GetMapping(value = "/profile")
    public ModelAndView getMainPage(Model model) {
        populateModel(model);
        return FinancesThymeleafUtil.buildMav(PROFILE_PAGE_NAME, model);
    }

    @PostMapping(value = "/profile/category/rearrange")
    public ModelAndView updatePriorities(CategoriesRearrangePrioritiesDto priorities, Model model) {
        categoryService.rearrangeCategories(priorities.getPriorities().stream()
                .map(Integer::longValue)
                .collect(Collectors.toList()));
        populateModel(model);
        return FinancesThymeleafUtil.buildMav(PROFILE_PAGE_NAME, model);
    }

    @PostMapping(value = "/profile/category/add")
    public ModelAndView addPriority(@RequestParam(name = "default-modal-input") String categoryName, Model model) {
        categoryService.addCategory(categoryName);
        populateModel(model);
        return FinancesThymeleafUtil.buildMav(PROFILE_PAGE_NAME, model);
    }


    private void populateModel(Model model) {
        model.addAttribute("categories", categoryService.findAllByCurrentUserOrderByPriority());
    }

}
