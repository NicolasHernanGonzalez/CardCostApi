package com.cardcostapi.domain;

import com.cardcostapi.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PanNumberTest {

    @Test
    void validate_ValidPan() {
        //SETUP
        PanNumber pan = new PanNumber();
        pan.setCardNumber("1234567890123456");

        //ASSERT
        assertDoesNotThrow(pan::validate);
    }

    @Test
    void validate_PanIsNull() {
        //SETUP
        PanNumber pan = new PanNumber();
        pan.setCardNumber(null);

        //ASSERT
        InvalidRequestException ex = assertThrows(InvalidRequestException.class, pan::validate);
        assertEquals("Invalid param: PAN is required", ex.getMessage());
    }

    @Test
    void validate_PanTooShort() {
        PanNumber pan = new PanNumber();
        pan.setCardNumber("1234567");

        //ASSERT
        InvalidRequestException ex = assertThrows(InvalidRequestException.class, pan::validate);
        assertEquals("Invalid param: PAN length must be between 8 and 19 digits", ex.getMessage());
    }

    @Test
    void calculateBinNumber_calculateBinNumber() {
        //SETUP
        PanNumber pan = new PanNumber();
        pan.setCardNumber("1234567890123456");

        //ASSERT
        String bin = pan.calculateBinNumber();
        assertEquals("12345678", bin);
    }
}
