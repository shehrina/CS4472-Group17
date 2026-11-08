# Assignment 1: Black Box Testing

## Part 1: Process and Assumptions

### 1. Session: PIN format and withdrawal amount (Robust Worst Case BVA)
We used Robust Worst Case BVA on the two inputs of a withdrawal: the PIN and the amount. Each input gets 7 values (min-, min, min+, nom, max-, max, max+), and worst case means we test every combination: 7 x 7 = 49 tests.

| Input | min- | min | min+ | nom | max- | max | max+ |
|---|---|---|---|---|---|---|---|
| PIN | -0001 | 00000 | 00001 | 50000 | 99998 | 99999 | 100000 |
| Amount ($) | 10 | 20 | 40 | 500 | 990 | 1000 | 1010 |

Assumptions:
- A valid PIN is any 5 digits, 00000 to 99999 (leading zeros allowed).
- A valid amount can be paid in $20 and $50 bills and is at most the $1,000 daily limit. The smallest one is $20. $30 can't be paid with those bills, so min+ is $40.
- The bank is faked with Mockito: a $5,000 balance and a $1,000 daily limit, and the credentials check always succeeds.
- The PIN is checked before the amount, so if the PIN is invalid we expect `InvalidPinFormatException`; if only the amount is invalid we expect `InvalidAmountException`; otherwise no exception.
- The spec says "under the single fault assumption", but the lecture slides (Lect2-Part2 slide 15; Lect3-Part1 slides 15-16) define robust worst case testing as *rejecting* the single fault assumption and taking the Cartesian product of the 7 values per variable. We followed the slides: 7 x 7 = 49 tests, which includes all 13 single-fault cases.
- Code: `src/test/java/atm/session/SessionTest.java`

### 2. FeesCalculator: Withdrawal (Robust Worst Case BVA)
We apply the seven-point robust boundary pattern to the specified fee-transition
interval [$1,000, $5,000], then take the full Cartesian product with both student
statuses and the two day categories. This gives **7 × 2 × 2 = 28 tests (W01–W28)**.

| Input | Selected values | Reason |
|---|---|---|
| Balance | 999.99, 1000, 1000.01, 3000, 4999.99, 5000, 5000.01 | min−, min, min+, nominal, max−, max, max+ around the two specified fee thresholds |
| Student | false, true | Both boolean values |
| Day | Monday, Saturday | Representatives of weekday and weekend; Calendar values 2 and 7 |
| Amount | 200 | Fixed positive amount; the specification gives no amount threshold |

Assumptions and scope:
- The $1,000 and $5,000 values are **fee thresholds, not legal account limits**.
  Values just outside that interval are valid and have specified fees. The
  nominal balance $3,000 is its midpoint; money neighbors differ by one cent.
- This is a threshold-focused adaptation of Robust Worst Case BVA: all selected
  boundary/category combinations are tested, rather than changing one input at
  a time. It is not a seven-value invalid-input grid for every method argument.
- No finite upper/lower amount domain or invalid-input behavior is specified.
  We therefore do not invent withdrawal limits, expected exceptions, or skipped
  exploratory cases. Session's daily limit and bill denominations do not apply.
- Monday and Saturday represent the two day categories in the written rules;
  other weekdays and Sunday are not separately enumerated.
- Expected fee = amount × specified rate: students pay 0 on weekdays and 0.001
  on weekends; non-students pay 0.003 below 1000, 0.001 from 1000 to below 5000,
  and 0 from 5000 onward. No rounding rule is specified; tolerance is 1e-9.
- Code: `src/test/java/bank/utils/WithdrawalFeeBoundaryTest.java`.

### 3. FeesCalculator: Deposit (Weak Robust Equivalence Class)
We used Weak Robust Equivalence Class testing on `calculateDepositInterest(amount, balance, student)`. We split each input into classes where the spec treats every value the same, then picked one value from inside each class. "Weak" means every valid class is used at least once, but we do not test every combination. "Robust" means we also add invalid classes, one invalid input per test with all other inputs valid.

