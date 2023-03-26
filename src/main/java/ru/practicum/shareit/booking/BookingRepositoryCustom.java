package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface BookingRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query(value = "update Booking b set b.status = ?1 where b.id = ?2")
    void changeStatusById(BookingStatus approved, long bookingId);

    @Query(value = "select b from Booking b where b.booker = ?1 and " +
            " b.start < CURRENT_TIMESTAMP and b.end > CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findAllByBookerAndCurrentOrderByStartDesc(User user);

    @Query(value = "select b from Booking b where b.booker = ?1 and " +
            " b.start > CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findAllByBookerAndFutureOrderByStartDesc(User user);

    @Query(value = "select b from Booking b where b.booker = ?1 and " +
            " b.end < CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findAllByBookerAndPastOrderByStartDesc(User user);

    @Query(value = "select b from Booking b where b.item in (?1) and " +
            " b.start < CURRENT_TIMESTAMP and b.end > CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findAllByItemsAndCurrentOrderByStartDesc(List<Item> items);

    @Query(value = "select b from Booking b where b.item in (?1) and " +
            " b.start > CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findAllByItemsAndFutureOrderByStartDesc(List<Item> items);

    @Query(value = "select b from Booking b where b.item in (?1) and " +
            " b.end < CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findAllByItemAndPastOrderByStartDesc(List<Item> items);

    @Query(value = "select b from Booking b where b.item = ?1 and " +
            " b.booker = ?2 and b.status not in (?3) and b.end < CURRENT_TIMESTAMP ")
    List<Booking> findByItemAndBookerAndStatus(Item item, User user, BookingStatus bookingStatus);
}
