package kz.temirlan.studentapi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PipelineFailureDemoTest {
    @Test
    void demonstratesPipelineFailure() {
        assertEquals(2, 1 + 0);
    }
}
