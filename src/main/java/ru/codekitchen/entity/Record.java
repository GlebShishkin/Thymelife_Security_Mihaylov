package ru.codekitchen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "records")
@Getter
@Setter
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "status", nullable = false)
    private RecordStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Record() {
    }

    public Record(String title, User user) {
        this.title = title;
        this.status = RecordStatus.ACTIVE;
        this.user = user;
    }
}
