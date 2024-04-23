package com.backend.inventory.dao;

import com.backend.inventory.entity.Item;
import com.backend.inventory.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
