package com.mkr.server.tests;

import com.mkr.server.utils.DataValidations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class DataValidationsTests {
    @ParameterizedTest
    @ValueSource(strings = {
        "email@domain.com",
        "firstname.lastname@domain.com",
        "email@subdomain.domain.com",
        "_______@domain.com",
        "firstname-lastname@domain.com"
    })
    public void isEmailReturnsTrueOnValidInputTest(String value) {
        Assertions.assertTrue(DataValidations.isValidEmail(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "plain address",
        "email@domain@domain.com",
    })
    public void isEmailReturnsFalseOnInvalidInputTest(String value) {
        Assertions.assertFalse(DataValidations.isValidEmail(value));
    }

    @Test
    public void isValidTelNumberTest() {
        Assertions.assertTrue(DataValidations.isValidTelNumber("1234567890"));
        Assertions.assertFalse(DataValidations.isValidEmail("aa"));
        Assertions.assertFalse(DataValidations.isValidEmail("123456789a"));
    }
}
