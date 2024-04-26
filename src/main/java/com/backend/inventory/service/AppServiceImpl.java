package com.backend.inventory.service;

import com.backend.inventory.dao.ItemRepository;
import com.backend.inventory.dao.LocationRepository;
import com.backend.inventory.entity.Item;
import com.backend.inventory.entity.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class AppServiceImpl implements AppService {

    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public AppServiceImpl(ItemRepository itemRepository, LocationRepository locationRepository) {
        this.itemRepository = itemRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public Flux<Item> findAllItems() {
        return Flux.fromIterable(itemRepository.findAll());
    }

    @Override
    public Flux<Item> findItemsByState(String stateName) {
        return Flux.fromIterable(itemRepository.findAll())
                .filter(item -> {
                    Location location = item.getLocation();
                    return location!=null && stateName.equals(location.getState());
                })
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "No items found for state: '" + stateName + "'")));
    }

    @Override
    public Item findItemById(int itemId) {
        Optional<Item> result = itemRepository.findById(itemId);

        Item foundItem = null;

        if (result.isPresent()) {
            foundItem = result.get();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The provided item `id` is not associated with a valid item.");
        }
        return foundItem;
    }

    @Override
    public Item createItem(int id, String name, String description, int location_id) {
        Optional<Location> foundLocation = locationRepository.findById(location_id);

        Location tempLocation;
        if (foundLocation.isPresent()) {
            tempLocation = foundLocation.get();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The provided `location_id` is not associated with a valid location.");
        }

        Optional<Item> foundItem = itemRepository.findById(id);
        if (foundItem.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Another item with the same `id` already exists. Please provide a unique item `id`.");
        }
        Item newItem = new Item(id, name, description, tempLocation);

        return itemRepository.save(newItem);
    }

    @Override
    public void deleteItem(int itemId) {
        Optional<Item> tempItem = itemRepository.findById(itemId);
        if (!tempItem.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The provided item `id` is not associated with a valid item.");
        }
        itemRepository.deleteById(itemId);
    }
}