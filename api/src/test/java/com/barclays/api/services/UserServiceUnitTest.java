package com.barclays.api.services;

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
import org.mockito.Mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barclays.api.dao.UserCrudRepository;
import com.barclays.api.domain.Address;
import com.barclays.api.domain.User;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    private UserService sut;

    @Mock
    private UserCrudRepository userCrudRepository;

    @BeforeEach
    public void setup() {
        sut = new UserService(userCrudRepository);
    }

    @Test
    public void findByEmailWithNullEmailThrowsAnException() {
        // test fixtures
        final String email = null;

        // when/then
        assertThatThrownBy(() -> sut.findByEmail(email))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findByEmailReturnsUserWhenExists() {
        // test fixtures
        final String email = "test@example.com";

        // test harness
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User expectedUser = User.Builder.create()
                .withName("Test User")
                .withEmail(email)
                .withPhoneNumber("+441234567890")
                .withAddress(address)
                .build();

        // given
        given(userCrudRepository.findByEmail(anyString()))
                .willReturn(expectedUser);

        // when
        final User result = sut.findByEmail(email);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(expectedUser);
        assertThat(result.getEmail()).isEqualTo(email);

        verify(userCrudRepository, only()).findByEmail(eq(email));
        verifyNoMoreInteractions(userCrudRepository);
    }

    @Test
    public void findByEmailReturnsNullWhenUserDoesNotExist() {
        // test fixtures
        final String email = "nonexistent@example.com";

        // given
        given(userCrudRepository.findByEmail(anyString()))
                .willReturn(null);

        // when
        final User result = sut.findByEmail(email);

        // then
        assertThat(result).isNull();

        verify(userCrudRepository, only()).findByEmail(eq(email));
        verifyNoMoreInteractions(userCrudRepository);
    }

    @Test
    public void saveUserWithNullUserThrowsAnException() {
        // test fixtures
        final User user = null;

        // when/then
        assertThatThrownBy(() -> sut.saveUser(user))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void saveUserSuccessfullyCreatesAndReturnsUser() {
        // test fixtures
        final Address address = Address.Builder.create()
                .withLine1("123 Test Street")
                .withTown("Test Town")
                .withCounty("Test County")
                .withPostcode("TE1 2ST")
                .build();

        final User userToSave = User.Builder.create()
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
        given(userCrudRepository.save(any(User.class)))
                .willReturn(savedUser);

        // when
        final User result = sut.saveUser(userToSave);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(savedUser);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");

        verify(userCrudRepository, only()).save(same(userToSave));
        verifyNoMoreInteractions(userCrudRepository);
    }
}
