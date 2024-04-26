package com.backend.inventory.service;

import com.backend.inventory.dao.ItemRepository;
import com.backend.inventory.dao.LocationRepository;
import com.backend.inventory.entity.Item;
import com.backend.inventory.entity.Location;
import com.fasterxml.jackson.databind.JsonNode;
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
                    return location != null && stateName.equals(location.getState());
                })
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "No items found for state: '" + stateName + "'")));
    }

    @Override
    public Item findItemById(int itemId) {
        Optional<Item> result = itemRepository.findById(itemId);

        Item foundItem;

        if (result.isPresent()) {
            foundItem = result.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The provided `id` " + itemId + " is not associated with a valid item.");
        }
        return foundItem;
    }

    @Override
    public Item createItem(JsonNode request) {
        Item newItem = validateRequest(request);
        return itemRepository.save(newItem);
    }

    private Item validateRequest(JsonNode request) {
        int id;
        String name;
        int location_id;
        String description = null;
        try {
            id = request.get("id").asInt();
            if (id <= 0) throw new Exception();

            name = request.get("name").toString()
                    .replaceAll("^\"|\"$", "").strip();

            location_id = request.get("location_id").asInt();
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Missing or not valid required properties `id`, `name` and/or `location_id` in the request body.", exc);
        }
        if (request.hasNonNull("description")) {
            description = request.get("description").toString()
                    .replaceAll("^\"|\"$", "").strip();
        }

        validateNoConflicts(id);
        Location location = getLocation(location_id);
        return new Item(id, name, description, location);
    }

    private void validateNoConflicts(int itemId) {
        Optional<Item> foundItem = itemRepository.findById(itemId);
        if (foundItem.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Another item with the same `id`: " + itemId + " already exists. Please provide a unique item `id`.");
        }
    }

    private Location getLocation(int location_id) {
        Optional<Location> foundLocation = locationRepository.findById(location_id);
        Location location;
        if (foundLocation.isPresent()) {
            location = foundLocation.get();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The provided `location_id`: " + location_id + " is not associated with a valid location.");
        }
        return location;
    }

    @Override
    public void deleteItem(int itemId) {
        Optional<Item> tempItem = itemRepository.findById(itemId);
        if (tempItem.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The provided `id` " + itemId + " is not associated with a valid item.");
        }
        itemRepository.deleteById(itemId);
    }
}