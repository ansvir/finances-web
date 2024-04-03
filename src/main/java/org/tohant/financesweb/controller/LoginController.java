package org.tohant.financesweb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.tohant.financesweb.service.database.UserService;
import org.tohant.financesweb.service.model.UserDto;
import org.tohant.financesweb.service.model.UserLoginDto;
import org.tohant.financesweb.util.FinancesThymeleafUtil;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private static final String LOGIN_PAGE_NAME = "login";
    private static final String HOME_PAGE_NAME = "home";

    private final UserService userService;

    @GetMapping(value = "/")
    public String getLoginPage() {
        return LOGIN_PAGE_NAME;
    }

    @PostMapping(value = "/" + LOGIN_PAGE_NAME)
    public ModelAndView tryLogin(UserLoginDto userLoginDto, Model model) {
        UserDto userDto = userService.getUserOrNull(userLoginDto);
        if (userDto != null) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDto, null, userDto.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return FinancesThymeleafUtil.buildMav(HOME_PAGE_NAME, model);
        } else {
            return FinancesThymeleafUtil.buildMav(LOGIN_PAGE_NAME, model);
        }
    }

}
