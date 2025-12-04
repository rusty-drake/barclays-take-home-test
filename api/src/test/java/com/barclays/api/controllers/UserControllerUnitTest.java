package com.barclays.api.controllers;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.barclays.api.domain.Address;
import com.barclays.api.domain.User;
import com.barclays.api.facade.UserFacade;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
public class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserFacade userFacade;

    @Test
    public void createUserReturnsCreatedUserWithStatus201() throws Exception {
        // test fixtures
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User inputUser = User.Builder.create()
                .withName("Test User")
                .withEmail("test@example.com")
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();

        // test harness
        final User createdUser = User.Builder.create()
                .withName("Test User")
                .withEmail("test@example.com")
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();
        createdUser.setId(1L);

        // given
        given(userFacade.create(any(User.class)))
                .willReturn(createdUser);

        // when & then
        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+441234567890"))
                .andExpect(jsonPath("$.address.line1").value("123 Test Street"))
                .andExpect(jsonPath("$.address.town").value("Test Town"))
                .andExpect(jsonPath("$.address.county").value("Test County"))
                .andExpect(jsonPath("$.address.postcode").value("TE1 2ST"));
    }

    @Test
    public void createUserWithInvalidEmailReturnsBadRequest() throws Exception {
        // test fixtures
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User invalidUser = User.Builder.create()
                .withName("Test User")
                .withEmail("invalid-email")  // Invalid email format
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();

        // when & then
        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserWithMissingNameReturnsBadRequest() throws Exception {
        // test fixtures
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User invalidUser = User.Builder.create()
                .withName("")  // Empty name
                .withEmail("test@example.com")
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();

        // when & then
        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserWithInvalidPhoneNumberReturnsBadRequest() throws Exception {
        // test fixtures
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User invalidUser = User.Builder.create()
                .withName("Test User")
                .withEmail("test@example.com")
                .withPhoneNumber("123456")  // Invalid phone format
                .withAddress(address)
                .build();

        // when & then
        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }
}
