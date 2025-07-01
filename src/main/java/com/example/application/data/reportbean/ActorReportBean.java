
        package com.example.application.data.reportbean;

import com.example.application.data.entity.Actor;

import java.time.LocalDateTime;

public class ActorReportBean {
    private Integer actorId;
    private String firstName;
    private String lastName;
    private LocalDateTime lastUpdate;

    public ActorReportBean(Actor actor) {
        this.actorId = actor.getActorId();
        this.firstName = actor.getFirstName();
        this.lastName = actor.getLastName();
        // Convert LocalDate to LocalDateTime (use midnight as default time)
        this.lastUpdate = actor.getLastUpdate() != null ? actor.getLastUpdate().atStartOfDay() : null;
    }

    public Integer getActorId() {
        return actorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
}
