package com.backend.inventory.rest;

import com.backend.inventory.entity.Item;
import com.backend.inventory.service.AppService;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/items")
public class InventoryRestController {
    private final AppService appService;

    @Autowired
    public InventoryRestController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping
    public Flux<Item> getItemsByState(@RequestParam(required = false) String state) {
        if (state != null) {
            return appService.findItemsByState(state);
        }
        return appService.findAllItems();
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable int itemId) {
        return appService.findItemById(itemId);
    }

    @PostMapping
    public Item insertNewItem(@RequestBody JsonNode requestBody) {//throws BadRequestException {
        String name = null;
        Integer location_id = null;
        String description = null;
        try {
            name = requestBody.get("name").toString()
                    .replaceAll("^\"|\"$", "").strip();
            location_id = requestBody.get("location_id").asInt();
        } catch (Exception e) { // Throw bad request
//            throw new BadRequestException("el entity no cuenta con los datos requeridos en el request body");
        }
        if (requestBody.hasNonNull("description")) {
            description = requestBody.get("description").toString()
                    .replaceAll("^\"|\"$", "").strip();
        }
        return appService.createItem(name, description, location_id);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId) {
        appService.deleteItem(itemId);
    }
}