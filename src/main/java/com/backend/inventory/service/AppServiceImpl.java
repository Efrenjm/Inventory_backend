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

        if (itemId <= 0)
            missingProperties.add("`id`");

        if (locationId <= 0)
            missingProperties.add("`location.id`");

        if (itemName == null || itemName.isBlank())
            missingProperties.add("`name`");

        if (stateName == null || stateName.isBlank())
            missingProperties.add("`location.state`");

        if (!missingProperties.isEmpty()) {
            String message = getMessage(missingProperties);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    message);
        }
    }

    private static String getMessage(List<String> missingProperties) {
        int missing = missingProperties.size();
        System.out.println(missing);
        StringBuilder listOfProperties = new StringBuilder();

        for (int i = 0; i < missing; i++) {
            listOfProperties.append(missingProperties.get(i));
            if (i < missing - 1)
                listOfProperties.append(", ");
        }
        String message;
        if (missing == 1)
            message = "The property: " + listOfProperties.toString()
                    + " is invalid or missing.";
        else
            message = "The properties: " + listOfProperties.toString()
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
        }
        return formattedLocation;
    }

//    @Override
//    public Item createItem(JsonNode request) {
////        Item newItem = validateRequest(request);
////        return itemRepository.save(newItem);
//        return new Item();
//    }

//    private Item validateRequest(JsonNode request) {
//        int id;
//        String name;
//        int location_id;
//        String description = null;
//        try {
//            id = request.get("id").asInt();
//            if (id <= 0) throw new Exception();
//
//            name = request.get("name").toString()
//                    .replaceAll("^\"|\"$", "").strip();
//
//            location_id = request.get("location_id").asInt();
//        } catch (Exception exc) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                    "Missing or not valid required properties `id`, `name` and/or `location_id` in the request body.", exc);
//        }
//        if (request.hasNonNull("description")) {
//            description = request.get("description").toString()
//                    .replaceAll("^\"|\"$", "").strip();
//        }
//
//        validateNoConflicts(id);
//        Location location = getLocation(location_id);
//        return new Item(id, name, description, location);
//    }



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