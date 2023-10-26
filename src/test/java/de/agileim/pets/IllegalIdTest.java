package de.agileim.pets;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.agileim.pets.generated.model.NewPet;
import de.agileim.pets.generated.model.Pet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IllegalIdTest {
    private final MockMvc mvc;

    @Autowired
    IllegalIdTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void testGetPetWrongId() throws Exception {
        var newPet = new NewPet("Bello").tag("dog");
        mvc.perform(get("/pets/A2").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsBytes(newPet)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatePetWrongId() throws Exception {
        var pet = new Pet("Bello", 1L).tag("dog");
        mvc.perform(put("/pets/1A").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsBytes(pet)))
                .andExpect(status().isBadRequest());
    }
}