The amount and balance cutoffs are different for students and non-students, so we made separate classes for each student value (student itself is a boolean with just two values and no invalid class).

| Variable | Valid classes | Invalid classes |
|---|---|---|
| Student | true, false | none (boolean) |
| Amount, student | A1: 0 < amount <= 50, A2: amount > 50 | amount <= 0 |
| Amount, non-student | A3: 0 < amount <= 250, A4: amount > 250 | amount <= 0 |
| Balance, student | B1: 0 <= balance <= 500, B2: 500 < balance <= 5,000, B3: balance > 5,000 | balance < 0 |
| Balance, non-student | B4: 0 <= balance <= 2,500, B5: 2,500 < balance <= 10,000, B6: balance > 10,000 | balance < 0 |

How we built the tests:
1. Valid tests: for each student value, amount has 2 classes and balance has 3, so weak testing needs max(2, 3) = 3 tests, which is 6 in total. We paired the classes so each test lands on a different fee rule (and so a different expected fee %).
2. Invalid tests: one test per invalid class for each student value (amount <= 0, balance < 0), so 4 tests, with the other inputs set to valid values.
3. Total: 6 + 4 = 10 tests (D01-D10).

| Test | Student | Amount class | Balance class | Spec rule (fee %) |
|---|---|---|---|---|
| D01 | Yes | A2 | B1 | amount > 50, balance <= 500: 0.25% |
| D02 | Yes | A1 | B2 | amount <= 50, balance <= 5,000: no fee |
| D03 | Yes | A1 | B3 | amount <= 50, balance > 5,000: 0.5% |
| D04 | No | A4 | B4 | amount > 250, balance <= 2,500: 0.4% |
| D05 | No | A3 | B5 | amount <= 250, balance <= 10,000: 0.1% |
| D06 | No | A4 | B6 | amount > 250, balance > 2,500: 0.8% |
| D07 | Yes | invalid (-100) | B2 | rejected |
| D08 | Yes | A1 | invalid (-100) | rejected |
| D09 | No | invalid (-100) | B5 | rejected |
| D10 | No | A3 | invalid (-100) | rejected |

Assumptions:
- The spec calls it a deposit "fee", but the method is named `calculateDepositInterest`. We test it as the fee in the spec: fee = fee % x amount, with a tolerance of 0.0001.
- A deposit amount must be more than $0, and a balance can't be negative. The spec gives no upper limit for either, so the top classes are open-ended.
- The spec doesn't say what happens with invalid input. We assumed the method should reject it by throwing `IllegalArgumentException`.
- We picked values well inside each class (e.g. $30, $100, $500 amounts), not on the edges, because equivalence class testing assumes any value in a class behaves the same. Edges are what boundary value analysis is for.
- The code is in `src/test/java/bank/utils/DepositFeeTest.java`.

### 4. FeesCalculator: Transfer (Decision Table)
We used Decision Table Analysis on `calculateTransferFee`. The spec is nested if/else conditions that each end in a fee percentage, so a decision table fits well.

Conditions (each is true/false):
- C1: the client is a student
- C2: the amount is small (student: under $200; non-student: under $100)
- C3: the source balance is low (student: under $2,000; non-student: under $4,000 if the amount is under $100, otherwise under $2,000)
- C4: the destination balance is low (student: under $1,000; non-student: under $2,000 if the amount is under $100, otherwise under $1,000)

How we built it:
1. We pulled the four conditions out of the spec. The cutoffs change with student/non-student and with the amount, so C2, C3 and C4 are each treated as one true/false condition inside their branch.
2. 4 conditions gives 2^4 = 16 combinations. Every one of them appears in the spec, so nothing was impossible or mergeable. That gives 16 rules, 8 for students and 8 for non-students.
3. The fee percentage for each rule comes straight from the spec.
4. We wrote one test per rule, using values that sit clearly inside each condition's region.

Assumptions:
- "Otherwise" at the last level means the destination balance is at or above the cutoff.
- We used normal values inside each region, not boundary values, because a decision table tests combinations of conditions.
- Fee = percentage x amount. The test allows a tolerance of 0.0001.
- Values used: students use $100 (small) and $400 (large); non-students use $50 (small) and $400 (large). Balances are well below or above the cutoffs.
- The code is in `src/test/java/bank/utils/TransferFeeTest.java`.

