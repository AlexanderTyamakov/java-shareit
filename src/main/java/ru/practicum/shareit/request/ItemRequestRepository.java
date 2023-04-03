package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.Pagination;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIsOrderByCreatedDesc(User user);

    List<ItemRequest> findAllByRequestorNot(Pagination pagination, User user);

}
