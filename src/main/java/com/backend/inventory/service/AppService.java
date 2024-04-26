package com.backend.inventory.service;

import com.backend.inventory.entity.Item;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Flux;

public interface AppService {
    Flux<Item> findAllItems();

    Flux<Item> findItemsByState(String stateName);

    Item findItemById(int itemId);

    Item createItem(Item request);

//    Item createItem(JsonNode request);

    void deleteItem(int itemId);
}