T = condition true, F = condition false. Fee % is the % of the amount transferred.

| Rule | Student | C2 | C3 | C4 | Fee % | Test |
|---|---|---|---|---|---|---|
| 1 | Yes | T | T | T | 0.1 | T01 |
| 2 | Yes | T | T | F | 0.05 | T02 |
| 3 | Yes | T | F | T | 0.05 | T03 |
| 4 | Yes | T | F | F | 0.025 | T04 |
| 5 | Yes | F | T | T | 0.05 | T05 |
| 6 | Yes | F | T | F | 0.025 | T06 |
| 7 | Yes | F | F | T | 0.25 | T07 |
| 8 | Yes | F | F | F | 0.125 | T08 |
| 9 | No | T | T | T | 0.2 | T09 |
| 10 | No | T | T | F | 0.1 | T10 |
| 11 | No | T | F | T | 1 | T11 |
| 12 | No | T | F | F | 0.5 | T12 |
| 13 | No | F | T | T | 0.2 | T13 |
| 14 | No | F | T | F | 0.1 | T14 |
| 15 | No | F | F | T | 0.5 | T15 |
| 16 | No | F | F | F | 0.25 | T16 |

## Part 2: Test Cases and Results

### 1. Session
| ID | PIN | Amount | Expected | Actual | Pass/Fail |
|---|---|---|---|---|---|
| S01 | -0001 | 10 | PIN error | PIN error | Pass |
| S02 | -0001 | 20 | PIN error | PIN error | Pass |
| S03 | -0001 | 40 | PIN error | PIN error | Pass |
| S04 | -0001 | 500 | PIN error | PIN error | Pass |
| S05 | -0001 | 990 | PIN error | PIN error | Pass |
| S06 | -0001 | 1000 | PIN error | PIN error | Pass |
| S07 | -0001 | 1010 | PIN error | PIN error | Pass |
| S08 | 00000 | 10 | Amount error | PIN error | Fail |
| S09 | 00000 | 20 | OK | PIN error | Fail |
| S10 | 00000 | 40 | OK | PIN error | Fail |
| S11 | 00000 | 500 | OK | PIN error | Fail |
| S12 | 00000 | 990 | OK | PIN error | Fail |
| S13 | 00000 | 1000 | OK | PIN error | Fail |
| S14 | 00000 | 1010 | Amount error | PIN error | Fail |
| S15 | 00001 | 10 | Amount error | PIN error | Fail |
| S16 | 00001 | 20 | OK | PIN error | Fail |
| S17 | 00001 | 40 | OK | PIN error | Fail |
| S18 | 00001 | 500 | OK | PIN error | Fail |
| S19 | 00001 | 990 | OK | PIN error | Fail |
| S20 | 00001 | 1000 | OK | PIN error | Fail |
| S21 | 00001 | 1010 | Amount error | PIN error | Fail |
| S22 | 50000 | 10 | Amount error | PIN error | Fail |
| S23 | 50000 | 20 | OK | PIN error | Fail |
| S24 | 50000 | 40 | OK | PIN error | Fail |
| S25 | 50000 | 500 | OK | PIN error | Fail |
| S26 | 50000 | 990 | OK | PIN error | Fail |
| S27 | 50000 | 1000 | OK | PIN error | Fail |
| S28 | 50000 | 1010 | Amount error | PIN error | Fail |
| S29 | 99998 | 10 | Amount error | PIN error | Fail |
| S30 | 99998 | 20 | OK | PIN error | Fail |
| S31 | 99998 | 40 | OK | PIN error | Fail |
| S32 | 99998 | 500 | OK | PIN error | Fail |
| S33 | 99998 | 990 | OK | PIN error | Fail |
| S34 | 99998 | 1000 | OK | PIN error | Fail |
| S35 | 99998 | 1010 | Amount error | PIN error | Fail |
| S36 | 99999 | 10 | Amount error | PIN error | Fail |
| S37 | 99999 | 20 | OK | PIN error | Fail |
| S38 | 99999 | 40 | OK | PIN error | Fail |
| S39 | 99999 | 500 | OK | PIN error | Fail |
| S40 | 99999 | 990 | OK | PIN error | Fail |
| S41 | 99999 | 1000 | OK | PIN error | Fail |
| S42 | 99999 | 1010 | Amount error | PIN error | Fail |
| S43 | 100000 | 10 | PIN error | PIN error | Pass |
| S44 | 100000 | 20 | PIN error | PIN error | Pass |
| S45 | 100000 | 40 | PIN error | PIN error | Pass |
| S46 | 100000 | 500 | PIN error | PIN error | Pass |
| S47 | 100000 | 990 | PIN error | PIN error | Pass |
| S48 | 100000 | 1000 | PIN error | PIN error | Pass |
| S49 | 100000 | 1010 | PIN error | PIN error | Pass |

