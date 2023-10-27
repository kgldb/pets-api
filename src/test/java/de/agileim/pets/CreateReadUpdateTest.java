package de.agileim.pets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.agileim.pets.generated.model.NewPet;
import de.agileim.pets.generated.model.Pet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class CreateReadUpdateTest {

    static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    private final MockMvc mvc;

    @Autowired
    CreateReadUpdateTest(MockMvc mvc) {
        this.mvc = mvc;
    }


    @Test
    void testCreateAndShowPet() throws Exception {
        var newPet = new NewPet("Bello").tag("dog");

        //save
        String resp = mvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON).content(toJson(newPet)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var savedPet = new ObjectMapper().readValue(resp, Pet.class);
        assertEquals(newPet.getName(), savedPet.getName());
        assertEquals(newPet.getTag(), savedPet.getTag());

        //get saved
        mvc.perform(get("/pets/" + savedPet.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(newPet.getName())))
                .andExpect(jsonPath("$.tag", is(newPet.getTag())));
    }


    @Test
    void testCreateUpdateAndShowPet() throws Exception {
        var newPet = new NewPet("Bello").tag("dog");

        //save
        String resp = mvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON).content(toJson(newPet)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var savedPet = new ObjectMapper().readValue(resp, Pet.class);

        //update
        var changedPet = new Pet("MiauMiau", savedPet.getId()).tag("cat");
        mvc.perform(put("/pets/" + savedPet.getId()).contentType(MediaType.APPLICATION_JSON).content(toJson(changedPet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(changedPet.getName())))
                .andExpect(jsonPath("$.tag", is(changedPet.getTag())));

        //get updated
        mvc.perform(get("/pets/" + savedPet.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(changedPet.getName())))
                .andExpect(jsonPath("$.tag", is(changedPet.getTag())));
    }


    @Test
    void testCreatePetTooLongData() throws Exception {
        var builder = new StringBuilder("a");
        builder.append("b".repeat(256));
        var newPet = new NewPet(builder.toString()).tag("dog");

        mvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON).content(toJson(newPet)))
                .andExpect(status().isBadRequest());
    }


}
