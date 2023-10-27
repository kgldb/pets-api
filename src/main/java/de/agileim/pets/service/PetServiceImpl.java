package de.agileim.pets.service;

import de.agileim.pets.generated.model.NewPet;
import de.agileim.pets.generated.model.Pet;
import de.agileim.pets.persist.PetEntity;
import de.agileim.pets.persist.PetRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;

    public PetServiceImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    private Pet petEntity2Pet(PetEntity petEntity) {
        return new Pet(petEntity.getName(), petEntity.getId()).tag(petEntity.getTag());
    }

    @Override
    public Pet createPet(NewPet newPet) {
        log.debug("create new pet: {}", newPet);
        var petEntity = new PetEntity();
        petEntity.setName(newPet.getName());
        petEntity.setTag(newPet.getTag());
        return petEntity2Pet(petRepository.save(petEntity));
    }

    @Override
    public Optional<Pet> showPetById(long petId) {
        log.debug("show Pet by Id {}", petId);
        return petRepository.findById(petId).map(this::petEntity2Pet);
    }

    @Override
    public PetListingResult listPets(int limit, int offset) {
        boolean hasMore = false;
        log.debug("list pets: limit={}, offset={}", limit, offset);
        List<PetEntity> page = petRepository.findAll(new OffsetLimitPageRequest(limit + 1, offset));
        List<Pet> pets = page.stream().map(this::petEntity2Pet).collect(Collectors.toList());
        if (pets.size() > limit) {
            hasMore = true;
            pets.remove(pets.size() - 1);
        }
        log.debug("return pets: pets={}, hasMore={}", pets, hasMore);
        return new PetListingResult(pets, hasMore);
    }

    @Override
    @Transactional
    public Optional<Pet> updatePetById(long petId, Pet pet) {
        Optional<PetEntity> foundPetOptional = petRepository.findById(petId);
        if (foundPetOptional.isEmpty()) {
            return Optional.empty();
        }
        var foundPet = foundPetOptional.get();
        foundPet.setName(pet.getName());
        foundPet.setTag(pet.getTag());
        log.debug("updating pet: {}", foundPet);
        return Optional.of(new Pet(foundPet.getName(), foundPet.getId()).tag(foundPet.getTag()));
    }
}
