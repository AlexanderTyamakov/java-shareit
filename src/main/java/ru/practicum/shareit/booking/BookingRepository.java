package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

    List<Booking> findAllByBookerIsOrderByStartDesc(User user);

    List<Booking> findAllByBookerIsAndStatusIsOrderByStartDesc(User user, BookingStatus bookingStatus);

    List<Booking> findAllByItemInAndStatusIsOrderByStartDesc(List<Item> items, BookingStatus bookingStatus);

    List<Booking> findAllByItemInOrderByStartDesc(List<Item> items);

    List<Booking> findAllByItemIsAndStatusNot(Item item, BookingStatus bookingStatus);

    List<Booking> findAllByItemInAndStatusIsNot(List<Item> items, BookingStatus bookingStatus);

}
