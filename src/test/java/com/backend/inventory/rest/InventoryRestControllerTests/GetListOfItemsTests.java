package com.backend.inventory.rest.InventoryRestControllerTests;

import com.backend.inventory.entity.Item;
import com.backend.inventory.rest.InventoryExceptionHandler;
import com.backend.inventory.rest.InventoryRestController;
import com.backend.inventory.service.AppService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class GetListOfItemsTests {
    private MockMvc mockMvc;

    @Mock
    private AppService appService;

    @InjectMocks
    private InventoryRestController inventoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(inventoryController)
                .setControllerAdvice(new InventoryExceptionHandler())
                .build();
    }

    @Test
    public void testOkResponse() throws Exception {
        String state = "available";
        Item item1 = new Item(1, "Item A", state);
        Item item2 = new Item(2, "Item B", state);
        Flux<Item> items = Flux.just(item1, item2);

        given(appService.findItemsByState(state)).willReturn(items);

        ResponseEntity<Flux<Item>> response = inventoryController.getListOfItems(state);
        assertThat(response.getBody(), is(equalTo(items)));
        assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));

        mockMvc.perform(get("/items?state=" + state))
                .andExpect(status().isOk());
    }

    @Test
    public void testNotFoundResponse() throws Exception {
        String state = "invalid";
        String errorMessage = "No items found for state: '" + state + "'";
        ResponseStatusException serviceException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                errorMessage);
        given(appService.findItemsByState(state))
                .willThrow(serviceException);

        mockMvc.perform(get("/items?state=" + state))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("message").value(errorMessage))
                .andExpect(jsonPath("path").value("/items"));
    }
}
