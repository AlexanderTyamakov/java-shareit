package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    List<Item> findAllByOwnerIsOrderById(long userId);

    List<Item> findAllByRequestIs(long requestId);

    List<Item> findAllByRequestIn(List<Long> id);


}
