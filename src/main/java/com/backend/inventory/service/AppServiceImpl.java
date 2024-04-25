package com.backend.inventory.service;

import com.backend.inventory.dao.ItemRepository;
import com.backend.inventory.dao.LocationRepository;
import com.backend.inventory.entity.Item;
import com.backend.inventory.entity.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public Item createItem(String name, String description, int location_id) {
        Optional<Location> result = locationRepository.findById(location_id);

        Location tempLocation = null;
        if (result.isPresent()) {
            tempLocation = result.get();
        } // else { throw bad request }

        Item newITem = new Item(name, description);
        newITem.setLocation(tempLocation);

        return itemRepository.save(newITem);
    }

    @Override
    public void deleteItem(int itemId) {
        itemRepository.deleteById(itemId);
    }
}