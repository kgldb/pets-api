package de.agileim.pets.controller;

import de.agileim.pets.generated.api.PetsApi;
import de.agileim.pets.generated.model.NewPet;
import de.agileim.pets.generated.model.Pet;
import de.agileim.pets.service.PetListingResult;
import de.agileim.pets.service.PetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Controller
@Slf4j
public class PetsController implements PetsApi {

    private final HttpServletRequest request;

    private final PetService petService;

    public PetsController(HttpServletRequest request, PetService petService) {
        this.request = request;
        this.petService = petService;
    }

    @Override
    public ResponseEntity<Pet> createPets(@Valid NewPet newPet) {
        log.debug("create new pet");
        return ResponseEntity.ok(petService.createPet(newPet));
    }

    @Override
    public ResponseEntity<List<Pet>> listPets(@Max(100) @Valid Integer limit, @Valid Integer offset) {
        offset = Objects.requireNonNullElse(offset, 0);
        limit = Objects.requireNonNullElse(limit, 100);
        if (limit < 0) {
            throw new IllegalArgumentException("limit cannot be negative");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be negative");
        }
        log.debug("get pets limit = {}, offset = {}", limit, offset);
        PetListingResult result = petService.listPets(limit, offset);
        if (result.hasMore()) {
            offset = offset + limit;
            return ResponseEntity.ok().header("x-next",
                    request.getRequestURL() + "?offset=" + offset + "&limit=" + limit).body(result.pets());
        }
        return ResponseEntity.ok(result.pets());
    }

    @Override
    public ResponseEntity<Pet> showPetById(String petId) {
        log.debug("get pet id = {}", petId);
        long petIdLong = parseId(petId);
        Optional<Pet> foundPet = petService.showPetById(petIdLong);
        Pet pet = foundPet.orElseThrow(() -> new NoSuchElementException("Pet with id " + petId + " not found"));
        return ResponseEntity.ok(pet);
    }

    @Override
    public ResponseEntity<Pet> updatePetById(String petId, @Valid Pet pet) {
        log.debug("update pet id = {}", petId);
        long petIdLong = parseId(petId);
        Optional<Pet> updatedPetOptional = petService.updatePetById(petIdLong, pet);
        Pet updatedPet = updatedPetOptional.orElseThrow(() -> new NoSuchElementException("Pet with id " + petId + " not found"));
        return ResponseEntity.ok(updatedPet);
    }

    private long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("String " + id + " cannot be converted to long");
        }
    }
}
