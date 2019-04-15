package com.github.keeganwitt.katas.linkorganizer.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.keeganwitt.katas.linkorganizer.api.Application;
import com.github.keeganwitt.katas.linkorganizer.api.config.EmbeddedMysqlConfiguration;
import com.github.keeganwitt.katas.linkorganizer.api.model.Link;
import com.github.keeganwitt.katas.linkorganizer.api.model.Tag;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.StringReader;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class, EmbeddedMysqlConfiguration.class})
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles({"test"})
public class IntegrationTest {
    @Resource
    WebApplicationContext webApplicationContext;

    @Test
    @WithMockUser
    public void canAddRetrieveAndRemoveLinks() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String url = "www.test.net";
        Link link = new Link();
        link.setUrl(url);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // add link
        mockMvc.perform(MockMvcRequestBuilders.post("/links").content(mapper.writeValueAsString(link)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // retrieve link
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/links"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JsonObject json = Json.createReader(new StringReader(result.getResponse().getContentAsString())).readObject();
        JsonArray links = json.getJsonObject("_embedded").getJsonArray("links");
        Assert.assertEquals(1, links.size());

        // remove link
        mockMvc.perform(MockMvcRequestBuilders.delete("/links/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        // verify delete
        result = mockMvc.perform(MockMvcRequestBuilders.get("/links"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = Json.createReader(new StringReader(result.getResponse().getContentAsString())).readObject();
        links = json.getJsonObject("_embedded").getJsonArray("links");
        Assert.assertEquals(0, links.size());
    }

    @Test
    @WithMockUser
    public void canAddRetrieveAndRemoveTags() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String tagName = "search engines";
        Tag tag = new Tag();
        tag.setName(tagName);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // add tag
        mockMvc.perform(MockMvcRequestBuilders.post("/tags").content(mapper.writeValueAsString(tag)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // retrieve tag
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tags")).andReturn();
        JsonObject json = Json.createReader(new StringReader(result.getResponse().getContentAsString())).readObject();
        JsonArray tags = json.getJsonObject("_embedded").getJsonArray("tags");
        Assert.assertEquals(1, tags.size());

        // remove tag
        mockMvc.perform(MockMvcRequestBuilders.delete("/tags/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        // verify delete
        result = mockMvc.perform(MockMvcRequestBuilders.get("/tags"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        json = Json.createReader(new StringReader(result.getResponse().getContentAsString())).readObject();
        tags = json.getJsonObject("_embedded").getJsonArray("tags");
        Assert.assertEquals(0, tags.size());
    }
}
