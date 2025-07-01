package com.example.application.services;

import com.example.application.data.entity.Store;
import com.example.application.repositories.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StoreService {
    private StoreRepository storeRepository;

    @Autowired
    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    public Optional<Store> findById(Integer id) {
        return storeRepository.findById(id);
    }

    public Store saveStore(Store store) {
        if (store.getLastUpdate() == null) {
            store.setLastUpdate(LocalDateTime.now());
        } else {
            store.setLastUpdate(LocalDateTime.now());
        }
        return storeRepository.save(store);
    }

    public void deleteStore(Integer id) {
        storeRepository.deleteById(id);
    }
}
