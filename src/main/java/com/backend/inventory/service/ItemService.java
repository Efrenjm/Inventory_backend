package com.backend.inventory.service;

import com.backend.inventory.entity.Item;

import java.util.List;

public interface ItemService {
    List<Item> findAll();

    Item findItemById(int id);

    List<Item> findItemsByState(String state);

    Item save(Item theItem);

    void deleteItemById(int theId);
}
