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

import java.util.ArrayList;
import java.util.List;
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
    public Item createItem(Item newItem) {
        validateRequiredProperties(newItem);
        validateNoConflicts(newItem.getId());
        newItem.setLocation(getDBLocation(newItem.getLocation()));

        return itemRepository.save(newItem);
    }

    private void validateRequiredProperties(Item newItem) {
        if (newItem.getLocation() == null) {
            newItem.setLocation(new Location());
        }

        int itemId = newItem.getId();
        int locationId = newItem.getLocation().getId();
        String itemName = newItem.getName();
        String stateName = newItem.getLocation().getState();

        List<String> missingProperties = new ArrayList<>();

        if (itemId <= 0) {
            missingProperties.add("`id`");
        }

        if (locationId <= 0) {
            missingProperties.add("`location.id`");
        } else {
            Optional<Location> foundLocation = locationRepository.findById(locationId);
            if (foundLocation.isEmpty() && (stateName == null || stateName.isBlank()))
                missingProperties.add("`location.state`");
        }

        if (itemName == null || itemName.isBlank()) {
            missingProperties.add("`name`");
        }


        if (!missingProperties.isEmpty()) {
            String message = getMessage(missingProperties);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    message);
        }
    }

    private static String getMessage(List<String> missingProperties) {
        int missing = missingProperties.size();

        StringBuilder listOfProperties = new StringBuilder();

        for (int i = 0; i < missing; i++) {
            listOfProperties.append(missingProperties.get(i));
            if (i < missing - 1)
                listOfProperties.append(", ");
        }
        String message;
        if (missing == 1)
            message = "The property: " + listOfProperties
                    + " is invalid or missing.";
        else
            message = "The properties: " + listOfProperties
                    + " are invalid or missing.";
        return message;
    }

    private void validateNoConflicts(int itemId) {
        Optional<Item> foundItem = itemRepository.findById(itemId);
        if (foundItem.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Another item with the same `id`: " + itemId + " already exists. Please provide a unique item `id`.");
        }
    }

    private Location getDBLocation(Location location) {
        int location_id = location.getId();
        Optional<Location> foundLocation = locationRepository.findById(location_id);
        Location formattedLocation;
        if (foundLocation.isPresent()) {
            formattedLocation = foundLocation.get();
        } else {
            locationRepository.findAll();
            formattedLocation = locationRepository.save(location);
            System.out.println("Location not found in DB. Saving new location with the following properties: " + formattedLocation);
        }
        return formattedLocation;
    }

    @Override
    public Item updateItem(Item changedItem) {
        Optional<Item> tempItem = itemRepository.findById(changedItem.getId());
        Item updatedItem;

        if (tempItem.isPresent()) {
            updatedItem = tempItem.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The provided `id` " + changedItem.getId() + " is not associated with a valid item.");
        }

        updatedItem.setName(changedItem.getName());
        updatedItem.setDescription(changedItem.getDescription());

        validateRequiredProperties(updatedItem);

        return itemRepository.save(updatedItem);
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