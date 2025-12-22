package ru.codekitchen.entity.dto;

import lombok.Getter;
import ru.codekitchen.entity.Record;

import java.util.List;

@Getter
public class RecordsContainerDto {
    private final String userName;
    private final List<Record> records;
    private final int numberOfDoneRecords;
    private final int numberOfActiveRecords;

    public RecordsContainerDto(String userName, List<Record> records, int numberOfDoneRecords, int numberOfActiveRecords) {
        this.userName = userName;
        this.records = records;
        this.numberOfDoneRecords = numberOfDoneRecords;
        this.numberOfActiveRecords = numberOfActiveRecords;
    }
}
