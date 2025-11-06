package ru.codekitchen.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.codekitchen.repository.RecordRepository;
import ru.codekitchen.entity.Record;
import ru.codekitchen.entity.RecordStatus;
import ru.codekitchen.entity.dto.RecordsContainerDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecordService {
    private final RecordRepository recordRepository;

    @Autowired
    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public RecordsContainerDto findAllRecords(String filterMode) {
 //       System.out.println("1) ### recordDao.findAllRecords() = " + recordDao.findAllRecords());

        // получаем все записи
        List<Record> records = recordRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        // считаем статистику
        int numberOfDoneRecords = (int) records.stream().filter(record -> record.getStatus() == RecordStatus.DONE).count();
        int numberOfActiveRecords = (int) records.stream().filter(record -> record.getStatus() == RecordStatus.ACTIVE).count();

        if (filterMode == null || filterMode.isBlank()) {
            return new RecordsContainerDto(records, numberOfDoneRecords, numberOfActiveRecords);
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
            return new RecordsContainerDto(filterRecords, numberOfDoneRecords, numberOfActiveRecords);
        } else {
            return new RecordsContainerDto(records, numberOfDoneRecords, numberOfActiveRecords);
        }
    }

    public void saveRecord(String title) {
        if (title != null && !title.isBlank()) {
            recordRepository.save(new Record(title));
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