PIN error = `InvalidPinFormatException`, Amount error = `InvalidAmountException`.

**Results:** 14 passed, 35 failed (S08-S42). Every valid 5-digit PIN was rejected, so the amount step was never reached for those tests.

### 2. Withdrawal

Revised suite executed November 6, 2026 on Temurin Java 11 / Maven 3.9.9.
Checkstyle and the informational full-suite run are checked locally; the
production code is unchanged. Table outcomes come from Surefire XML via
`python3 scripts/withdrawal_results.py`. Successful values are recorded within
assertion tolerance; exact successful return values are not separately logged.

#### Withdrawal fault localization

All three faults are in `bank.utils.FeesCalculator.calculateWithdrawalFee`.

| Failure ID | Failing cases | Suspected statement | Justification |
|---|---|---|---|
| WF1 | W03–W04, W07–W08, W11–W12, W15–W16, W19–W20, W23–W24, W27–W28 | Lines 12–15: weekend rate 0, weekday rate 0.001 | Student rates are reversed: weekdays should be free and weekends should cost 0.1%. |
| WF2 | W01–W02 | Line 19: feePercentage = 0.002 | A non-student below $1,000 should pay 0.3%; $200 should cost $0.60, but costs $0.40. |
| WF3 | W21–W22, W25–W26 | Line 20: accountBalance < 10000.00 | Non-student fees should stop at $5,000; the implementation continues charging $0.20 on $200 until $10,000. |

See [failure-log.md](failure-log.md) for retained original discovery records
and the revised case-ID mapping.

<!-- GENERATED RESULTS: replaced by scripts/withdrawal_results.py -->

**Results:** 8 passed, 20 failed; no errors or skipped cases.

