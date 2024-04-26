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

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/items")
public class InventoryRestController {
    private final AppService appService;

    @Autowired
    public InventoryRestController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping
    public ResponseEntity<Flux<Item>> getItemsByState(@RequestParam(required = false) String state) {
        Flux<Item> items;
        if (state != null) {
            items = appService.findItemsByState(state);
        } else {
            items = appService.findAllItems();
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItemById(@PathVariable int itemId) {
        return ResponseEntity.ok(appService.findItemById(itemId));
    }

    @PostMapping
    public ResponseEntity<Item> insertNewItem(@RequestBody JsonNode requestBody, UriComponentsBuilder uriBuilder) {
        Item newItem = appService.createItem(requestBody);
        URI location = uriBuilder.path("/items/{id}").buildAndExpand(newItem.getId()).toUri();
        return ResponseEntity.created(location).body(newItem);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity deleteItem(@PathVariable int itemId) {
        appService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}