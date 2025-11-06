package ru.codekitchen.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.codekitchen.entity.Record;
import ru.codekitchen.entity.RecordStatus;

import java.util.List;
import java.util.Optional;


@Repository
public interface RecordRepository extends JpaRepository<Record, Integer> {

    @Modifying
    @Transactional  // без этой анотации была ошибка
    @Query("UPDATE Record SET status = :status WHERE id = :id")
    void update(int id, @Param("status") RecordStatus newStatus);   // HQL-вариант
/*
// OK альтернативный native-вариант, где статус передается, как newStatus.ordinal() - как число, а не объект RecordStatus
    @Modifying
    @Transactional
    @Query(value = "UPDATE records SET status = :newStatus WHERE id = :id", nativeQuery = true)
    void update(int id, int newStatus);
*/

    List<Record> findAllByStatus(RecordStatus status);
    List<Record> findAllByStatusAndTitleContainsOrderByIdDesc(RecordStatus status, String titlePart);
    int countAllByStatus(RecordStatus status);
    Optional<Record> findFirstByTitleContains(String titlePart);
}