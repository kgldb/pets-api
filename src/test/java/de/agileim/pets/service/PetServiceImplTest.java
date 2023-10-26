package de.agileim.pets.service;

import de.agileim.pets.generated.model.NewPet;
import de.agileim.pets.generated.model.Pet;
import de.agileim.pets.persist.PetEntity;
import de.agileim.pets.persist.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PetServiceImplTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    PetServiceImpl petService;


    @Test
    void testCreatePet() {
        var newPet = new NewPet("TestName").tag("tag");

        var expectedEntity = new PetEntity(null, newPet.getName(), newPet.getTag());
        var returnEntity = new PetEntity(1L, newPet.getName(), newPet.getTag());

        when(petRepository.save(expectedEntity)).thenReturn(returnEntity);

        var createdPet = petService.createPet(newPet);
        assertEquals(1L, createdPet.getId());
        assertEquals(newPet.getName(), createdPet.getName());
        assertEquals(newPet.getTag(), createdPet.getTag());
    }


    @Test
    void testShowPetById() {
        var returnEntity = new PetEntity(1L, "some name", "some tag");

        when(petRepository.findById(1L)).thenReturn(Optional.of(returnEntity));

        var foundPet = petService.showPetById(1L);
        assertTrue(foundPet.isPresent());
        assertEquals(1L, foundPet.get().getId());
        assertEquals(returnEntity.getName(), foundPet.get().getName());
        assertEquals(returnEntity.getTag(), foundPet.get().getTag());
    }

    @Test
    void testShowPetByIdNotFound() {
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        var foundPet = petService.showPetById(1L);
        assertTrue(foundPet.isEmpty());
    }

    @Test
    void testListPets() {
        var foundEntities = List.of(new PetEntity(1L, "some name", "some tag"),
                new PetEntity(2L, "some name", "some tag"),
                new PetEntity(3L, "some name", "some tag"));

        when(petRepository.findAll(any(Pageable.class))).thenReturn(foundEntities);

        var foundPets = petService.listPets(5, 3);
        assertEquals(3, foundPets.pets().size());
        assertFalse(foundPets.hasMore());
    }

    @Test
    void testListPetsHasMore() {
        var foundEntities = List.of(new PetEntity(1L, "some name", "some tag"),
                new PetEntity(2L, "some name", "some tag"),
                new PetEntity(3L, "some name", "some tag"));

        when(petRepository.findAll(any(Pageable.class))).thenReturn(foundEntities);

        var foundPets = petService.listPets(2, 3);
        assertEquals(2, foundPets.pets().size());
        assertTrue(foundPets.hasMore());
    }


    @Test
    void testUpdatePet() {
        var returnEntity = new PetEntity(1L, "some name", "some tag");

        when(petRepository.findById(1L)).thenReturn(Optional.of(returnEntity));

        var petData = new Pet("changed name", null).tag("changed tag");

        var updatedPet = petService.updatePetById(1L, petData);
        assertTrue(updatedPet.isPresent());
        assertEquals(1L, updatedPet.get().getId());
        assertEquals(returnEntity.getName(), updatedPet.get().getName());
        assertEquals(returnEntity.getTag(), updatedPet.get().getTag());
    }

    @Test
    void testUpdatePetByIdNotFound() {
        when(petRepository.findById(1L)).thenReturn(Optional.empty());
        var petData = new Pet("changed name", null).tag("changed tag");

        var updatedPet = petService.updatePetById(1L, petData);
        assertTrue(updatedPet.isEmpty());
    }
}
