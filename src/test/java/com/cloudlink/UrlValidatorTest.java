package com.cloudlink;
import com.cloudlink.validation.UrlValidation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlValidatorTest {

    private final UrlValidation validator = new UrlValidation();

    @Test
    void validHttpsUrl_returnsTrue() {
        assertTrue(validator.isValid("https://example.com"));
    }

    @Test
    void validHttpUrl_returnsTrue() {
        assertTrue(validator.isValid("http://google.com"));
    }

    @Test
    void nullUrl_returnsFalse() {
        assertFalse(validator.isValid(null));
    }

    @Test
    void emptyString_returnsFalse() {
        assertFalse(validator.isValid(""));
    }

    @Test
    void blankString_returnsFalse() {
        assertFalse(validator.isValid(" "));
    }

    @Test
    void randomText_returnsFalse() {
        assertFalse(validator.isValid("hello"));
    }

    @Test
    void malformedUrl_returnsFalse() {
        assertFalse(validator.isValid("ht!tp://abc"));
    }
}