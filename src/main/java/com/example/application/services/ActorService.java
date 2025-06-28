package com.example.application.services;

import com.example.application.data.Actor;
import com.example.application.repositories.ActorRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActorService {

    private final ActorRepository repository;

    public ActorService(ActorRepository repository) {
        this.repository = repository;
    }

    public Optional<Actor> get(Long id) {
        return repository.findById(id);
    }

    public Actor save(Actor entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Actor> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Actor> list(Pageable pageable, Specification<Actor> filter) {
        return repository.findAll(filter, pageable);
    }
    public List<Actor> listAll() {
        return repository.findAll();
    }
    public int count() {
        return (int) repository.count();
    }

}
