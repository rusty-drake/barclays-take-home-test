package com.barclays.api.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import org.mockito.InOrder;
import org.mockito.Mock;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barclays.api.domain.Address;
import com.barclays.api.domain.User;
import com.barclays.api.exceptions.DuplicateResourceException;
import com.barclays.api.services.UserService;

@ExtendWith(MockitoExtension.class)
public class UserFacadeUnitTest {

    private UserFacade sut;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setup() {
        sut = new UserFacade(userService);
    }

    @Test
    public void createWithNullUserThrowsAnException() {
        // test fixtures
        final User user = null;
        final String principalEmail = "test@example.com";

        // when/then
        assertThatThrownBy(() -> sut.create(user, principalEmail))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createUserWillThrowDuplicateResourceExceptionIfUserAlreadyExists() {
        // test fixtures
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User newUser = User.Builder.create()
                .withName("Test User")
                .withEmail("test@example.com")
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();

        // test harness
        final User existingUser = User.Builder.create()
                .withName("Existing User")
                .withEmail("test@example.com")
                .withPhoneNumber("+441234567891")
                .withAddress(address)
                .build();
        existingUser.setId(1L);

        // given
        given(userService.findByEmail(anyString()))
                .willReturn(existingUser);

        // when
        try {
            sut.create(newUser, "test@example.com");
            
            // Should not reach here
            assertThat(false).as("create() should have thrown an exception").isTrue();
        } catch (Exception ex) {
            // then
            assertThat(ex).isInstanceOf(DuplicateResourceException.class);

            final DuplicateResourceException duplicateException = (DuplicateResourceException) ex;
            assertThat(duplicateException.getMessage())
                    .contains("User with email test@example.com already exists.");

            verify(userService, times(1)).findByEmail(eq("test@example.com"));
            verify(userService, never()).saveUser(any(User.class));
            verifyNoMoreInteractions(userService);
        }
    }

    @Test
    public void createUserSuccessfullyCreatesAndReturnsNewUser() {
        // test fixtures
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User newUser = User.Builder.create()
                .withName("Test User")
                .withEmail("test@example.com")
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();

        // test harness
        final User savedUser = User.Builder.create()
                .withName("Test User")
                .withEmail("test@example.com")
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();
        savedUser.setId(1L);

        // given
        given(userService.findByEmail(anyString()))
                .willReturn(null);
        given(userService.saveUser(any(User.class)))
                .willReturn(savedUser);

        // when
        final User result = sut.create(newUser, "test@example.com");

        // then
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(savedUser);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");

        final InOrder verificationOrder = inOrder(userService);

        verificationOrder.verify(userService, times(1))
                .findByEmail(eq("test@example.com"));
        verificationOrder.verify(userService, times(1))
                .saveUser(same(newUser));

        verifyNoMoreInteractions(userService);
    }

    @Test
    public void createUserWillThrowSecurityExceptionIfPrincipalEmailDoesNotMatchUserEmail() {
        // test fixtures
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User newUser = User.Builder.create()
                .withName("Test User")
                .withEmail("user@example.com")
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();

        final String principalEmail = "different@example.com";

        // when
        try {
            sut.create(newUser, principalEmail);
            
            // Should not reach here
            assertThat(false).as("create() should have thrown an exception").isTrue();
        } catch (Exception ex) {
            // then
            assertThat(ex).isInstanceOf(SecurityException.class);

            final SecurityException securityException = (SecurityException) ex;
            assertThat(securityException.getMessage())
                    .contains("Authenticated user does not match the user being created.");

            // Should not call any service methods when security validation fails
            verifyNoMoreInteractions(userService);
        }
    }
}
