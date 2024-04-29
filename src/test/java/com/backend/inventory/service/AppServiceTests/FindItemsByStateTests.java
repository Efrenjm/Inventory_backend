package com.backend.inventory.service.AppServiceTests;

import com.backend.inventory.dao.ItemRepository;
import com.backend.inventory.dao.LocationRepository;
import com.backend.inventory.entity.Item;
import com.backend.inventory.entity.Location;
import com.backend.inventory.service.AppServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;

public class FindItemsByStateTests {
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
    void testExistingState() {
        Location location1 = new Location(1, "firstState");
        Location location2 = new Location(2, "secondState");
        Item item1 = new Item(1, "Item 1", location1);
        Item item2 = new Item(2, "Item 2", location1);
        Item item3 = new Item(3, "Item 3", location2);

        given(itemRepository.findAll())
                .willReturn(Arrays.asList(item1, item2, item3));

        Flux<Item> itemFlux = appService.findItemsByState("firstState");
        StepVerifier.create(itemFlux)
                .expectNextCount(2)
                .verifyComplete();

        itemFlux = appService.findItemsByState("secondState");
        StepVerifier.create(itemFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testNonExistingState() {
        Item item1 = new Item("Item 1", "Description 1");
        Item item2 = new Item("Item 2", "Description 2");

        given(itemRepository.findAll())
                .willReturn(Arrays.asList(item1, item2));

        Flux<Item> itemFlux = appService.findItemsByState("NoName");
        StepVerifier.create(itemFlux)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode()
                                        .equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("No items found for state: 'NoName'"))
                .verify();
    }
}
