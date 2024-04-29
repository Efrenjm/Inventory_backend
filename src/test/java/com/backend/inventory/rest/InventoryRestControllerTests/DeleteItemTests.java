package com.backend.inventory.rest.InventoryRestControllerTests;

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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class DeleteItemTests {
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
    public void testNoContentResponse() throws Exception {
        int itemId = 1;

        mockMvc.perform(delete("/items/" + itemId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    public void testNotFoundResponse() throws Exception {
        int itemId = 1;
        String errorMessage = "The provided `id` " + itemId + " is not associated with a valid item.";
        ResponseStatusException serviceException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                errorMessage);
        doThrow(serviceException).when(appService).deleteItem(itemId);

        mockMvc.perform(delete("/items/" + itemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("message").value(errorMessage))
                .andExpect(jsonPath("path").value("/items/" + itemId));
    }
}
