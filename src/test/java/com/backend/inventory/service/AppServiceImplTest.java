package com.backend.inventory.service;
import com.backend.inventory.dao.ItemRepository;
import com.backend.inventory.dao.LocationRepository;
import com.backend.inventory.entity.Item;
import com.backend.inventory.entity.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class AppServiceImplTest {
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

    @Test
    void testFindItemsByState_recordedState() {
        Location location1 = new Location(1, "firstState", "address", null);
        Location location2 = new Location(2, "secondState", "address", null);
        Item item1 = new Item(1, "Item 1", "Description 1", location1);
        Item item2 = new Item(2, "Item 2", "Description 2", location1);
        Item item3 = new Item(3, "Item 3", "Description 3", location2);

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
    void testFindItemsByState_nonRecordedState() {
        Item item1 = new Item("Item 1", "Description 1");
        Item item2 = new Item("Item 2", "Description 2");

        given(itemRepository.findAll())
                .willReturn(Arrays.asList(item1, item2));

        Flux<Item> itemFlux = appService.findItemsByState("non recorded state name");
        StepVerifier.create(itemFlux)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testFindItemById_recordedId() {
        Item item = new Item(200, "Item 1", "Description 1");

        given(itemRepository.findById(200))
                .willReturn(Optional.of(item));

        Item result = appService.findItemById(200);

        assertThat(result, is(equalTo(item)));
    }

    @Test
    void testFindItemById_nonRecordedId() {
        given(itemRepository.findById(404))
                .willReturn(Optional.of(null));

        Item result = appService.findItemById(404);

        assertThat(result, is(equalTo(null)));
    }


//    Item createItem(int id, String name, String description, int location_id);
    @Test
    void testCreateItem_expectedRequest() {
        Location location = new Location(1, "state", "address", null)
        Item newItem = new Item(1, "new item", null, location);


    }
    @Test
    void testCreatedItem_missingProperties() {

    }
    @Test
    void testCreatedItem_duplicatedId() {

    }



//    void deleteItem(int itemId);
//    @Test
//    void testFindItemsByState() {
//        Item item1 = new Item("Item 1", "Description 1");
//        Item item2 = new Item("Item 2", "Description 2");
//        Location location1 = new Location("State 1");
//        Location location2 = new Location("State 2");
//        item1.setLocation(location1);
//        item2.setLocation(location2);
//        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));
//
//        Flux<Item> itemFlux = appService.findItemsByState("State 1");
//
//        StepVerifier.create(itemFlux)
//                .expectNext(item1)
//                .verifyComplete();
//    }
//
//    // Similar tests for createItem() and deleteItem() methods
}
