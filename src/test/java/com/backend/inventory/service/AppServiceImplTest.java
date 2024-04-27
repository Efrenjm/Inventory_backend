package com.backend.inventory.service;

import com.backend.inventory.dao.ItemRepository;
import com.backend.inventory.dao.LocationRepository;
import com.backend.inventory.entity.Item;
import com.backend.inventory.entity.Location;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

//@RunWith(MockitoJUnitRunner.class)
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
    void testFindItemsByState_existingState() {
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
    void testFindItemsByState_nonExistingState() {
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

    @Test
    void testCreateItem_expectedRequest() {
        int itemId = 1;
        int locationId = 1;
        Location location = new Location(locationId, "state", "address", null);
        Item expectedItem = new Item(itemId, "new item", null, location);
        given(locationRepository.findById(locationId))
                .willReturn(Optional.of(location));

        given(itemRepository.findById(itemId))
                .willReturn(Optional.empty());

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        given(itemRepository.save(itemCaptor.capture()))
                .willReturn(expectedItem);

        appService.createItem(expectedItem);

        assertThat(itemCaptor.getValue().toString(), is(equalTo(expectedItem.toString())));
    }

    @Test
    void testCreatedItem_negativeItemIdAndLocationId() {
        Location location = new Location(-1, "state", null, null);
        Item properties = new Item(-1, "name", "description", location);
        ResponseStatusException expectedException = assertThrows(ResponseStatusException.class,
                () -> appService.createItem(properties));

        assertThat(expectedException.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(expectedException.getMessage(),
                containsString("The properties: `id`, `location.id` "
                        + "are invalid or missing."));
    }

    @Test
    void testCreatedItem_missingIdAndLocationState() {
        Location location = new Location(1, null);
        Item properties = new Item("name", "description");
        properties.setLocation(location);
        ResponseStatusException expectedException = assertThrows(ResponseStatusException.class,
                () -> appService.createItem(properties));

        assertThat(expectedException.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(expectedException.getMessage(),
                containsString("The properties: `id`, `location.state` "
                        + "are invalid or missing."));
    }

    @Test
    void testCreatedItem_missingName() {
        Location location = new Location(1, "state");
        Item properties = new Item(1, null, location);

        ResponseStatusException expectedException = assertThrows(ResponseStatusException.class,
                () -> appService.createItem(properties));

        assertThat(expectedException.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(expectedException.getMessage(),
                containsString("The property: `name` is invalid or missing."));
    }

    @Test
    void testCreatedItem_missingLocation() {
        Item properties = new Item(1, "name", "description");
        ResponseStatusException expectedException = assertThrows(ResponseStatusException.class,
                () -> appService.createItem(properties));

        assertThat(expectedException.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(expectedException.getMessage(),
                containsString("The properties: `location.id`, `location.state` are invalid or missing."));
    }


    @Test
    void testCreatedItem_duplicatedItemId() {
        int itemId = 1;
        int locationId = 1;
        Location location = new Location(locationId, "state", "address", null);
        given(locationRepository.findById(locationId))
                .willReturn(Optional.of(location));

        given(itemRepository.findById(itemId))
                .willReturn(Optional.of(new Item()));

        Item item = new Item(itemId, "new item", null, location);
        ResponseStatusException expectedException = assertThrows(ResponseStatusException.class,
                () -> appService.createItem(item));
        assertThat(expectedException.getStatusCode(), is(equalTo(HttpStatus.CONFLICT)));
        assertThat(expectedException.getMessage(),
                containsString("Another item with the same `id`: "
                        + itemId + " already exists. Please provide a unique item `id`."));
    }

    @Test
    void testCreatedItem_duplicatedLocationId() {
        int itemId = 1;
        int locationId = 1;
        Location dbLocation = new Location(locationId, "state", "address", null);
        Location requestLocation = new Location(locationId, "another_state", "another_address", 10L);
        given(locationRepository.findById(locationId))
                .willReturn(Optional.of(dbLocation));

        Item requestedItem = new Item(itemId, "new item", null, requestLocation);
        Item expectedItem = new Item(itemId, "new item", null, dbLocation);

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        given(itemRepository.save(itemCaptor.capture()))
                .willReturn(expectedItem);

        appService.createItem(requestedItem);

        assertThat(itemCaptor.getValue().toString(), is(equalTo(expectedItem.toString())));

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