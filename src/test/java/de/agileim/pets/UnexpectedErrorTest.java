package de.agileim.pets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.agileim.pets.generated.model.Error;
import de.agileim.pets.generated.model.NewPet;
import de.agileim.pets.generated.model.Pet;
import de.agileim.pets.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UnexpectedErrorTest {

    private final String EXP_MESSAGE = "something does not work";

    private final MockMvc mvc;

    @MockBean
    private PetService petService;

    @Autowired
    UnexpectedErrorTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @BeforeEach
    void setup() {
        when(petService.createPet(any())).thenThrow(new RuntimeException(EXP_MESSAGE));
        when(petService.listPets(anyInt(), anyInt())).thenThrow(new RuntimeException(EXP_MESSAGE));
        when(petService.showPetById(anyLong())).thenThrow(new RuntimeException(EXP_MESSAGE));
        when(petService.updatePetById(anyLong(), any())).thenThrow(new RuntimeException(EXP_MESSAGE));
    }

    private void checkExpectedError(String resp) throws JsonProcessingException {
        Error error = new ObjectMapper().readValue(resp, Error.class);
        assertEquals(EXP_MESSAGE, error.getMessage());
        assertEquals(500, error.getCode());
    }

    @Test
    void testCreatePet() throws Exception {
        var newPet = new NewPet("Bello").tag("dog");
        String resp = mvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsBytes(newPet)))
                .andExpect(status().isInternalServerError()).andReturn().getResponse().getContentAsString();
        checkExpectedError(resp);
    }

    @Test
    void testGetPet() throws Exception {
        String resp = mvc.perform(get("/pets/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()).andReturn().getResponse().getContentAsString();
        checkExpectedError(resp);
    }


    @Test
    void testUpdatePet() throws Exception {
        var pet = new Pet("Bello", 1L).tag("dog");
        String resp = mvc.perform(put("/pets/1").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsBytes(pet)))
                .andExpect(status().isInternalServerError()).andReturn().getResponse().getContentAsString();
        checkExpectedError(resp);
    }

    @Test
    void testListPets() throws Exception {
        String resp = mvc.perform(get("/pets").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()).andReturn().getResponse().getContentAsString();
        checkExpectedError(resp);
    }

}
