package de.agileim.pets;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.agileim.pets.generated.model.NewPet;
import de.agileim.pets.generated.model.Pet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class ReadListTest {

    private final MockMvc mvc;

    @Autowired
    ReadListTest(MockMvc mvc) {
        this.mvc = mvc;
    }


    @Test
    void testListPets() throws Exception {
        //save
        for (int i = 0; i < 5; i++) {
            var newPet = new NewPet("Bello" + 1).tag(String.valueOf(i));
            mvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsBytes(newPet)));
        }

        //read all
        String content = mvc.perform(get("/pets")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Pet[] savedPets = new ObjectMapper().readValue(content, Pet[].class);
        assertEquals(5, savedPets.length);

        //first 2
        MockHttpServletResponse resp = mvc.perform(get("/pets?limit=2")).andExpect(status().isOk()).andReturn().getResponse();
        String nextLink = resp.getHeader("x-next");
        Pet[] page1 = new ObjectMapper().readValue(resp.getContentAsString(), Pet[].class);
        assertEquals(2, page1.length);
        assertEquals("0", page1[0].getTag());
        assertEquals("1", page1[1].getTag());
        assertNotNull(nextLink);

        //next
        resp = mvc.perform(get(nextLink)).andExpect(status().isOk()).andReturn().getResponse();
        nextLink = resp.getHeader("x-next");
        Pet[] page2 = new ObjectMapper().readValue(resp.getContentAsString(), Pet[].class);
        assertEquals(2, page2.length);
        assertEquals("2", page2[0].getTag());
        assertEquals("3", page2[1].getTag());
        assertNotNull(nextLink);

        //next
        resp = mvc.perform(get(nextLink)).andExpect(status().isOk()).andReturn().getResponse();
        nextLink = resp.getHeader("x-next");
        Pet[] page3 = new ObjectMapper().readValue(resp.getContentAsString(), Pet[].class);
        assertEquals(1, page3.length);
        assertEquals("4", page3[0].getTag());
        assertNull(nextLink);
    }

    @Test
    void testLimitOver100() throws Exception {
        mvc.perform(get("/pets?limit=101")).andExpect(status().isBadRequest());
    }

    @Test
    void testLimitAndOffset0() throws Exception {
        mvc.perform(get("/pets?limit=0&offset=0")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void testNegativeLimit() throws Exception {
        mvc.perform(get("/pets?limit=-1")).andExpect(status().isBadRequest());
    }

    @Test
    void testNegativeOffset() throws Exception {
        mvc.perform(get("/pets?offset=-1")).andExpect(status().isBadRequest());
    }

    @Test
    void testBigOffset() throws Exception {
        mvc.perform(get("/pets?limit=100&offset=1000")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

}
