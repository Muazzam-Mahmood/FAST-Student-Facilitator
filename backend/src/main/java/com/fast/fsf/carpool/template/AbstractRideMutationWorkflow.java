package com.fast.fsf.carpool.template;

import com.fast.fsf.carpool.domain.Ride;
import com.fast.fsf.carpool.persistence.RideRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

/**
 * Abstract workflow for mutating ride entities.
 * Defines the skeleton for loading, mutating, saving, and notifying observers.
 */
public abstract class AbstractRideMutationWorkflow {

    protected final RideRepository rideRepository;
    protected final ApplicationEventPublisher eventPublisher;

    protected AbstractRideMutationWorkflow(RideRepository rideRepository, ApplicationEventPublisher eventPublisher) {
        this.rideRepository = rideRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Template method — {@code final} so the control flow cannot drift in subclasses.
     */
    protected final ResponseEntity<Ride> execute(Long id, Optional<String> optionalReason) {
        return rideRepository.findById(id)
                .map(ride -> {
                    Ride mutated = mutateRide(ride, optionalReason);
                    Ride saved = rideRepository.save(mutated);
                    publishDomainEvent(saved, optionalReason);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** Hook #1 — combine {@link com.fast.fsf.carpool.state.RideModerationContext} transitions etc. */
    protected abstract Ride mutateRide(Ride ride, Optional<String> optionalReason);

    /** Hook #2 — translate persistence outcome into Spring {@code ApplicationEvent}s for observers. */
    protected abstract void publishDomainEvent(Ride savedRide, Optional<String> optionalReason);
}
