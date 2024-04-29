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
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;

public class FindAllItemsTests {
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
    void testFindAllItems() {
        Item item1 = new Item("Item 1", "Description 1");
        Item item2 = new Item("Item 2", "Description 2");

        given(itemRepository.findAll())
                .willReturn(Arrays.asList(item1, item2));

        Flux<Item> itemFlux = appService.findAllItems();
        StepVerifier.create(itemFlux)
                .expectNext(item1, item2)
                .verifyComplete();
    }
}
