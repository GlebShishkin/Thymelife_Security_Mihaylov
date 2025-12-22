package ru.codekitchen.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.codekitchen.entity.User;
import ru.codekitchen.repository.RecordRepository;
import ru.codekitchen.entity.Record;
import ru.codekitchen.entity.RecordStatus;
import ru.codekitchen.entity.dto.RecordsContainerDto;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecordService {
    private final UserService userService;
    private final RecordRepository recordRepository;

    @Autowired
    public RecordService(UserService userService, RecordRepository recordRepository) {
        this.userService = userService;
        this.recordRepository = recordRepository;
    }

    public RecordsContainerDto findAllRecords(String filterMode) {
 //       System.out.println("1) ### recordDao.findAllRecords() = " + recordDao.findAllRecords());

        User user = userService.getCurrentUser();

        // получаем все записи
        List<Record> records = user.getRecords().stream()
                .sorted(Comparator.comparingInt(Record::getId))
                .collect(Collectors.toList());
//        List<Record> records = recordRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        // считаем статистику
        int numberOfDoneRecords = (int) records.stream().filter(record -> record.getStatus() == RecordStatus.DONE).count();
        int numberOfActiveRecords = (int) records.stream().filter(record -> record.getStatus() == RecordStatus.ACTIVE).count();

        if (filterMode == null || filterMode.isBlank()) {
            return new RecordsContainerDto(user.getName(), records, numberOfDoneRecords, numberOfActiveRecords);
        }

        String filterModeInUpperCase = filterMode.toUpperCase();
        List<String> allowedFilterModes = Arrays.stream(RecordStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        if (allowedFilterModes.contains(filterModeInUpperCase)) {
            // фильтруем все записи
            List<Record> filterRecords = records.stream()
                    .filter(record -> record.getStatus() == RecordStatus.valueOf(filterModeInUpperCase))
                    .collect(Collectors.toList());
            return new RecordsContainerDto(user.getName(), filterRecords, numberOfDoneRecords, numberOfActiveRecords);
        } else {
            return new RecordsContainerDto(user.getName(), records, numberOfDoneRecords, numberOfActiveRecords);
        }
    }

    public void saveRecord(String title) {
        if (title != null && !title.isBlank()) {
            User user = userService.getCurrentUser();
            recordRepository.save(new Record(title, user));
        }
    }

    public void updateRecordStatus(int id, RecordStatus newStatus) {
        log.info("1) ############## service updateRecordStatus: id = " + id + "; newStatus = " + newStatus + "; newStatus.ordinal() = " + newStatus.ordinal());
        recordRepository.update(id, newStatus);

//        recordRepository.update(id, newStatus.ordinal()); // альтернативный вариант для native-запроса

/*OK альтернативный вариант
        recordRepository.findById(id).ifPresent(
                record -> {
                    record.setStatus(newStatus);
                    recordRepository.save(record);
                }
        );
 */
    }

    public void deleteRecord(int id) {
        recordRepository.deleteById(id);
    }
}
