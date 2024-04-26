package com.backend.inventory.service;

import com.backend.inventory.dao.ItemRepository;
import com.backend.inventory.dao.LocationRepository;
import com.backend.inventory.entity.Item;
import com.backend.inventory.entity.Location;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        Item item = new Item(itemId, "Item 1", "Description 1");

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

        JsonNode properties = objectMapper.createObjectNode()
                .put("id", itemId)
                .put("location_id", locationId)
                .put("name", "new item");
        appService.createItem(properties);

        assertThat(itemCaptor.getValue().toString(), is(equalTo(expectedItem.toString())));
    }

    @Test
    void testCreatedItem_missingId() {
        JsonNode properties = objectMapper.createObjectNode()
                .put("location_id", 1)
                .put("name", "new item");
        validateBadRequest(properties);
    }

    @Test
    void testCreatedItem_missingName() {
        JsonNode properties = objectMapper.createObjectNode()
                .put("id", 1)
                .put("location_id", 1);
        validateBadRequest(properties);
    }

    @Test
    void testCreatedItem_missingLocationId() {
        JsonNode properties = objectMapper.createObjectNode()
                .put("id", 1)
                .put("name", "new item");
        validateBadRequest(properties);
    }

    private void validateBadRequest(JsonNode properties) {
        ResponseStatusException expectedException = assertThrows(ResponseStatusException.class,
                () -> appService.createItem(properties));
        assertThat(expectedException.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(expectedException.getMessage(),
                containsString("Missing or not valid required properties `id`, "
                        + "`name` and/or `location_id` in the request body."));
    }

    @Test
    void testCreatedItem_duplicatedId() {
        int itemId = 1;
        int locationId = 1;
        Location location = new Location(locationId, "state", "address", null);
        Item expectedItem = new Item(itemId, "new item", null, location);
        given(locationRepository.findById(locationId))
                .willReturn(Optional.of(location));

        given(itemRepository.findById(itemId))
                .willReturn(Optional.of(expectedItem));

        JsonNode properties = objectMapper.createObjectNode()
                .put("id", itemId)
                .put("name", "new item")
                .put("location_id", locationId);
        ResponseStatusException expectedException = assertThrows(ResponseStatusException.class,
                () -> appService.createItem(properties));
        assertThat(expectedException.getStatusCode(), is(equalTo(HttpStatus.CONFLICT)));
        assertThat(expectedException.getMessage(),
                containsString("Another item with the same `id`: "
                        + itemId + " already exists. Please provide a unique item `id`."));

    }

    @Test
    void testCreatedItem_nonExistingLocation() {
        int itemId = 1;
        int locationId = 1;
        given(locationRepository.findById(locationId))
                .willReturn(Optional.empty());

        JsonNode properties = objectMapper.createObjectNode()
                .put("id", itemId)
                .put("name", "new item")
                .put("location_id", locationId);
        ResponseStatusException expectedException = assertThrows(ResponseStatusException.class,
                () -> appService.createItem(properties));
        assertThat(expectedException.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(expectedException.getMessage(),
                containsString("The provided `location_id`: "
                        + locationId + " is not associated with a valid location."));
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