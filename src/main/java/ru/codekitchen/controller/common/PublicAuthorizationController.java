package ru.codekitchen.controller.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.codekitchen.entity.User;
import ru.codekitchen.entity.UserRole;
import ru.codekitchen.service.UserService;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Controller
public class PublicAuthorizationController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // !здесь @Autowired указывает Spring, что ему нужно найти бин типа PasswordEncoder и передать его в конструктор
    @Autowired
    public PublicAuthorizationController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, @RequestParam(required = false) String error) {
      log.info("1) ########### error = " + error);
        if (error != null) {
            log.info("2) ########### error");
            model.addAttribute("isAuthenticationFailed", true);
        }
        return "public/authorization/login-page";
    }

    @GetMapping("/registration")
    public String getRegistrationPage() {
        return "public/authorization/registration-page";
    }

    @PostMapping("/registration")
    public String createUserAccount(@RequestParam String name
                                , @RequestParam String email
                                , @RequestParam String password) {
        String encodedPassword = passwordEncoder.encode(password);
        userService.save(new User(name, email, encodedPassword, UserRole.USER));
        forceAutoLogin(email, encodedPassword); // вызываем из регистрации, чтобы сразу залогироваться после него

        log.info("###### /registration securityFilterChain: " + SecurityContextHolder.getContext().getAuthentication().getName());
        log.info("###### /registration securityFilterChain: " + SecurityContextHolder.getContext().toString());

        return "redirect:/account";
    }

    // автологирование (вызов логирования из кода) - вызывается из регистрации, чтобы сразу залогироваться после него
    private void forceAutoLogin(String email, String password) {
        Set<SimpleGrantedAuthority> roles = Collections.singleton(UserRole.USER.toAuthority());
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password, roles);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
