package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.Pagination;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

    List<Booking> findAllByBooker(Pagination pageRequest, User user);

    List<Booking> findAllByBookerIsAndStatusIs(Pagination pageRequest, User user, BookingStatus bookingStatus);

    List<Booking> findAllByItemInAndStatusIs(Pagination pageRequest, List<Item> items, BookingStatus bookingStatus);

    List<Booking> findAllByItemIn(Pagination pageRequest, List<Item> items);

    List<Booking> findAllByItemIsAndStatusNot(Item item, BookingStatus bookingStatus);

    List<Booking> findAllByItemInAndStatusIsNot(List<Item> items, BookingStatus bookingStatus);

}
