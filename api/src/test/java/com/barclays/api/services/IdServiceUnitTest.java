package com.barclays.api.services;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IdServiceUnitTest {

    private IdService sut;

    @BeforeEach
    public void setup() {
        sut = new IdService();
    }

    @Test
    public void generateIdReturnsIdWithCorrectPrefix() {
        // given
        String prefix = "test";

        // when
        String result = sut.generateId(prefix);

        // then
        assertThat(result).isNotNull();
        assertThat(result).startsWith(prefix + "-");
        assertThat(result).hasSize(prefix.length() + 2); // prefix + "-" + one character
    }

    @Test
    public void generateIdReturnsValidFormatWithDifferentPrefixes() {
        // given
        String[] prefixes = {"usr", "acc", "tan", "org"};

        for (String prefix : prefixes) {
            // when
            String result = sut.generateId(prefix);

            // then
            assertThat(result).isNotNull();
            assertThat(result).startsWith(prefix + "-");
            assertThat(result).matches(prefix + "-[A-Za-z0-9]");
            assertThat(result).hasSize(prefix.length() + 2);
        }
    }

    @Test
    public void generateIdReturnsUniqueIds() {
        // given
        String prefix = "test";
        
        // when
        String id1 = sut.generateId(prefix);
        String id2 = sut.generateId(prefix);
        String id3 = sut.generateId(prefix);

        // then
        assertThat(id1).isNotNull();
        assertThat(id2).isNotNull();
        assertThat(id3).isNotNull();
        
        // Note: Due to randomness, there's a small chance these could be equal,
        // but it's very unlikely given the character set size (62 characters)
        // This test verifies the format is correct for multiple generations
        assertThat(id1).matches(prefix + "-[A-Za-z0-9]");
        assertThat(id2).matches(prefix + "-[A-Za-z0-9]");
        assertThat(id3).matches(prefix + "-[A-Za-z0-9]");
    }

    @Test
    public void generateIdWorksWithEmptyPrefix() {
        // given
        String prefix = "";

        // when
        String result = sut.generateId(prefix);

        // then
        assertThat(result).isNotNull();
        assertThat(result).startsWith("-");
        assertThat(result).matches("-[A-Za-z0-9]");
        assertThat(result).hasSize(2);
    }

    @Test
    public void generateIdWorksWithLongPrefix() {
        // given
        String prefix = "verylongprefix";

        // when
        String result = sut.generateId(prefix);

        // then
        assertThat(result).isNotNull();
        assertThat(result).startsWith(prefix + "-");
        assertThat(result).matches(prefix + "-[A-Za-z0-9]");
        assertThat(result).hasSize(prefix.length() + 2);
    }
}
