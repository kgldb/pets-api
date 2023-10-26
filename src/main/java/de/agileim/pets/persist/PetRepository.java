package de.agileim.pets.persist;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PetRepository  extends CrudRepository<PetEntity, Long> {
    List<PetEntity> findAll(Pageable pageable);
}