| ID | Amount | Balance | Student | Day | Expected fee | Actual fee | Pass/Fail | Fault |
|---|---|---|---|---|---|---|---|---|
| W01 | 200 | 999.99 | False | Monday | 0.6 | 0.4 | Fail | WF2 |
| W02 | 200 | 999.99 | False | Saturday | 0.6 | 0.4 | Fail | WF2 |
| W03 | 200 | 999.99 | True | Monday | 0 | 0.2 | Fail | WF1 |
| W04 | 200 | 999.99 | True | Saturday | 0.2 | 0.0 | Fail | WF1 |
| W05 | 200 | 1000 | False | Monday | 0.2 | 0.2 (within tolerance) | Pass | — |
| W06 | 200 | 1000 | False | Saturday | 0.2 | 0.2 (within tolerance) | Pass | — |
| W07 | 200 | 1000 | True | Monday | 0 | 0.2 | Fail | WF1 |
| W08 | 200 | 1000 | True | Saturday | 0.2 | 0.0 | Fail | WF1 |
| W09 | 200 | 1000.01 | False | Monday | 0.2 | 0.2 (within tolerance) | Pass | — |
| W10 | 200 | 1000.01 | False | Saturday | 0.2 | 0.2 (within tolerance) | Pass | — |
| W11 | 200 | 1000.01 | True | Monday | 0 | 0.2 | Fail | WF1 |
| W12 | 200 | 1000.01 | True | Saturday | 0.2 | 0.0 | Fail | WF1 |
| W13 | 200 | 3000 | False | Monday | 0.2 | 0.2 (within tolerance) | Pass | — |
| W14 | 200 | 3000 | False | Saturday | 0.2 | 0.2 (within tolerance) | Pass | — |
| W15 | 200 | 3000 | True | Monday | 0 | 0.2 | Fail | WF1 |
| W16 | 200 | 3000 | True | Saturday | 0.2 | 0.0 | Fail | WF1 |
| W17 | 200 | 4999.99 | False | Monday | 0.2 | 0.2 (within tolerance) | Pass | — |
| W18 | 200 | 4999.99 | False | Saturday | 0.2 | 0.2 (within tolerance) | Pass | — |
| W19 | 200 | 4999.99 | True | Monday | 0 | 0.2 | Fail | WF1 |
| W20 | 200 | 4999.99 | True | Saturday | 0.2 | 0.0 | Fail | WF1 |
| W21 | 200 | 5000 | False | Monday | 0 | 0.2 | Fail | WF3 |
| W22 | 200 | 5000 | False | Saturday | 0 | 0.2 | Fail | WF3 |
| W23 | 200 | 5000 | True | Monday | 0 | 0.2 | Fail | WF1 |
| W24 | 200 | 5000 | True | Saturday | 0.2 | 0.0 | Fail | WF1 |
| W25 | 200 | 5000.01 | False | Monday | 0 | 0.2 | Fail | WF3 |
| W26 | 200 | 5000.01 | False | Saturday | 0 | 0.2 | Fail | WF3 |
| W27 | 200 | 5000.01 | True | Monday | 0 | 0.2 | Fail | WF1 |
| W28 | 200 | 5000.01 | True | Saturday | 0.2 | 0.0 | Fail | WF1 |

<!-- END GENERATED WITHDRAWAL RESULTS -->

### 3. Deposit
| ID | Amount | Balance | Student | Expected Fee | Actual Fee | Pass/Fail |
|---|---|---|---|---|---|---|
| D01 | 100 | 300 | True | 0.25 | 0.2 | Fail |
| D02 | 30 | 2000 | True | 0 | 0.06 | Fail |
| D03 | 30 | 8000 | True | 0.15 | 0.15 | Pass |
| D04 | 500 | 1000 | False | 2 | 0 | Fail |
| D05 | 100 | 5000 | False | 0.1 | 0 | Fail |
| D06 | 500 | 15000 | False | 4 | 2.5 | Fail |
| D07 | -100 | 2000 | True | Rejected | Returned -0.2 | Fail |
| D08 | 30 | -100 | True | Rejected | Returned 0.06 | Fail |
| D09 | -100 | 5000 | False | Rejected | Returned -0.0 | Fail |
| D10 | 100 | -100 | False | Rejected | Returned 0.0 | Fail |

Rejected = throws `IllegalArgumentException`.

**Results:** 1 passed, 9 failed (D01, D02, D04-D10).

#### Deposit fault localization

All faults below are in `bank.utils.FeesCalculator.calculateDepositInterest`.

| Failure ID | Cases | Suspected statement | Justification |
|---|---|---|---|
| DF1 | D01 | Student branch: `amount > 100.00` (line 39) | The spec cutoff is $50, so a $100 deposit should get the "more than $50" rate, but the code sends it to the small-amount branch. |
| DF2 | D01, D02 | Student, small amount, balance <= 5,000: `interestPercentage = 0.002` (line 50) | The spec says no fee here, and both observed fees equal amount x 0.002 (0.2 and 0.06). |
| DF3 | D04, D06 | Non-student branch: `amount > 500.00` (line 54) | The spec cutoff is $250, so a $500 deposit should get the large-amount rates, but the code sends it to the small-amount branch. |
| DF4 | D04, D05 | Non-student, small amount, balance <= 10,000: `interestPercentage = 0.0` (line 64) | The spec says 0.1%, but both cases return a fee of 0. |
| DF5 | D06 | Non-student, small amount, balance > 10,000: `interestPercentage = 0.005` (line 62) | The spec says no fee for a small deposit with a balance over $10,000, but the observed fee is 500 x 0.005 = 2.5. |
| DF6 | D07-D10 | Whole method: no check for amount <= 0 or balance < 0 | Under our assumption invalid input should be rejected, but the method just multiplies and returns a value (even a negative fee for D07). |

