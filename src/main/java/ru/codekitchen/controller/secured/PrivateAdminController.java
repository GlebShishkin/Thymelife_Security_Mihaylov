package ru.codekitchen.controller.secured;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.codekitchen.entity.User;
import ru.codekitchen.service.UserService;

@Controller
public class PrivateAdminController {

    private final UserService userService;

    @Autowired
    public PrivateAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String getManagementPage(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("userName", user.getName());
        return "private/admin/management-page";
    }
}
