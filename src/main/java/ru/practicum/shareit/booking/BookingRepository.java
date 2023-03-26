package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

    List<Booking> findAllByBookerIsOrderByStartDesc(long id);

    List<Booking> findAllByBookerIsAndStatusIsOrderByStartDesc(long id, BookingStatus bookingStatus);

    List<Booking> findAllByItemInAndStatusIsOrderByStartDesc(List<Long> items, BookingStatus bookingStatus);

    List<Booking> findAllByItemInOrderByStartDesc(List<Long> items);
}
