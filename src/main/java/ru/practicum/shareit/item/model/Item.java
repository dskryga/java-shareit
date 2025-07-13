package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class Item {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column(name = "is_available")
    private Boolean available;
    @Column(name = "user_id")
    private Long ownerId;
    @Column(name = "request_id")
    private Long requestId;
}
