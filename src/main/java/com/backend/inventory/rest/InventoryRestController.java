package com.backend.inventory.rest;


import com.backend.inventory.entity.Item;
import com.backend.inventory.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class InventoryRestController {
    private ItemService itemService;

    @Autowired
    public InventoryRestController(ItemService theItemService) {
        itemService = theItemService;
    }

    @GetMapping("/items")
    public List<Item> getItems(@RequestBody Optional<String> state) {
        if (state.isPresent()) {
            return itemService.findItemsByState(state.get());
        }
        return itemService.findAll();
    }

    @GetMapping("/items/{itemId}")
    public Item getItemById(@PathVariable int itemId) {
        // Look for specific id

        // 200 OK
        // 404 Not Found
        return itemService.findItemById(itemId);
    }

    @PostMapping("/items")
    public Item insertNewItem(@RequestBody Item theItem) {
        Item dbItem = itemService.save(theItem);
        // 201 Created
        // 400 Bad request (no cuenta con los datos requeridos)
        // 409 Conflict (el item id ya existe en la BD)
        return dbItem;
    }

    @DeleteMapping("/items/{itemId}")
    public void deleteItem(@PathVariable int itemId) {
        // 204 No content - se eliminó correctamente
        // 404 Not Found - el item id no existe
    }
}
