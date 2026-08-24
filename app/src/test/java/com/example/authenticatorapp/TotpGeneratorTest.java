package com.example.authenticatorapp;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TotpGeneratorTest {
    @Test
    public void generatesRfc6238SixDigitExample() throws Exception {
        assertEquals("287082", TotpGenerator.generate("GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ", 59_000L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidBase32() throws Exception {
        TotpGenerator.generate("not-a-valid-secret!", 59_000L);
    }
}
