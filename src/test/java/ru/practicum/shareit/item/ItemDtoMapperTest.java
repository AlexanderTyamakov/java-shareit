package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemDtoMapperTest {

    @Test
    public void patchToItem() {
        Item item = new Item(1L, "Item", "Strong", true, 2L, 3L);

        ItemDto itemDto1 = new ItemDto(1L, "name", "description", false, 5L, null,
                null,
                new ArrayList<>()
        );
        ItemDto itemDto2 = new ItemDto(1L, null, "description", false, 5L, null,
                null,
                new ArrayList<>()
        );
        ItemDto itemDto3 = new ItemDto(1L, "name", null, false, 5L, null,
                null,
                new ArrayList<>()
        );
        ItemDto itemDto4 = new ItemDto(1L, "name", "description", null, 5L, null,
                null,
                new ArrayList<>()
        );
        ItemDto itemDto5 = new ItemDto(1L, "name", "description", true, null, null,
                null,
                new ArrayList<>()
        );

        Item patchedItem1 = ItemDtoMapper.patchToItem(itemDto1, item, 1L);
        assertEquals(itemDto1.getName(), patchedItem1.getName());
        assertEquals(itemDto1.getDescription(), patchedItem1.getDescription());
        assertEquals(itemDto1.getAvailable(), patchedItem1.getAvailable());
        assertEquals(itemDto1.getRequestId(), patchedItem1.getRequest());

        Item patchedItem2 = ItemDtoMapper.patchToItem(itemDto2, item, 1L);
        assertEquals(item.getName(), patchedItem2.getName());
        assertEquals(itemDto2.getDescription(), patchedItem2.getDescription());
        assertEquals(itemDto2.getAvailable(), patchedItem2.getAvailable());
        assertEquals(itemDto2.getRequestId(), patchedItem2.getRequest());

        Item patchedItem3 = ItemDtoMapper.patchToItem(itemDto3, item, 1L);
        assertEquals(itemDto3.getName(), patchedItem3.getName());
        assertEquals(item.getDescription(), patchedItem3.getDescription());
        assertEquals(itemDto3.getAvailable(), patchedItem3.getAvailable());
        assertEquals(itemDto3.getRequestId(), patchedItem3.getRequest());

        Item patchedItem4 = ItemDtoMapper.patchToItem(itemDto4, item, 1L);
        assertEquals(itemDto4.getName(), patchedItem4.getName());
        assertEquals(itemDto4.getDescription(), patchedItem4.getDescription());
        assertEquals(item.getAvailable(), patchedItem4.getAvailable());
        assertEquals(itemDto4.getRequestId(), patchedItem4.getRequest());

        Item patchedItem5 = ItemDtoMapper.patchToItem(itemDto5, item, 1L);
        assertEquals(itemDto5.getName(), patchedItem5.getName());
        assertEquals(itemDto5.getDescription(), patchedItem5.getDescription());
        assertEquals(itemDto5.getAvailable(), patchedItem5.getAvailable());
        assertEquals(item.getRequest(), patchedItem5.getRequest());


    }
}
