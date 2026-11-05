package bank.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Assignment 1, Withdrawal: robust worst-case BVA; domains explained in docs/assignment1.md. */
class WithdrawalFeeBoundaryTest {
    private final FeesCalculator calculator = new FeesCalculator();

    static Stream<Arguments> specifiedCases() {
        return cases(false);
    }

    static Stream<Arguments> exploratoryCases() {
        return cases(true);
    }

    private static Stream<Arguments> cases(boolean exploratory) {
        double[] amounts = {-0.01, 0, 0.01, 5000, 9999.99, 10000, 10000.01};
        double[] balances = {-0.01, 0, 0.01, 999.99, 1000, 1000.01, 4999.99,
            5000, 5000.01, 10000, 19999.99, 20000, 20000.01};
        List<Arguments> cases = new ArrayList<>();
        int number = 0;
        for (double amount : amounts) {
            for (double balance : balances) {
                for (boolean student : new boolean[] {false, true}) {
                    for (int day = 0; day <= 8; day++) {
                        String id = String.format("W%04d", ++number);
                        boolean unspecified = amount < 0 || balance < 0 || day < 1 || day > 7;
                        if (unspecified == exploratory) {
                            cases.add(Arguments.of(id, amount, balance, student, day));
                        }
                    }
                }
            }
        }
        return cases.stream();
    }

    @ParameterizedTest(name = "{0}: amount={1}, balance={2}, student={3}, day={4}")
    @MethodSource("specifiedCases")
    void withdrawalMatchesSpecification(String id, double amount, double balance,
            boolean student, int day) {
        // Oracle uses only Assignment 1's Withdrawal rules; Calendar uses Sunday=1.
        double rate;
        if (student) {
            rate = day == 1 || day == 7 ? 0.001 : 0;
        } else if (balance < 1000) {
            rate = 0.003;
        } else if (balance < 5000) {
            rate = 0.001;
        } else {
            rate = 0;
        }
        assertEquals(amount * rate,
            calculator.calculateWithdrawalFee(amount, balance, student, day), 1e-9, id);
    }

    @ParameterizedTest(name = "{0}: exploratory amount={1}, balance={2}, student={3}, day={4}")
    @MethodSource("exploratoryCases")
    void observeUnspecifiedInputs(String id, double amount, double balance,
            boolean student, int day) {
        String observed;
        try {
            observed = "returned " + calculator.calculateWithdrawalFee(amount, balance, student, day);
        } catch (RuntimeException exception) {
            observed = "threw " + exception.getClass().getName();
        }
        // Aborted (reported as skipped), never a fabricated conformance pass/failure.
        assumeTrue(false, id + ": no specified oracle; " + observed);
    }
}
