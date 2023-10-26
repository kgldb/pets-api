package de.agileim.pets.service;

import de.agileim.pets.generated.model.Pet;

import java.util.List;

public record PetListingResult(List<Pet> pets, boolean hasMore) {

}
