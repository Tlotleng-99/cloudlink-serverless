package com.cloudlink;
import com.cloudlink.util.ShortCodeGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGeneratorTest {

    @Test
    void generateCode_returnsNonNullValue() {
        ShortCodeGenerator generator = new ShortCodeGenerator();

        String code = generator.generateCode();

        assertNotNull(code);
    }

    @Test
    void generateCode_returnsEightCharacters() {
        ShortCodeGenerator generator = new ShortCodeGenerator();

        String code = generator.generateCode();

        assertEquals(8, code.length());
    }

    @Test
    void generateCode_containsOnlyLettersAndNumbers() {
        ShortCodeGenerator generator = new ShortCodeGenerator();

        String code = generator.generateCode();

        assertTrue(code.matches("[A-Za-z0-9]{8}"));
    }

    @Test
    void generateCode_returnsDifferentCodes() {
        ShortCodeGenerator generator = new ShortCodeGenerator();

        String first = generator.generateCode();
        String second = generator.generateCode();

        assertNotEquals(first, second);
    }
}