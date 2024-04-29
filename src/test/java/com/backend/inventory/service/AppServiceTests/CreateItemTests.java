package com.backend.inventory.service.AppServiceTests;

import com.backend.inventory.dao.ItemRepository;
import com.backend.inventory.dao.LocationRepository;
import com.backend.inventory.entity.Item;
import com.backend.inventory.entity.Location;
import com.backend.inventory.service.AppServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

public class CreateItemTests {
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
    void testExpectedRequest() {
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
    void testNegativeItemIdAndLocationId() {
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
    void testMissingIdAndLocationState() {
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
    void testMissingName() {
        Location location = new Location(1, "state");
        Item properties = new Item(1, null, location);

        ResponseStatusException expectedException = assertThrows(ResponseStatusException.class,
                () -> appService.createItem(properties));

        assertThat(expectedException.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(expectedException.getMessage(),
                containsString("The property: `name` is invalid or missing."));
    }

    @Test
    void testMissingLocation() {
        Item properties = new Item(1, "name", "description");
        ResponseStatusException expectedException = assertThrows(ResponseStatusException.class,
                () -> appService.createItem(properties));

        assertThat(expectedException.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(expectedException.getMessage(),
                containsString("The properties: `location.id`, `location.state` are invalid or missing."));
    }


    @Test
    void testDuplicatedItemId() {
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
    void testDuplicatedLocationId() {
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
}
