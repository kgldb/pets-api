package de.agileim.pets.service;

import de.agileim.pets.generated.model.NewPet;
import de.agileim.pets.generated.model.Pet;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface PetService {

    Pet createPet(NewPet newPet);

    Optional<Pet> showPetById(long petId);

    PetListingResult  listPets(int limit, int offset);

    Optional<Pet> updatePetById(long petId, Pet pet);
}
