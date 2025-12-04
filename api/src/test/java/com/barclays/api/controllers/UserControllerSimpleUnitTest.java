package com.barclays.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.barclays.api.domain.Address;
import com.barclays.api.domain.User;
import com.barclays.api.facade.UserFacade;

@ExtendWith(MockitoExtension.class)
public class UserControllerSimpleUnitTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserFacade userFacade;

    @Mock
    private Authentication authentication;

    @Test
    public void createUserReturnsCreatedUserWithStatus201() {
        // test fixtures
        final String principalEmail = "test@example.com";
        
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User inputUser = User.Builder.create()
                .withName("Test User")
                .withEmail(principalEmail)
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();

        final User createdUser = User.Builder.create()
                .withName("Test User")
                .withEmail(principalEmail)
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();
        createdUser.setId(1L);

        // given
        given(authentication.getName()).willReturn(principalEmail);
        given(userFacade.create(any(User.class), anyString()))
                .willReturn(createdUser);

        // when
        ResponseEntity<User> response = userController.createUser(inputUser, authentication);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        
        User responseBody = response.getBody();
        if (responseBody != null) {
            assertThat(responseBody.getId()).isEqualTo(1L);
            assertThat(responseBody.getEmail()).isEqualTo(principalEmail);
        }

        verify(authentication, times(1)).getName();
        verify(userFacade, times(1)).create(inputUser, principalEmail);
    }
}