D01, D04 and D06 each fail because of two faults working together (a wrong amount cutoff plus a wrong rate in the branch it leads to). Reading the code also shows wrong cutoffs and rates in the large-amount branches (lines 41-44 and 55-58), which no test reached, because DF1 and DF3 send our test values to the small-amount branch. Weak equivalence class testing picks only one value per class, so a wrong cutoff can hide the branch behind it.

### 4. Transfer
| ID | Amount | From Balance | To Balance | Student | Expected Fee | Actual Fee | Pass/Fail |
|---|---|---|---|---|---|---|---|
| T01 | 100 | 1000 | 500 | True | 0.1 | 2.5 | Fail |
| T02 | 100 | 1000 | 1500 | True | 0.05 | 1.25 | Fail |
| T03 | 100 | 3000 | 500 | True | 0.05 | 2.5 | Fail |
| T04 | 100 | 3000 | 1500 | True | 0.025 | 1.25 | Fail |
| T05 | 400 | 1000 | 500 | True | 0.2 | 10 | Fail |
| T06 | 400 | 1000 | 1500 | True | 0.1 | 5 | Fail |
| T07 | 400 | 3000 | 500 | True | 1 | 10 | Fail |
| T08 | 400 | 3000 | 1500 | True | 0.5 | 5 | Fail |
| T09 | 50 | 2000 | 1000 | False | 0.1 | 2.5 | Fail |
| T10 | 50 | 2000 | 3000 | False | 0.05 | 2.5 | Fail |
| T11 | 50 | 6000 | 1000 | False | 0.5 | 2.5 | Fail |
| T12 | 50 | 6000 | 3000 | False | 0.25 | 2.5 | Fail |
| T13 | 400 | 1000 | 500 | False | 0.8 | 20 | Fail |
| T14 | 400 | 1000 | 1500 | False | 0.4 | 22 | Fail |
| T15 | 400 | 3000 | 500 | False | 2 | 20 | Fail |
| T16 | 400 | 3000 | 1500 | False | 1 | 22 | Fail |

**Results:** 0 passed, 16 failed.

#### Transfer fault localization

All faults below are in `bank.utils.FeesCalculator.calculateTransferFee`.

| Failure ID | Cases | Suspected statement | Justification |
|---|---|---|---|
| TF1 | T01-T08 | Student branch: all eight `feePercentage` values | Every student case returns a fee far above the spec percentage (about 10x, and 100x where amount < 100 and from >= 1000). |
| TF2 | T01-T04 | Student branch: `amount < 100.00` | The spec says small means under $200, so $100 should count as small but the code sends it to the large branch. |
| TF3 | T01, T02, T05, T06 | Student branch: `fromAccountBalance < 1000.00` | The spec says a low source balance is under $2,000, so $1,000 should count as low but the code treats it as high. |
| TF4 | T09-T16 | Non-student branch: all eight `feePercentage` values | Every non-student case returns a fee far above the spec percentage. |
| TF5 | T09, T10, T11, T13, T14 | Non-student branch: `fromAccountBalance < 1000.00` and `toAccountBalance < 1000.00` | The spec cutoffs are $4,000 / $2,000 (source) and $2,000 (destination, amount under $100), so balances like from = $2,000 or to = $1,000 are low in the spec but high in the code. |
| TF6 | T14, T16 | Non-student branch, amount >= 100, both balances high: `feePercentage = 0.055` | The spec says 0.25%, and 0.055 is out of line even with the other wrong values. |

Because the percentages are wrong in every branch, every test fails anyway, so these tests can't prove the cutoff faults (TF2, TF3, TF5) on their own. We found those by reading the code next to the spec.