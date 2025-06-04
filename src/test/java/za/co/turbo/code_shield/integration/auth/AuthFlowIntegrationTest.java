package za.co.turbo.code_shield.integration.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.UserRepository;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @AutoConfigureMockMvc
    @ActiveProfiles("test")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    public class AuthFlowIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;


        @Test
        void userLoginAndRegister_endToEndTest() throws Exception {
            // Register user
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                {
                    "username": "john",
                    "password": "password123",
                    "email": "john@email.com"
                }
            """))
                    .andExpect(status().isCreated());

            // Login and get token
           mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                {
                    "username": "john",
                    "password": "password123"
                }
            """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isString());
        }


        @Test
        void userSavedToDatabaseAfterRegistration() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                {
                    "username": "jane",
                    "password": "pass123",
                    "email": "jane@email.com"
                }
            """))
                    .andExpect(status().isCreated());

            Optional<User> userOpt = userRepository.findByUsername("jane");
            assertTrue(userOpt.isPresent());
        }

//        @Test
//        void generatePasswordHash() {
//            System.out.println(new BCryptPasswordEncoder().encode("password123"));
//        }

        @Sql("classpath:auth.sql")
        @Test
        void loginWithValidCredentials_shouldReturn200() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                {
                    "username": "testuser",
                    "password": "password123"
                }
            """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isString());

        }

        @Test
        void loginWithInvalidCredentials_shouldReturn401() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                {
                    "username": "invalid",
                    "password": "wrong"
                }
            """))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void registerDuplicateUser_shouldReturn409() throws Exception {
            // Register once
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                {
                    "username": "dupuser",
                    "password": "abc",
                    "email": "dup@email.com"
                }
            """))
                    .andExpect(status().isCreated());

            // Register again
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                {
                     "username": "dupuser",
                    "password": "abc",
                    "email": "dup@email.com"
                }
            """))
                    .andExpect(status().isConflict());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "a", "ab", "verylongusernamethatisinvalid"})
        void registerWithInvalidUsernames_shouldFail(String username) throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(String.format("""
                {
                    "username": "%s",
                    "password": "validPass123",
                    "email": "invalid@email.com"
                }
            """, username)))
                    .andExpect(status().isBadRequest());
        }


    }



