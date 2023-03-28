package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDtoOut getItemRequestById(long userId, long itemRequestId) {
        handleOptionalUser(userRepository.findById(userId), userId);
        ItemRequest itemRequest = handleOptionalItemRequest(itemRequestRepository.findById(itemRequestId), itemRequestId);
        List<Item> items = itemRepository.findAllByRequestIs(itemRequest.getId());
        return ItemRequestDtoMapper.toItemRequestOut(itemRequest, items);
    }

    @Override
    public List<ItemRequestDtoOut> getItemRequestsOfUser(long userId) {
        User user = handleOptionalUser(userRepository.findById(userId), userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIsOrderByCreatedDesc(user);
        return getItemRequestDtoOuts(requests);

    }

    @Override
    public List<ItemRequestDtoOut> getAllItemRequests(long userId, Integer from, Integer size) {
        User user = handleOptionalUser(userRepository.findById(userId), userId);
        List<ItemRequest> listItemRequest = new ArrayList<>();
        Pageable pageable;
        Page<ItemRequest> page;
        Pagination pager = new Pagination(from, size);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        if (size == null) {
            listItemRequest = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(user).stream()
                    .skip(from).collect(toList());
        } else {
            for (int i = pager.getIndex(); i < pager.getTotalPages(); i++) {
                pageable = PageRequest.of(i, pager.getPageSize(), sort);
                page = itemRequestRepository.findAllByRequestorNot(user, pageable);
                listItemRequest.addAll(page.stream().collect(toList()));
                if (!page.hasNext()) {
                    break;
                }
            }
            listItemRequest = listItemRequest.stream().limit(size).collect(toList());
        }
        return getItemRequestDtoOuts(listItemRequest);
    }

    @Override
    public ItemRequestDtoOut saveRequest(long userId, ItemRequestDtoIn itemRequestDtoIn, LocalDateTime localDateTime) {
        User user = handleOptionalUser(userRepository.findById(userId), userId);
        ItemRequest itemRequest = ItemRequestDtoMapper.toItemRequest(itemRequestDtoIn, user, localDateTime);
        itemRequestRepository.save(itemRequest);
        ItemRequest created = itemRequestRepository.findFirstByOrderByIdDesc();
        List<Item> items = itemRepository.findAllByRequestIs(created.getId());
        return ItemRequestDtoMapper.toItemRequestOut(created, items);
    }

    private List<ItemRequestDtoOut> getItemRequestDtoOuts(List<ItemRequest> requests) {
        List<Long> requestsId = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIn(requestsId);
        return requests.stream()
                .map(x -> ItemRequestDtoMapper.toItemRequestOut(x, items.stream()
                        .filter(y -> Objects.equals(y.getRequest(), x.getId())).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    private User handleOptionalUser(Optional<User> user, long id) {
        if (user.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден");
        }
        return user.orElseThrow();
    }

    private Item handleOptionalItem(Optional<Item> item, long id) {
        if (item.isEmpty()) {
            throw new ItemNotFoundException("Вещь с id = " + id + " не найдена");
        }
        return item.orElseThrow();
    }

    private Booking handleOptionalBooking(Optional<Booking> booking, long id) {
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Бронирование с id = " + id + " не найдено");
        }
        return booking.orElseThrow();
    }

    private ItemRequest handleOptionalItemRequest(Optional<ItemRequest> itemRequest, long id) {
        if (itemRequest.isEmpty()) {
            throw new ItemRequestNotFoundException("Запрос вещи с id = " + id + " не найден");
        }
        return itemRequest.orElseThrow();
    }
}
