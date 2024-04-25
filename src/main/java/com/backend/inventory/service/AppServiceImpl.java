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
    public Item findItemById(int itemId) {
        Optional<Item> result = itemRepository.findById(itemId);

        Item foundItem = null;

        if (result.isPresent()) {
            foundItem = result.get();
        }
        return foundItem;
    }

    @Override
    public Flux<Item> findItemsByState(String stateName) {

        return Flux.fromIterable(itemRepository.findAll())
                .filter(item -> item.getLocation().getState().equals(stateName));
    }

    @Override
    public Item createItem(int id, String name, String description, int location_id) {
        Optional<Location> result = locationRepository.findById(location_id);

        Location tempLocation = null;
        if (result.isPresent()) {
            tempLocation = result.get();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "'location_id' is not associated with a valid location.");
        }
        if (id != 0) {
            Optional<Item> actualRecord = itemRepository.findById(id);
            if (actualRecord.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Another item with the same id already exists. Please provide a unique id.");
            }
        }
        Item newItem = new Item(name, description);
        newItem.setId(id);
//        newItem.setId(id);
        newItem.setLocation(tempLocation);

        return itemRepository.save(newItem);
    }

    @Override
    public void deleteItem(int itemId) {
        itemRepository.deleteById(itemId);
    }
}