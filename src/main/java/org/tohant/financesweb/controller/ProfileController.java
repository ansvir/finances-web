package org.tohant.financesweb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.tohant.financesweb.service.database.CategoryService;
import org.tohant.financesweb.service.database.ProfileService;
import org.tohant.financesweb.service.model.CategoriesRearrangePrioritiesDto;
import org.tohant.financesweb.service.model.CategoryIdDto;
import org.tohant.financesweb.util.FinancesThymeleafUtil;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private static final String PROFILE_PAGE_NAME = "profile";

    private final CategoryService categoryService;
    private final ProfileService profileService;

    @GetMapping(value = "/" + PROFILE_PAGE_NAME)
    public ModelAndView getMainPage(Model model) {
        populateModel(model);
        return FinancesThymeleafUtil.buildMav(PROFILE_PAGE_NAME, model);
    }

    @PutMapping(value = "/" + PROFILE_PAGE_NAME)
    public ModelAndView updatePriorities(CategoriesRearrangePrioritiesDto priorities, Model model) {
        categoryService.rearrangeCategories(priorities.getPriorities().stream()
                .map(Integer::longValue)
                .collect(Collectors.toList()));
        populateModel(model);
        return FinancesThymeleafUtil.buildMav(PROFILE_PAGE_NAME, model);
    }

    @PostMapping(value = "/" + PROFILE_PAGE_NAME)
    public ModelAndView addPriority(@RequestParam(name = "default-modal-input") @Valid @NotNull @NotBlank @Size(max = 100,
            message = "Размер имени категории не должен превышать 100 символов") String categoryName, Model model) {
        categoryService.addCategory(categoryName);
        populateModel(model);
        return FinancesThymeleafUtil.buildMav(PROFILE_PAGE_NAME, model);
    }

    @DeleteMapping(value = "/" + PROFILE_PAGE_NAME)
    public ModelAndView deleteCategory(CategoryIdDto category, Model model) {
        profileService.deleteCategoryFromProfile(Long.parseLong(category.getRearrangedId()),
                Long.parseLong(category.getDeletedId()));
        populateModel(model);
        return FinancesThymeleafUtil.buildMav(PROFILE_PAGE_NAME, model);
    }


    private void populateModel(Model model) {
        model.addAttribute("categories", categoryService.findAllByCurrentUserOrderByPriority());
    }

}
