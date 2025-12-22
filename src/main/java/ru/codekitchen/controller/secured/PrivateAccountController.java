package ru.codekitchen.controller.secured;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.codekitchen.entity.RecordStatus;
import ru.codekitchen.entity.User;
import ru.codekitchen.entity.dto.RecordsContainerDto;
import ru.codekitchen.service.RecordService;
import ru.codekitchen.service.UserService;

@Slf4j
@Controller
@RequestMapping("/account")
public class PrivateAccountController {

    private final UserService userService;
    private final RecordService recordService;

    @Autowired
    public PrivateAccountController(RecordService recordService, UserService userService) {
        this.recordService = recordService;
        this.userService = userService;
    }

    // метод, кот служит для отображения страницы (заполняет Model)
    @GetMapping
    public String getMainPage(Model model, @RequestParam(name = "filter", required = false) String filterMode) {
//        User user = userService.getCurrentUser();
        RecordsContainerDto container = recordService.findAllRecords(filterMode);
        model.addAttribute("userName", container.getUserName());
        model.addAttribute("records", container.getRecords());
        model.addAttribute("numberOfDoneRecords", container.getNumberOfDoneRecords());
        model.addAttribute("numberOfActiveRecords", container.getNumberOfActiveRecords());
        return "private/account-page";
    }

    // добавляем запись в список
    @PostMapping("/add-record")
    public String addRecord(@RequestParam String title) {
        recordService.saveRecord(title);
        return "redirect:/account";
    }

    // ставим запись в списке в статус "done"
    @PostMapping("/make-record-done")
    public String makeRecordDone(@RequestParam int id
                            , @RequestParam(name="filter", required = false) String filterMode) {
//        log.info("1) ############## repository: id = " + id + "; filterMode = " + filterMode);
        recordService.updateRecordStatus(id, RecordStatus.DONE);
        return "redirect:/account" + (filterMode != null && !filterMode.isBlank() ? "?filter=" + filterMode : "");
    }

    @PostMapping("/delete-record")
    public String deleteRecord(@RequestParam int id,
                            @RequestParam(name="filter", required = false) String filterMode) {
        recordService.deleteRecord(id);
        return "redirect:/account" + (filterMode != null && !filterMode.isBlank() ? "?filter=" + filterMode : "");
    }
}
