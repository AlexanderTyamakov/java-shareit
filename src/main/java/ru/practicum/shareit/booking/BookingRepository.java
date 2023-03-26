package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

    List<Booking> findAllByBookerIsOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIsAndStatusIsOrderByStartDesc(long id, BookingStatus bookingStatus);

    List<Booking> findAllByItemInAndStatusIsOrderByStartDesc(List<Long> items, BookingStatus bookingStatus);

    List<Booking> findAllByItemInOrderByStartDesc(List<Long> items);

    List<Booking> findAllByItemIsAndStatusNot(Long itemId, BookingStatus bookingStatus);

    List<Booking> findAllByItemInAndStatusIsNot(List<Long> itemsId, BookingStatus bookingStatus);
}
