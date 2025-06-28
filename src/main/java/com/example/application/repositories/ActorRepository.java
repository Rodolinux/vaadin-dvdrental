package com.example.application.repositories;

import com.example.application.data.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActorRepository
        extends
            JpaRepository<Actor, Long>,
            JpaSpecificationExecutor<Actor> {

}
