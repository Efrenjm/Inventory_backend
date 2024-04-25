package com.backend.inventory.service;

import com.backend.inventory.entity.Item;
import reactor.core.publisher.Flux;

public interface AppService {
    Flux<Item> findAllItems();

    Item findItemById(int itemId);

    Flux<Item> findItemsByState(String stateName);

    Item createItem(int id, String name, String description, int location_id);

    void deleteItem(int itemId);
}
