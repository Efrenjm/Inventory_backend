package com.backend.inventory.rest;

import com.backend.inventory.entity.Item;
import com.backend.inventory.service.AppService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;

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
            return appService.findItemsByState(state)
                    .switchIfEmpty(Mono.error(
                            new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "No items found for state: '" + state + "'")));
        }
        return appService.findAllItems();
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable int itemId) {
        Item requestedItem = appService.findItemById(itemId);
        if (requestedItem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No items found for id: " + itemId);
        }
        return requestedItem;
    }

    @PostMapping
    public ResponseEntity<Item> insertNewItem(@RequestBody JsonNode requestBody, UriComponentsBuilder uriBuilder) {
        int id;
        String name;
        int location_id;
        String description = null;
        try {
            id = requestBody.get("id").asInt();
            name = requestBody.get("name").toString()
                    .replaceAll("^\"|\"$", "").strip();
            location_id = requestBody.get("location_id").asInt();
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Missing or not valid required properties 'id', 'name' and/or 'location_id' in the request body.", exc);
        }
        if (requestBody.hasNonNull("description")) {
            description = requestBody.get("description").toString()
                    .replaceAll("^\"|\"$", "").strip();
        }

        Item newItem = appService.createItem(id, name, description, location_id);
        URI location = uriBuilder.path("/items/{id}").buildAndExpand(newItem.getId()).toUri();
        return ResponseEntity.created(location).body(newItem);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity deleteItem(@PathVariable int itemId) {
        Item tempItem = appService.findItemById(itemId);
        if (tempItem == null) {
            return ResponseEntity.notFound().build();
        }

        appService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}