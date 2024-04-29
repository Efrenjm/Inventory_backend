package com.backend.inventory.service.AppServiceTests;

import com.backend.inventory.dao.ItemRepository;
import com.backend.inventory.dao.LocationRepository;
import com.backend.inventory.entity.Item;
import com.backend.inventory.service.AppServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

public class FindItemByIdTests {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private AppServiceImpl appService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindItemById_existingId() {
        int itemId = 200;
        Item item = new Item(itemId, "Item 1");

        given(itemRepository.findById(itemId))
                .willReturn(Optional.of(item));

        Item result = appService.findItemById(itemId);

        assertThat(result, is(equalTo(item)));
    }

    @Test
    void testFindItemById_nonExistingId() {
        int itemId = 404;
        given(itemRepository.findById(itemId))
                .willReturn(Optional.empty());

        ResponseStatusException expectedException = assertThrows(ResponseStatusException.class,
                () -> appService.findItemById(itemId));
        assertThat(expectedException.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
        assertThat(expectedException.getMessage(),
                containsString("The provided `id` " + itemId + " is not associated with a valid item."));
    }
}
