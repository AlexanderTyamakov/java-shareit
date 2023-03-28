package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query(value = "update Item i set i.name = ?1, i.description = ?2, i.available = ?3 where i.id = ?4")
    void updateItemById(String name, String description, Boolean available, long itemId);

    @Query(value = "select i from Item i " +
            "where i.available = TRUE and " +
            "(LOWER(i.name) LIKE CONCAT('%',?1,'%') or LOWER(i.description) LIKE CONCAT('%',?1,'%'))")
    Page<Item> searchByNameAndDescription(String text, Pageable pageable);
}
