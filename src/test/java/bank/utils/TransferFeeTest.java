package bank.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TransferFeeTest {

    private static final double DELTA = 0.0001;
    private final FeesCalculator calculator = new FeesCalculator();

    @ParameterizedTest(name = "{0}: student={1}, amt={2}, from={3}, to={4} -> fee {5}")
    @DisplayName("Transfer fee - decision table rules")
    @CsvSource({
        "S1,    true,    100,    1000,  500,   0.10",
        "S2,    true,    100,    1000,  1500,  0.05",
        "S3,    true,    100,    3000,  500,   0.05",
        "S4,    true,    100,    3000,  1500,  0.025",
        "S5,    true,    400,    1000,  500,   0.20",
        "S6,    true,    400,    1000,  1500,  0.10",
        "S7,    true,    400,    3000,  500,   1.00",
        "S8,    true,    400,    3000,  1500,  0.50",
        "N1,    false,   50,     2000,  1000,  0.10",
        "N2,    false,   50,     2000,  3000,  0.05",
        "N3,    false,   50,     6000,  1000,  0.50",
        "N4,    false,   50,     6000,  3000,  0.25",
        "N5,    false,   400,    1000,  500,   0.80",
        "N6,    false,   400,    1000,  1500,  0.40",
        "N7,    false,   400,    3000,  500,   2.00",
        "N8,    false,   400,    3000,  1500,  1.00"
    })
    void transferFeeMatchesDecisionTable(String id, boolean student, double amount,
                                         double from, double to, double expected) {
        double actual = calculator.calculateTransferFee(amount, from, to, student);
        assertEquals(expected, actual, DELTA, "Rule " + id + " failed");
    }
}
