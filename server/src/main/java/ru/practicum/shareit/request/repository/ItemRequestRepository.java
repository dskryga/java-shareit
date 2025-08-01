package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long requesterId);

    //Возвращает все реквесты, кроме реквестов от пользователя, отправившего запрос
    List<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Long userId);
}
