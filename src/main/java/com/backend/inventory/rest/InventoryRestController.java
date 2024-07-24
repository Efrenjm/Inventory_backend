package com.backend.inventory.rest;

import com.backend.inventory.entity.Item;
import com.backend.inventory.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;

@RestController
@RequestMapping(value = "/items")
@CrossOrigin(origins = "http://localhost:3000")
public class InventoryRestController {
    private final AppService appService;

    @Autowired
    public InventoryRestController(AppService appService) {
        this.appService = appService;
    }

    @ResponseBody
    @GetMapping
    public ResponseEntity<Flux<Item>> getListOfItems(@RequestParam(required = false) String state) {
        Flux<Item> items;
        if (state != null) {
            items = appService.findItemsByState(state);
        } else {
            items = appService.findAllItems();
        }
        return ResponseEntity.ok(items);
    }

    @ResponseBody
    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Item> getItemById(@PathVariable int itemId) {
        return ResponseEntity.ok(appService.findItemById(itemId));
    }

    @ResponseBody
    @PostMapping
    public ResponseEntity<Item> insertNewItems(@RequestBody Item requestBody, UriComponentsBuilder uriBuilder) {
        Item newItem = appService.createItem(requestBody);
        URI location = uriBuilder.path("/items/{id}").buildAndExpand(newItem.getId()).toUri();
        return ResponseEntity.created(location).body(newItem);
    }

    @ResponseBody
    @PatchMapping
    public ResponseEntity<Item> editItem(@RequestBody Item requestBody) {
        return ResponseEntity.ok(appService.updateItem(requestBody));
    }

    @DeleteMapping(value = "/{itemId}")
    public ResponseEntity<Item> deleteItem(@PathVariable int itemId) {
        appService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}