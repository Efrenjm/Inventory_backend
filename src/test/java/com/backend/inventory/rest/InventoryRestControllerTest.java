package com.backend.inventory.rest;

import com.backend.inventory.entity.Item;
import com.backend.inventory.service.AppService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class InventoryRestControllerTest {
    @Mock
    private AppService appService;


    @InjectMocks
    private InventoryRestController inventoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetListOfItems_Ok(){
        String state = "available";
        Item item1 = new Item(1, "Item A", state);
        Item item2 = new Item(2, "Item B", state);
        Flux<Item> items = Flux.just(item1, item2);

        given(appService.findItemsByState(state)).willReturn(items);

        ResponseEntity<Flux<Item>> response = inventoryController.getListOfItems(state);

        assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(response.getBody(), is(equalTo(items)));
    }
    @Test
    public void testGetListOfItems_NotFound() {
    }
}
