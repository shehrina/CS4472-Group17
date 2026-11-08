package bank.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        // Seven-point boundary pattern around the specified fee-transition interval.
        double[] balances = {999.99, 1000, 1000.01, 3000, 4999.99, 5000, 5000.01};
        List<Arguments> cases = new ArrayList<>();
        int number = 0;
        for (double balance : balances) {
            for (boolean student : new boolean[] {false, true}) {
                // Monday and Saturday represent the two specified day categories.
                for (int day : new int[] {2, 7}) {
                    cases.add(Arguments.of(String.format("W%02d", ++number),
                        200.0, balance, student, day));
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

}
