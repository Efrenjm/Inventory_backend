package com.backend.inventory.service;

import com.backend.inventory.dao.ItemRepository;
import com.backend.inventory.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository theItemRepository) {
        itemRepository = theItemRepository;
    }

    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public Item findItemById(int theId) {
        Optional<Item> result = itemRepository.findById(theId);

        Item theItem = null;

        if (result.isPresent()) {
            theItem = result.get();
        }
        System.out.println("El item es: " + theItem);
//        else {
//            throw new RuntimeException("No se ha encontrado el item");
//        }

        return theItem;
    }

    @Override
    public List<Item> findItemsByState(String state) {
        return List.of();
    }

    @Override
    public Item save(Item theItem) {
        return itemRepository.save(theItem);
    }

    @Override
    public void deleteItemById(int theId) {
        itemRepository.deleteById(theId);
    }
}
