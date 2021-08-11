package com.bol.stonepitgame.bolgameapi.controller;

import java.nio.charset.Charset;
import java.sql.SQLDataException;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.WebApplicationContext;

import com.bol.stonepitgame.bolgameapi.domain.Game;
import com.bol.stonepitgame.bolgameapi.exceptions.BolException;
import com.bol.stonepitgame.bolgameapi.service.GameService;

import lombok.SneakyThrows;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class GameControllerTests {

    private final Resource jsonCreateGame = new ClassPathResource("create.json");
    private final Resource jsonGameSowPit = new ClassPathResource("play.json");

    private MockMvc mockMvc;

    @Autowired
    private GameService gameService;

    private Game bolGame;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @PostConstruct
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @SneakyThrows
    private String asJson(Resource resource) {
        return StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
    }
    @Before
    public void setupTest (){
        this.bolGame = new Game(6);
    }

    @Test
    public void testGameCreation() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.post("/gameapi"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
    }

    @Test
    public void testSowPitIndex2() throws Exception {

        this.bolGame = gameService.createNewGame(6);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/gameapi/"+this.bolGame.getId()+"/pits/2"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
    }

   // @Test
    public void testSowingTheGameOfInvalidId() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.put("/gameapi/2/pits/2"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("@.message").value("Game id 2 not found!"))
                .andReturn();
    }

   // @Test
    public void testSowingTheGameAtInvalidPitIndex() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/gameapi/1/pits/14"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("@.message").value("Invalid pit Index. It should be between 1..6 or 8..13"))
                .andReturn();
    }
}
