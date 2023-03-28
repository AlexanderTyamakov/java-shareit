package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

    Page<Booking> findAllByBookerOrderByStartDesc(User user, Pageable pageable);

    Page<Booking> findAllByBookerIsAndStatusIsOrderByStartDesc(User user, BookingStatus bookingStatus, Pageable pageable);

    Page<Booking> findAllByItemInAndStatusIsOrderByStartDesc(List<Item> items, BookingStatus bookingStatus, Pageable pageable);

    Page<Booking> findAllByItemInOrderByStartDesc(List<Item> items, Pageable pageable);

    List<Booking> findAllByItemIsAndStatusNot(Item item, BookingStatus bookingStatus);

    List<Booking> findAllByItemInAndStatusIsNot(List<Item> items, BookingStatus bookingStatus);

}
