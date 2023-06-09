package com.openclassrooms.starterjwt.controllers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.junit.jupiter.api.Assertions.assertEquals;

//test
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureMockMvc
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanup.sql")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("gym@studio.com");
        loginRequest.setPassword("test!1234");
        restTemplate.postForEntity("/api/auth/login", loginRequest, JwtResponse.class);
    }

    @Test
    public void testGetUserById() throws Exception {
        MvcResult requestResult = mockMvc.perform(get("/api/user/1")
                .with(SecurityMockMvcRequestPostProcessors.user("gym@studio.com"))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is(200)).andReturn();
        String result = requestResult.getResponse().getContentAsString();
        User resultUser = objectMapper.readValue(result, User.class);
        assertEquals("Admin", resultUser.getFirstName());
    }

    @Test
    public void testGetUserByIdUserNull() throws Exception {
        MvcResult requestResult = mockMvc.perform(get("/api/user/3")
                        .with(SecurityMockMvcRequestPostProcessors.user("gym@studio.com"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andReturn();
        assertEquals(404, requestResult.getResponse().getStatus());
        assertTrue(requestResult.getResponse().getContentAsString().isEmpty());
    }

    @Test
    public void testGetUserByIdThrownError() throws Exception {
        MvcResult requestResult = mockMvc.perform(get("/api/user/test")
                        .with(SecurityMockMvcRequestPostProcessors.user("gym@studio.com"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andReturn();
        assertEquals(400, requestResult.getResponse().getStatus());
    }

    @Test
    public void testDeleteUserById() throws Exception {
        MvcResult requestResult  = mockMvc.perform(delete("/api/user/2")
                .with(SecurityMockMvcRequestPostProcessors.user("gym@studio.com"))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is(200)).andReturn();
        int code = requestResult.getResponse().getStatus();
        assertEquals(200, code);
       
    }

    @Test
    public void testDeleteUserByIdUserNull() throws Exception {
        MvcResult requestResult  = mockMvc.perform(delete("/api/user/3")
                        .with(SecurityMockMvcRequestPostProcessors.user("gym@studio.com"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andReturn();
        assertEquals(404, requestResult.getResponse().getStatus());
        assertTrue(requestResult.getResponse().getContentAsString().isEmpty());
    }

    @Test
    public void testDeleteUserByIdThrownError() throws Exception {
        MvcResult requestResult  = mockMvc.perform(delete("/api/user/test")
                        .with(SecurityMockMvcRequestPostProcessors.user("gym@studio.com"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andReturn();
        assertEquals(400, requestResult.getResponse().getStatus());
    }

}