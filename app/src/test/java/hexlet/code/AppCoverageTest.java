package hexlet.code;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AppCoverageTest {

    @Test
    void getPortUsesDefaultForBlankAndInvalidValues() {
        assertEquals(7070, App.getPort(null));
        assertEquals(7070, App.getPort(""));
        assertEquals(7070, App.getPort("   "));
        assertEquals(7070, App.getPort("not-a-number"));
    }

    @Test
    void getPortUsesProvidedValueWhenItIsValid() {
        assertEquals(8081, App.getPort("8081"));
    }
}
