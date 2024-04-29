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
import static org.mockito.Mockito.verify;

public class DeleteItemTests {
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
    void testDeleteItem_existingItem() {
        int itemId = 1;
        given(itemRepository.findById(itemId))
                .willReturn(Optional.of(new Item()));
        appService.deleteItem(itemId);
        verify(itemRepository).deleteById(itemId);
    }

    @Test
    void testDeleteItem_nonExistingItem() {
        int itemId = 1;
        given(itemRepository.findById(itemId))
                .willReturn(Optional.empty());

        ResponseStatusException expectedException = assertThrows(ResponseStatusException.class, () -> appService.deleteItem(itemId));
        assertThat(expectedException.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
        assertThat(expectedException.getMessage(),
                containsString("The provided `id` " + itemId + " is not associated with a valid item."));
    }
}
