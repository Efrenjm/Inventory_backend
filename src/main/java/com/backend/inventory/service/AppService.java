package com.backend.inventory.service;

import com.backend.inventory.entity.Item;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Flux;

public interface AppService {
    Flux<Item> findAllItems();

    Item findItemById(int itemId);

    Flux<Item> findItemsByState(String stateName);

    Item createItem(JsonNode request);

    void deleteItem(int itemId);
}
