package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    ItemRequest findFirstByOrderByIdDesc();

    List<ItemRequest> findAllByRequestorIsOrderByCreatedDesc(User user);

    List<ItemRequest> findAllByRequestorNotOrderByCreatedDesc(User user);

    Page<ItemRequest> findAllByRequestorNot(User user, Pageable pageable);

}
