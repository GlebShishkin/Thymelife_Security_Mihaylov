package ru.codekitchen.controller.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@ControllerAdvice
public class GlobalExceptionHandler implements ErrorController {

    @RequestMapping("/error")
    public String errorPage() {
        return "public/error/error-page";    // кастомная страница для ошибок, кот. отразим по адресу "/error"
    }

    @ExceptionHandler(Throwable.class)
    public String handleThrowable(Throwable throwable) {
        return "redirect:/error";
    }
}
