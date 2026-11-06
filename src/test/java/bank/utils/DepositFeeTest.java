package bank.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Deposit fee tests using Weak Robust Equivalence Class testing.
 * Expected values come only from the Assignment 1 deposit specification.
 */
public class DepositFeeTest {

    private static final double DELTA = 0.0001;
    private final FeesCalculator calculator = new FeesCalculator();

    // Valid classes: each class of each variable is used at least once.
    @ParameterizedTest(name = "{0}: amount={1}, balance={2}, student={3} -> fee {4}")
    @CsvSource({
        // Student: amount > 50 and balance <= 500 -> 0.25%
        "D01, 100, 300, true, 0.25",
        // Student: amount <= 50 and 500 < balance <= 5000 -> no fee
        "D02, 30, 2000, true, 0.0",
        // Student: amount <= 50 and balance > 5000 -> 0.5%
        "D03, 30, 8000, true, 0.15",
        // Not a student: amount > 250 and balance <= 2500 -> 0.4%
        "D04, 500, 1000, false, 2.0",
        // Not a student: amount <= 250 and 2500 < balance <= 10000 -> 0.1%
        "D05, 100, 5000, false, 0.1",
        // Not a student: amount > 250 and balance > 10000 -> 0.8%
        "D06, 500, 15000, false, 4.0"
    })
    void validClasses(String id, double amount, double balance, boolean student, double expectedFee) {
        double actual = calculator.calculateDepositInterest(amount, balance, student);
        assertEquals(expectedFee, actual, DELTA, "Test " + id + " failed");
    }

    // Invalid classes: one invalid value per test, everything else valid.
    // Assumption: invalid input should be rejected with IllegalArgumentException.
    @ParameterizedTest(name = "{0}: amount={1}, balance={2}, student={3} -> rejected")
    @CsvSource({
        // Student, amount <= 0
        "D07, -100, 2000, true",
        // Student, balance < 0
        "D08, 30, -100, true",
        // Not a student, amount <= 0
        "D09, -100, 5000, false",
        // Not a student, balance < 0
        "D10, 100, -100, false"
    })
    void invalidClasses(String id, double amount, double balance, boolean student) {
        assertThrows(IllegalArgumentException.class,
            () -> calculator.calculateDepositInterest(amount, balance, student),
            "Test " + id + " failed");
    }
}
