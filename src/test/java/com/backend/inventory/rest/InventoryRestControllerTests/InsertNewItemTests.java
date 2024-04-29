package com.backend.inventory.rest.InventoryRestControllerTests;

import com.backend.inventory.entity.Item;
import com.backend.inventory.entity.Location;
import com.backend.inventory.rest.InventoryExceptionHandler;
import com.backend.inventory.rest.InventoryRestController;
import com.backend.inventory.service.AppService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class InsertNewItemTests {
    private final ObjectMapper objectMapper = new ObjectMapper();

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
    public void testCreatedResponse() throws Exception {
        String state = "available";
        Location location = new Location(1, "Loc A");
        Item item = new Item(1, "Item A", state, location);

        given(appService.createItem(any())).willReturn(item);
        ResponseEntity<Item> response = inventoryController.insertNewItems(item, UriComponentsBuilder.newInstance());
        assertThat(response.getBody(), is(equalTo(item)));
        assertThat(response.getStatusCode(), is(equalTo(HttpStatus.CREATED)));

        String itemJson = objectMapper.writeValueAsString(item);
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testBadRequestResponse() throws Exception {
        String errorMessage = "The properties: `location.id`, `location.state` are invalid or missing.";
        ResponseStatusException serviceException = new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        given(appService.createItem(any()))
                .willThrow(serviceException);

        Item item = new Item(1, "Item A");
        String itemJson = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("BAD_REQUEST"))
                .andExpect(jsonPath("message").value(errorMessage))
                .andExpect(jsonPath("path").value("/items"));
    }

    @Test
    public void testConflictResponse() throws Exception {
        int itemId = 1;
        String errorMessage = "Another item with the same `id`: " + itemId + " already exists. Please provide a unique item `id`.";
        ResponseStatusException serviceException = new ResponseStatusException(HttpStatus.CONFLICT,
                errorMessage);
        given(appService.createItem(any()))
                .willThrow(serviceException);

        Item item = new Item(itemId, "Item A");
        String itemJson = objectMapper.writeValueAsString(item);
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value("CONFLICT"))
                .andExpect(jsonPath("message").value(errorMessage))
                .andExpect(jsonPath("path").value("/items"));
    }
}
