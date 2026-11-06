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


Scope: `bank.utils.FeesCalculator.calculateWithdrawalFee`; Assignment 1's
Withdrawal specification. Session, deposit, and transfer deliverables belong
to other team members and are not completed by this document.

Technique: **Robust Worst Case Boundary Value Analysis**. Choose min−,
min, min+, nominal, max−, max, max+ for ordered domains and take their full
Cartesian product, allowing multiple out-of-domain inputs simultaneously.
This is not single-fault BVA. Booleans have two values rather than numerical
neighbors. Add internal fee-threshold neighbors and enumerate all valid days.

The assignment defines no finite amount/balance upper bounds or invalid-input
oracle for this method. The following are explicit sampling assumptions,
not bank limits or claimed requirements:

| Input | Selected values | Rationale |
|---|---|---|
| Amount | −0.01, 0, 0.01, 5000, 9999.99, 10000, 10000.01 | Assumed sampling window [0,10000], nominal 5000, one-cent neighbors |
| Balance | −0.01, 0, 0.01, 999.99, 1000, 1000.01, 4999.99, 5000, 5000.01, 10000, 19999.99, 20000, 20000.01 | Assumed sampling window [0,20000], nominal 10000, plus neighbors at specified thresholds 1000 and 5000 |
| Student | false, true | Entire boolean domain |
| Day | 0,1,2,3,4,5,6,7,8 | Calendar convention: Sunday=1, Saturday=7; robust endpoint neighbors 0/8, all seven valid days |

The canonical seven-point numerical grid is augmented to 13 balance values
and 9 day values: **7 × 13 × 2 × 9 = 1638** combinations, identified W0001–W1638
in amount/balance/student/day order. Valid amounts/balances include values
above the sampling windows: those windows are not rejection boundaries.
Zero is an assumed admissible arithmetic input and produces zero fee.
Negative amount/balance and days outside 1–7 are exploratory, not conformance
cases. No expected exceptions are invented. NaN/infinity, sub-cent amounts,
and other non-finite or extended domains are outside this finite design.

This fee function is tested independently: amount need not be dispensable in
bills, be below balance, or satisfy Session's $1000 daily limit or $5000 balance.
Money uses one-cent neighbors for sampling, but the specification requires no
fee rounding: assert the raw amount × rate with absolute tolerance 1e−9.

The independent oracle is derived from the written rules:

| Student | Day / balance | Expected rate |
|---|---|---|
| Yes | Monday–Friday | 0 |
| Yes | Saturday/Sunday | 0.001 |
| No | Balance < 1000 | 0.003 |
| No | 1000 ≤ balance < 5000 | 0.001 |
| No | Balance ≥ 5000 | 0 |

Conformance cases use that oracle. Exploratory cases call the method and
record the return value/exception, then deliberately abort via a JUnit
assumption. Their SKIP status means **no specified oracle**, not success or
an unexecuted method. They do not establish missing validation as a defect.

### 3. FeesCalculator: Deposit (Weak Robust Equivalence Class)
TODO

### 4. FeesCalculator: Transfer (Decision Table)
TODO

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


Executed November 4, 2026 using portable Temurin Java 11 and Maven 3.9.9.
Course source: `grimasta/WesternCS4472-ATM-Assignment` main at
`a617ec3` (the remote default master is an empty starter).
Team history starts at CI commit `54ed1a6`, preserved on local dev.
Production files were imported unchanged. Compiler 3.11.0 and Surefire 3.2.5
are pinned in the POM to support Java 11 and discover JUnit 5 reliably.

Commands (use installed Java 11 and Maven, or substitute their portable paths):

```sh
mvn -B checkstyle:check
mvn -B -Dmaven.test.failure.ignore=true test
python3 scripts/ci_reports.py
python3 scripts/withdrawal_results.py
```

Checkstyle: zero violations. Informational Maven execution succeeds while
retaining test failures in Surefire XML. The original withdrawal-only run included three passing starter tests.
Session results are documented separately above; combined-suite verification follows integration. JaCoCo generates HTML and XML under target/site/jacoco.

#### Withdrawal fault localization

All faults below are in `bank.utils.FeesCalculator.calculateWithdrawalFee`.
The table groups every conformance failure; case tables below assign the group.

| Failure ID | Cases | Suspected statement | Justification |
|---|---|---|---|
| WF1 | Student, positive amount, valid day; 420 failures | Lines 12–15, weekend gives 0 and weekday gives 0.001 | Specification requires weekend 0.001 and weekday 0, so both student assignments are reversed. |
| WF2 | Non-student, positive amount, balance <1000; 105 failures | Line 19, feePercentage = 0.002 | Expected rate is 0.003, but observed fee equals amount × 0.002. |
| WF3 | Non-student, positive amount, 5000 ≤ balance <10000; 70 failures | Line 20, accountBalance < 10000.00 | Expected zero fee from 5000 onward, but this condition charges 0.001 until 10000. |

Zero amounts mask rate faults because every rate yields zero. Passing cases
therefore do not prove the corresponding decision branch is correct.
See [failure log](failure-log.md) for timing limitations and grouped evidence.

<!-- GENERATED RESULTS: replaced by scripts/withdrawal_results.py -->

Observed withdrawal totals: **413 PASS, 595 FAIL, 630 exploratory SKIP**, no errors.

PASS observations are bounded by the assertion tolerance; exact successful return values were not logged.

### Specification-derived cases

| ID | Amount | Balance | Student | Day | Expected fee | Observed | Result | Fault |
|---|---|---|---|---|---|---|---|---|
| W0254 | 0 | 0 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0255 | 0 | 0 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0256 | 0 | 0 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0257 | 0 | 0 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0258 | 0 | 0 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0259 | 0 | 0 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0260 | 0 | 0 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0263 | 0 | 0 | True | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0264 | 0 | 0 | True | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0265 | 0 | 0 | True | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0266 | 0 | 0 | True | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0267 | 0 | 0 | True | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0268 | 0 | 0 | True | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0269 | 0 | 0 | True | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0272 | 0 | 0.01 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0273 | 0 | 0.01 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0274 | 0 | 0.01 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0275 | 0 | 0.01 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0276 | 0 | 0.01 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0277 | 0 | 0.01 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0278 | 0 | 0.01 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0281 | 0 | 0.01 | True | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0282 | 0 | 0.01 | True | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0283 | 0 | 0.01 | True | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0284 | 0 | 0.01 | True | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0285 | 0 | 0.01 | True | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0286 | 0 | 0.01 | True | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0287 | 0 | 0.01 | True | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0290 | 0 | 999.99 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0291 | 0 | 999.99 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0292 | 0 | 999.99 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0293 | 0 | 999.99 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0294 | 0 | 999.99 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0295 | 0 | 999.99 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0296 | 0 | 999.99 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0299 | 0 | 999.99 | True | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0300 | 0 | 999.99 | True | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0301 | 0 | 999.99 | True | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0302 | 0 | 999.99 | True | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0303 | 0 | 999.99 | True | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0304 | 0 | 999.99 | True | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0305 | 0 | 999.99 | True | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0308 | 0 | 1000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0309 | 0 | 1000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0310 | 0 | 1000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0311 | 0 | 1000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0312 | 0 | 1000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0313 | 0 | 1000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0314 | 0 | 1000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0317 | 0 | 1000 | True | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0318 | 0 | 1000 | True | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0319 | 0 | 1000 | True | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0320 | 0 | 1000 | True | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0321 | 0 | 1000 | True | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0322 | 0 | 1000 | True | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0323 | 0 | 1000 | True | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0326 | 0 | 1000.01 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0327 | 0 | 1000.01 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0328 | 0 | 1000.01 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0329 | 0 | 1000.01 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0330 | 0 | 1000.01 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0331 | 0 | 1000.01 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0332 | 0 | 1000.01 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0335 | 0 | 1000.01 | True | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0336 | 0 | 1000.01 | True | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0337 | 0 | 1000.01 | True | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0338 | 0 | 1000.01 | True | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0339 | 0 | 1000.01 | True | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0340 | 0 | 1000.01 | True | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0341 | 0 | 1000.01 | True | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0344 | 0 | 4999.99 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0345 | 0 | 4999.99 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0346 | 0 | 4999.99 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0347 | 0 | 4999.99 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0348 | 0 | 4999.99 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0349 | 0 | 4999.99 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0350 | 0 | 4999.99 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0353 | 0 | 4999.99 | True | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0354 | 0 | 4999.99 | True | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0355 | 0 | 4999.99 | True | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0356 | 0 | 4999.99 | True | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0357 | 0 | 4999.99 | True | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0358 | 0 | 4999.99 | True | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0359 | 0 | 4999.99 | True | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0362 | 0 | 5000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0363 | 0 | 5000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0364 | 0 | 5000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0365 | 0 | 5000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0366 | 0 | 5000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0367 | 0 | 5000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0368 | 0 | 5000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0371 | 0 | 5000 | True | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0372 | 0 | 5000 | True | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0373 | 0 | 5000 | True | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0374 | 0 | 5000 | True | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0375 | 0 | 5000 | True | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0376 | 0 | 5000 | True | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0377 | 0 | 5000 | True | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0380 | 0 | 5000.01 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0381 | 0 | 5000.01 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0382 | 0 | 5000.01 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0383 | 0 | 5000.01 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0384 | 0 | 5000.01 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0385 | 0 | 5000.01 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0386 | 0 | 5000.01 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0389 | 0 | 5000.01 | True | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0390 | 0 | 5000.01 | True | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0391 | 0 | 5000.01 | True | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0392 | 0 | 5000.01 | True | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0393 | 0 | 5000.01 | True | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0394 | 0 | 5000.01 | True | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0395 | 0 | 5000.01 | True | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0398 | 0 | 10000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0399 | 0 | 10000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0400 | 0 | 10000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0401 | 0 | 10000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0402 | 0 | 10000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0403 | 0 | 10000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0404 | 0 | 10000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0407 | 0 | 10000 | True | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0408 | 0 | 10000 | True | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0409 | 0 | 10000 | True | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0410 | 0 | 10000 | True | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0411 | 0 | 10000 | True | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0412 | 0 | 10000 | True | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0413 | 0 | 10000 | True | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0416 | 0 | 19999.99 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0417 | 0 | 19999.99 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0418 | 0 | 19999.99 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0419 | 0 | 19999.99 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0420 | 0 | 19999.99 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0421 | 0 | 19999.99 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0422 | 0 | 19999.99 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0425 | 0 | 19999.99 | True | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0426 | 0 | 19999.99 | True | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0427 | 0 | 19999.99 | True | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0428 | 0 | 19999.99 | True | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0429 | 0 | 19999.99 | True | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0430 | 0 | 19999.99 | True | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0431 | 0 | 19999.99 | True | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0434 | 0 | 20000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0435 | 0 | 20000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0436 | 0 | 20000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0437 | 0 | 20000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0438 | 0 | 20000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0439 | 0 | 20000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0440 | 0 | 20000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0443 | 0 | 20000 | True | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0444 | 0 | 20000 | True | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0445 | 0 | 20000 | True | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0446 | 0 | 20000 | True | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0447 | 0 | 20000 | True | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0448 | 0 | 20000 | True | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0449 | 0 | 20000 | True | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0452 | 0 | 20000.01 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0453 | 0 | 20000.01 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0454 | 0 | 20000.01 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0455 | 0 | 20000.01 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0456 | 0 | 20000.01 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0457 | 0 | 20000.01 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0458 | 0 | 20000.01 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0461 | 0 | 20000.01 | True | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0462 | 0 | 20000.01 | True | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0463 | 0 | 20000.01 | True | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0464 | 0 | 20000.01 | True | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0465 | 0 | 20000.01 | True | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0466 | 0 | 20000.01 | True | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0467 | 0 | 20000.01 | True | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0488 | 0.01 | 0 | False | 1 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0489 | 0.01 | 0 | False | 2 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0490 | 0.01 | 0 | False | 3 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0491 | 0.01 | 0 | False | 4 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0492 | 0.01 | 0 | False | 5 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0493 | 0.01 | 0 | False | 6 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0494 | 0.01 | 0 | False | 7 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0497 | 0.01 | 0 | True | 1 | 1e-05 | 0.0 | FAIL | WF1 |
| W0498 | 0.01 | 0 | True | 2 | 0 | 1.0E-5 | FAIL | WF1 |
| W0499 | 0.01 | 0 | True | 3 | 0 | 1.0E-5 | FAIL | WF1 |
| W0500 | 0.01 | 0 | True | 4 | 0 | 1.0E-5 | FAIL | WF1 |
| W0501 | 0.01 | 0 | True | 5 | 0 | 1.0E-5 | FAIL | WF1 |
| W0502 | 0.01 | 0 | True | 6 | 0 | 1.0E-5 | FAIL | WF1 |
| W0503 | 0.01 | 0 | True | 7 | 1e-05 | 0.0 | FAIL | WF1 |
| W0506 | 0.01 | 0.01 | False | 1 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0507 | 0.01 | 0.01 | False | 2 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0508 | 0.01 | 0.01 | False | 3 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0509 | 0.01 | 0.01 | False | 4 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0510 | 0.01 | 0.01 | False | 5 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0511 | 0.01 | 0.01 | False | 6 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0512 | 0.01 | 0.01 | False | 7 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0515 | 0.01 | 0.01 | True | 1 | 1e-05 | 0.0 | FAIL | WF1 |
| W0516 | 0.01 | 0.01 | True | 2 | 0 | 1.0E-5 | FAIL | WF1 |
| W0517 | 0.01 | 0.01 | True | 3 | 0 | 1.0E-5 | FAIL | WF1 |
| W0518 | 0.01 | 0.01 | True | 4 | 0 | 1.0E-5 | FAIL | WF1 |
| W0519 | 0.01 | 0.01 | True | 5 | 0 | 1.0E-5 | FAIL | WF1 |
| W0520 | 0.01 | 0.01 | True | 6 | 0 | 1.0E-5 | FAIL | WF1 |
| W0521 | 0.01 | 0.01 | True | 7 | 1e-05 | 0.0 | FAIL | WF1 |
| W0524 | 0.01 | 999.99 | False | 1 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0525 | 0.01 | 999.99 | False | 2 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0526 | 0.01 | 999.99 | False | 3 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0527 | 0.01 | 999.99 | False | 4 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0528 | 0.01 | 999.99 | False | 5 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0529 | 0.01 | 999.99 | False | 6 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0530 | 0.01 | 999.99 | False | 7 | 3e-05 | 2.0E-5 | FAIL | WF2 |
| W0533 | 0.01 | 999.99 | True | 1 | 1e-05 | 0.0 | FAIL | WF1 |
| W0534 | 0.01 | 999.99 | True | 2 | 0 | 1.0E-5 | FAIL | WF1 |
| W0535 | 0.01 | 999.99 | True | 3 | 0 | 1.0E-5 | FAIL | WF1 |
| W0536 | 0.01 | 999.99 | True | 4 | 0 | 1.0E-5 | FAIL | WF1 |
| W0537 | 0.01 | 999.99 | True | 5 | 0 | 1.0E-5 | FAIL | WF1 |
| W0538 | 0.01 | 999.99 | True | 6 | 0 | 1.0E-5 | FAIL | WF1 |
| W0539 | 0.01 | 999.99 | True | 7 | 1e-05 | 0.0 | FAIL | WF1 |
| W0542 | 0.01 | 1000 | False | 1 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0543 | 0.01 | 1000 | False | 2 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0544 | 0.01 | 1000 | False | 3 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0545 | 0.01 | 1000 | False | 4 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0546 | 0.01 | 1000 | False | 5 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0547 | 0.01 | 1000 | False | 6 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0548 | 0.01 | 1000 | False | 7 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0551 | 0.01 | 1000 | True | 1 | 1e-05 | 0.0 | FAIL | WF1 |
| W0552 | 0.01 | 1000 | True | 2 | 0 | 1.0E-5 | FAIL | WF1 |
| W0553 | 0.01 | 1000 | True | 3 | 0 | 1.0E-5 | FAIL | WF1 |
| W0554 | 0.01 | 1000 | True | 4 | 0 | 1.0E-5 | FAIL | WF1 |
| W0555 | 0.01 | 1000 | True | 5 | 0 | 1.0E-5 | FAIL | WF1 |
| W0556 | 0.01 | 1000 | True | 6 | 0 | 1.0E-5 | FAIL | WF1 |
| W0557 | 0.01 | 1000 | True | 7 | 1e-05 | 0.0 | FAIL | WF1 |
| W0560 | 0.01 | 1000.01 | False | 1 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0561 | 0.01 | 1000.01 | False | 2 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0562 | 0.01 | 1000.01 | False | 3 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0563 | 0.01 | 1000.01 | False | 4 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0564 | 0.01 | 1000.01 | False | 5 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0565 | 0.01 | 1000.01 | False | 6 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0566 | 0.01 | 1000.01 | False | 7 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0569 | 0.01 | 1000.01 | True | 1 | 1e-05 | 0.0 | FAIL | WF1 |
| W0570 | 0.01 | 1000.01 | True | 2 | 0 | 1.0E-5 | FAIL | WF1 |
| W0571 | 0.01 | 1000.01 | True | 3 | 0 | 1.0E-5 | FAIL | WF1 |
| W0572 | 0.01 | 1000.01 | True | 4 | 0 | 1.0E-5 | FAIL | WF1 |
| W0573 | 0.01 | 1000.01 | True | 5 | 0 | 1.0E-5 | FAIL | WF1 |
| W0574 | 0.01 | 1000.01 | True | 6 | 0 | 1.0E-5 | FAIL | WF1 |
| W0575 | 0.01 | 1000.01 | True | 7 | 1e-05 | 0.0 | FAIL | WF1 |
| W0578 | 0.01 | 4999.99 | False | 1 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0579 | 0.01 | 4999.99 | False | 2 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0580 | 0.01 | 4999.99 | False | 3 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0581 | 0.01 | 4999.99 | False | 4 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0582 | 0.01 | 4999.99 | False | 5 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0583 | 0.01 | 4999.99 | False | 6 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0584 | 0.01 | 4999.99 | False | 7 | 1e-05 | Within 1e-9 of expected | PASS | — |
| W0587 | 0.01 | 4999.99 | True | 1 | 1e-05 | 0.0 | FAIL | WF1 |
| W0588 | 0.01 | 4999.99 | True | 2 | 0 | 1.0E-5 | FAIL | WF1 |
| W0589 | 0.01 | 4999.99 | True | 3 | 0 | 1.0E-5 | FAIL | WF1 |
| W0590 | 0.01 | 4999.99 | True | 4 | 0 | 1.0E-5 | FAIL | WF1 |
| W0591 | 0.01 | 4999.99 | True | 5 | 0 | 1.0E-5 | FAIL | WF1 |
| W0592 | 0.01 | 4999.99 | True | 6 | 0 | 1.0E-5 | FAIL | WF1 |
| W0593 | 0.01 | 4999.99 | True | 7 | 1e-05 | 0.0 | FAIL | WF1 |
| W0596 | 0.01 | 5000 | False | 1 | 0 | 1.0E-5 | FAIL | WF3 |
| W0597 | 0.01 | 5000 | False | 2 | 0 | 1.0E-5 | FAIL | WF3 |
| W0598 | 0.01 | 5000 | False | 3 | 0 | 1.0E-5 | FAIL | WF3 |
| W0599 | 0.01 | 5000 | False | 4 | 0 | 1.0E-5 | FAIL | WF3 |
| W0600 | 0.01 | 5000 | False | 5 | 0 | 1.0E-5 | FAIL | WF3 |
| W0601 | 0.01 | 5000 | False | 6 | 0 | 1.0E-5 | FAIL | WF3 |
| W0602 | 0.01 | 5000 | False | 7 | 0 | 1.0E-5 | FAIL | WF3 |
| W0605 | 0.01 | 5000 | True | 1 | 1e-05 | 0.0 | FAIL | WF1 |
| W0606 | 0.01 | 5000 | True | 2 | 0 | 1.0E-5 | FAIL | WF1 |
| W0607 | 0.01 | 5000 | True | 3 | 0 | 1.0E-5 | FAIL | WF1 |
| W0608 | 0.01 | 5000 | True | 4 | 0 | 1.0E-5 | FAIL | WF1 |
| W0609 | 0.01 | 5000 | True | 5 | 0 | 1.0E-5 | FAIL | WF1 |
| W0610 | 0.01 | 5000 | True | 6 | 0 | 1.0E-5 | FAIL | WF1 |
| W0611 | 0.01 | 5000 | True | 7 | 1e-05 | 0.0 | FAIL | WF1 |
| W0614 | 0.01 | 5000.01 | False | 1 | 0 | 1.0E-5 | FAIL | WF3 |
| W0615 | 0.01 | 5000.01 | False | 2 | 0 | 1.0E-5 | FAIL | WF3 |
| W0616 | 0.01 | 5000.01 | False | 3 | 0 | 1.0E-5 | FAIL | WF3 |
| W0617 | 0.01 | 5000.01 | False | 4 | 0 | 1.0E-5 | FAIL | WF3 |
| W0618 | 0.01 | 5000.01 | False | 5 | 0 | 1.0E-5 | FAIL | WF3 |
| W0619 | 0.01 | 5000.01 | False | 6 | 0 | 1.0E-5 | FAIL | WF3 |
| W0620 | 0.01 | 5000.01 | False | 7 | 0 | 1.0E-5 | FAIL | WF3 |
| W0623 | 0.01 | 5000.01 | True | 1 | 1e-05 | 0.0 | FAIL | WF1 |
| W0624 | 0.01 | 5000.01 | True | 2 | 0 | 1.0E-5 | FAIL | WF1 |
| W0625 | 0.01 | 5000.01 | True | 3 | 0 | 1.0E-5 | FAIL | WF1 |
| W0626 | 0.01 | 5000.01 | True | 4 | 0 | 1.0E-5 | FAIL | WF1 |
| W0627 | 0.01 | 5000.01 | True | 5 | 0 | 1.0E-5 | FAIL | WF1 |
| W0628 | 0.01 | 5000.01 | True | 6 | 0 | 1.0E-5 | FAIL | WF1 |
| W0629 | 0.01 | 5000.01 | True | 7 | 1e-05 | 0.0 | FAIL | WF1 |
| W0632 | 0.01 | 10000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0633 | 0.01 | 10000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0634 | 0.01 | 10000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0635 | 0.01 | 10000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0636 | 0.01 | 10000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0637 | 0.01 | 10000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0638 | 0.01 | 10000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0641 | 0.01 | 10000 | True | 1 | 1e-05 | 0.0 | FAIL | WF1 |
| W0642 | 0.01 | 10000 | True | 2 | 0 | 1.0E-5 | FAIL | WF1 |
| W0643 | 0.01 | 10000 | True | 3 | 0 | 1.0E-5 | FAIL | WF1 |
| W0644 | 0.01 | 10000 | True | 4 | 0 | 1.0E-5 | FAIL | WF1 |
| W0645 | 0.01 | 10000 | True | 5 | 0 | 1.0E-5 | FAIL | WF1 |
| W0646 | 0.01 | 10000 | True | 6 | 0 | 1.0E-5 | FAIL | WF1 |
| W0647 | 0.01 | 10000 | True | 7 | 1e-05 | 0.0 | FAIL | WF1 |
| W0650 | 0.01 | 19999.99 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0651 | 0.01 | 19999.99 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0652 | 0.01 | 19999.99 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0653 | 0.01 | 19999.99 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0654 | 0.01 | 19999.99 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0655 | 0.01 | 19999.99 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0656 | 0.01 | 19999.99 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0659 | 0.01 | 19999.99 | True | 1 | 1e-05 | 0.0 | FAIL | WF1 |
| W0660 | 0.01 | 19999.99 | True | 2 | 0 | 1.0E-5 | FAIL | WF1 |
| W0661 | 0.01 | 19999.99 | True | 3 | 0 | 1.0E-5 | FAIL | WF1 |
| W0662 | 0.01 | 19999.99 | True | 4 | 0 | 1.0E-5 | FAIL | WF1 |
| W0663 | 0.01 | 19999.99 | True | 5 | 0 | 1.0E-5 | FAIL | WF1 |
| W0664 | 0.01 | 19999.99 | True | 6 | 0 | 1.0E-5 | FAIL | WF1 |
| W0665 | 0.01 | 19999.99 | True | 7 | 1e-05 | 0.0 | FAIL | WF1 |
| W0668 | 0.01 | 20000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0669 | 0.01 | 20000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0670 | 0.01 | 20000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0671 | 0.01 | 20000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0672 | 0.01 | 20000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0673 | 0.01 | 20000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0674 | 0.01 | 20000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0677 | 0.01 | 20000 | True | 1 | 1e-05 | 0.0 | FAIL | WF1 |
| W0678 | 0.01 | 20000 | True | 2 | 0 | 1.0E-5 | FAIL | WF1 |
| W0679 | 0.01 | 20000 | True | 3 | 0 | 1.0E-5 | FAIL | WF1 |
| W0680 | 0.01 | 20000 | True | 4 | 0 | 1.0E-5 | FAIL | WF1 |
| W0681 | 0.01 | 20000 | True | 5 | 0 | 1.0E-5 | FAIL | WF1 |
| W0682 | 0.01 | 20000 | True | 6 | 0 | 1.0E-5 | FAIL | WF1 |
| W0683 | 0.01 | 20000 | True | 7 | 1e-05 | 0.0 | FAIL | WF1 |
| W0686 | 0.01 | 20000.01 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0687 | 0.01 | 20000.01 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0688 | 0.01 | 20000.01 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0689 | 0.01 | 20000.01 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0690 | 0.01 | 20000.01 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0691 | 0.01 | 20000.01 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0692 | 0.01 | 20000.01 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0695 | 0.01 | 20000.01 | True | 1 | 1e-05 | 0.0 | FAIL | WF1 |
| W0696 | 0.01 | 20000.01 | True | 2 | 0 | 1.0E-5 | FAIL | WF1 |
| W0697 | 0.01 | 20000.01 | True | 3 | 0 | 1.0E-5 | FAIL | WF1 |
| W0698 | 0.01 | 20000.01 | True | 4 | 0 | 1.0E-5 | FAIL | WF1 |
| W0699 | 0.01 | 20000.01 | True | 5 | 0 | 1.0E-5 | FAIL | WF1 |
| W0700 | 0.01 | 20000.01 | True | 6 | 0 | 1.0E-5 | FAIL | WF1 |
| W0701 | 0.01 | 20000.01 | True | 7 | 1e-05 | 0.0 | FAIL | WF1 |
| W0722 | 5000 | 0 | False | 1 | 15 | 10.0 | FAIL | WF2 |
| W0723 | 5000 | 0 | False | 2 | 15 | 10.0 | FAIL | WF2 |
| W0724 | 5000 | 0 | False | 3 | 15 | 10.0 | FAIL | WF2 |
| W0725 | 5000 | 0 | False | 4 | 15 | 10.0 | FAIL | WF2 |
| W0726 | 5000 | 0 | False | 5 | 15 | 10.0 | FAIL | WF2 |
| W0727 | 5000 | 0 | False | 6 | 15 | 10.0 | FAIL | WF2 |
| W0728 | 5000 | 0 | False | 7 | 15 | 10.0 | FAIL | WF2 |
| W0731 | 5000 | 0 | True | 1 | 5 | 0.0 | FAIL | WF1 |
| W0732 | 5000 | 0 | True | 2 | 0 | 5.0 | FAIL | WF1 |
| W0733 | 5000 | 0 | True | 3 | 0 | 5.0 | FAIL | WF1 |
| W0734 | 5000 | 0 | True | 4 | 0 | 5.0 | FAIL | WF1 |
| W0735 | 5000 | 0 | True | 5 | 0 | 5.0 | FAIL | WF1 |
| W0736 | 5000 | 0 | True | 6 | 0 | 5.0 | FAIL | WF1 |
| W0737 | 5000 | 0 | True | 7 | 5 | 0.0 | FAIL | WF1 |
| W0740 | 5000 | 0.01 | False | 1 | 15 | 10.0 | FAIL | WF2 |
| W0741 | 5000 | 0.01 | False | 2 | 15 | 10.0 | FAIL | WF2 |
| W0742 | 5000 | 0.01 | False | 3 | 15 | 10.0 | FAIL | WF2 |
| W0743 | 5000 | 0.01 | False | 4 | 15 | 10.0 | FAIL | WF2 |
| W0744 | 5000 | 0.01 | False | 5 | 15 | 10.0 | FAIL | WF2 |
| W0745 | 5000 | 0.01 | False | 6 | 15 | 10.0 | FAIL | WF2 |
| W0746 | 5000 | 0.01 | False | 7 | 15 | 10.0 | FAIL | WF2 |
| W0749 | 5000 | 0.01 | True | 1 | 5 | 0.0 | FAIL | WF1 |
| W0750 | 5000 | 0.01 | True | 2 | 0 | 5.0 | FAIL | WF1 |
| W0751 | 5000 | 0.01 | True | 3 | 0 | 5.0 | FAIL | WF1 |
| W0752 | 5000 | 0.01 | True | 4 | 0 | 5.0 | FAIL | WF1 |
| W0753 | 5000 | 0.01 | True | 5 | 0 | 5.0 | FAIL | WF1 |
| W0754 | 5000 | 0.01 | True | 6 | 0 | 5.0 | FAIL | WF1 |
| W0755 | 5000 | 0.01 | True | 7 | 5 | 0.0 | FAIL | WF1 |
| W0758 | 5000 | 999.99 | False | 1 | 15 | 10.0 | FAIL | WF2 |
| W0759 | 5000 | 999.99 | False | 2 | 15 | 10.0 | FAIL | WF2 |
| W0760 | 5000 | 999.99 | False | 3 | 15 | 10.0 | FAIL | WF2 |
| W0761 | 5000 | 999.99 | False | 4 | 15 | 10.0 | FAIL | WF2 |
| W0762 | 5000 | 999.99 | False | 5 | 15 | 10.0 | FAIL | WF2 |
| W0763 | 5000 | 999.99 | False | 6 | 15 | 10.0 | FAIL | WF2 |
| W0764 | 5000 | 999.99 | False | 7 | 15 | 10.0 | FAIL | WF2 |
| W0767 | 5000 | 999.99 | True | 1 | 5 | 0.0 | FAIL | WF1 |
| W0768 | 5000 | 999.99 | True | 2 | 0 | 5.0 | FAIL | WF1 |
| W0769 | 5000 | 999.99 | True | 3 | 0 | 5.0 | FAIL | WF1 |
| W0770 | 5000 | 999.99 | True | 4 | 0 | 5.0 | FAIL | WF1 |
| W0771 | 5000 | 999.99 | True | 5 | 0 | 5.0 | FAIL | WF1 |
| W0772 | 5000 | 999.99 | True | 6 | 0 | 5.0 | FAIL | WF1 |
| W0773 | 5000 | 999.99 | True | 7 | 5 | 0.0 | FAIL | WF1 |
| W0776 | 5000 | 1000 | False | 1 | 5 | Within 1e-9 of expected | PASS | — |
| W0777 | 5000 | 1000 | False | 2 | 5 | Within 1e-9 of expected | PASS | — |
| W0778 | 5000 | 1000 | False | 3 | 5 | Within 1e-9 of expected | PASS | — |
| W0779 | 5000 | 1000 | False | 4 | 5 | Within 1e-9 of expected | PASS | — |
| W0780 | 5000 | 1000 | False | 5 | 5 | Within 1e-9 of expected | PASS | — |
| W0781 | 5000 | 1000 | False | 6 | 5 | Within 1e-9 of expected | PASS | — |
| W0782 | 5000 | 1000 | False | 7 | 5 | Within 1e-9 of expected | PASS | — |
| W0785 | 5000 | 1000 | True | 1 | 5 | 0.0 | FAIL | WF1 |
| W0786 | 5000 | 1000 | True | 2 | 0 | 5.0 | FAIL | WF1 |
| W0787 | 5000 | 1000 | True | 3 | 0 | 5.0 | FAIL | WF1 |
| W0788 | 5000 | 1000 | True | 4 | 0 | 5.0 | FAIL | WF1 |
| W0789 | 5000 | 1000 | True | 5 | 0 | 5.0 | FAIL | WF1 |
| W0790 | 5000 | 1000 | True | 6 | 0 | 5.0 | FAIL | WF1 |
| W0791 | 5000 | 1000 | True | 7 | 5 | 0.0 | FAIL | WF1 |
| W0794 | 5000 | 1000.01 | False | 1 | 5 | Within 1e-9 of expected | PASS | — |
| W0795 | 5000 | 1000.01 | False | 2 | 5 | Within 1e-9 of expected | PASS | — |
| W0796 | 5000 | 1000.01 | False | 3 | 5 | Within 1e-9 of expected | PASS | — |
| W0797 | 5000 | 1000.01 | False | 4 | 5 | Within 1e-9 of expected | PASS | — |
| W0798 | 5000 | 1000.01 | False | 5 | 5 | Within 1e-9 of expected | PASS | — |
| W0799 | 5000 | 1000.01 | False | 6 | 5 | Within 1e-9 of expected | PASS | — |
| W0800 | 5000 | 1000.01 | False | 7 | 5 | Within 1e-9 of expected | PASS | — |
| W0803 | 5000 | 1000.01 | True | 1 | 5 | 0.0 | FAIL | WF1 |
| W0804 | 5000 | 1000.01 | True | 2 | 0 | 5.0 | FAIL | WF1 |
| W0805 | 5000 | 1000.01 | True | 3 | 0 | 5.0 | FAIL | WF1 |
| W0806 | 5000 | 1000.01 | True | 4 | 0 | 5.0 | FAIL | WF1 |
| W0807 | 5000 | 1000.01 | True | 5 | 0 | 5.0 | FAIL | WF1 |
| W0808 | 5000 | 1000.01 | True | 6 | 0 | 5.0 | FAIL | WF1 |
| W0809 | 5000 | 1000.01 | True | 7 | 5 | 0.0 | FAIL | WF1 |
| W0812 | 5000 | 4999.99 | False | 1 | 5 | Within 1e-9 of expected | PASS | — |
| W0813 | 5000 | 4999.99 | False | 2 | 5 | Within 1e-9 of expected | PASS | — |
| W0814 | 5000 | 4999.99 | False | 3 | 5 | Within 1e-9 of expected | PASS | — |
| W0815 | 5000 | 4999.99 | False | 4 | 5 | Within 1e-9 of expected | PASS | — |
| W0816 | 5000 | 4999.99 | False | 5 | 5 | Within 1e-9 of expected | PASS | — |
| W0817 | 5000 | 4999.99 | False | 6 | 5 | Within 1e-9 of expected | PASS | — |
| W0818 | 5000 | 4999.99 | False | 7 | 5 | Within 1e-9 of expected | PASS | — |
| W0821 | 5000 | 4999.99 | True | 1 | 5 | 0.0 | FAIL | WF1 |
| W0822 | 5000 | 4999.99 | True | 2 | 0 | 5.0 | FAIL | WF1 |
| W0823 | 5000 | 4999.99 | True | 3 | 0 | 5.0 | FAIL | WF1 |
| W0824 | 5000 | 4999.99 | True | 4 | 0 | 5.0 | FAIL | WF1 |
| W0825 | 5000 | 4999.99 | True | 5 | 0 | 5.0 | FAIL | WF1 |
| W0826 | 5000 | 4999.99 | True | 6 | 0 | 5.0 | FAIL | WF1 |
| W0827 | 5000 | 4999.99 | True | 7 | 5 | 0.0 | FAIL | WF1 |
| W0830 | 5000 | 5000 | False | 1 | 0 | 5.0 | FAIL | WF3 |
| W0831 | 5000 | 5000 | False | 2 | 0 | 5.0 | FAIL | WF3 |
| W0832 | 5000 | 5000 | False | 3 | 0 | 5.0 | FAIL | WF3 |
| W0833 | 5000 | 5000 | False | 4 | 0 | 5.0 | FAIL | WF3 |
| W0834 | 5000 | 5000 | False | 5 | 0 | 5.0 | FAIL | WF3 |
| W0835 | 5000 | 5000 | False | 6 | 0 | 5.0 | FAIL | WF3 |
| W0836 | 5000 | 5000 | False | 7 | 0 | 5.0 | FAIL | WF3 |
| W0839 | 5000 | 5000 | True | 1 | 5 | 0.0 | FAIL | WF1 |
| W0840 | 5000 | 5000 | True | 2 | 0 | 5.0 | FAIL | WF1 |
| W0841 | 5000 | 5000 | True | 3 | 0 | 5.0 | FAIL | WF1 |
| W0842 | 5000 | 5000 | True | 4 | 0 | 5.0 | FAIL | WF1 |
| W0843 | 5000 | 5000 | True | 5 | 0 | 5.0 | FAIL | WF1 |
| W0844 | 5000 | 5000 | True | 6 | 0 | 5.0 | FAIL | WF1 |
| W0845 | 5000 | 5000 | True | 7 | 5 | 0.0 | FAIL | WF1 |
| W0848 | 5000 | 5000.01 | False | 1 | 0 | 5.0 | FAIL | WF3 |
| W0849 | 5000 | 5000.01 | False | 2 | 0 | 5.0 | FAIL | WF3 |
| W0850 | 5000 | 5000.01 | False | 3 | 0 | 5.0 | FAIL | WF3 |
| W0851 | 5000 | 5000.01 | False | 4 | 0 | 5.0 | FAIL | WF3 |
| W0852 | 5000 | 5000.01 | False | 5 | 0 | 5.0 | FAIL | WF3 |
| W0853 | 5000 | 5000.01 | False | 6 | 0 | 5.0 | FAIL | WF3 |
| W0854 | 5000 | 5000.01 | False | 7 | 0 | 5.0 | FAIL | WF3 |
| W0857 | 5000 | 5000.01 | True | 1 | 5 | 0.0 | FAIL | WF1 |
| W0858 | 5000 | 5000.01 | True | 2 | 0 | 5.0 | FAIL | WF1 |
| W0859 | 5000 | 5000.01 | True | 3 | 0 | 5.0 | FAIL | WF1 |
| W0860 | 5000 | 5000.01 | True | 4 | 0 | 5.0 | FAIL | WF1 |
| W0861 | 5000 | 5000.01 | True | 5 | 0 | 5.0 | FAIL | WF1 |
| W0862 | 5000 | 5000.01 | True | 6 | 0 | 5.0 | FAIL | WF1 |
| W0863 | 5000 | 5000.01 | True | 7 | 5 | 0.0 | FAIL | WF1 |
| W0866 | 5000 | 10000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0867 | 5000 | 10000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0868 | 5000 | 10000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0869 | 5000 | 10000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0870 | 5000 | 10000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0871 | 5000 | 10000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0872 | 5000 | 10000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0875 | 5000 | 10000 | True | 1 | 5 | 0.0 | FAIL | WF1 |
| W0876 | 5000 | 10000 | True | 2 | 0 | 5.0 | FAIL | WF1 |
| W0877 | 5000 | 10000 | True | 3 | 0 | 5.0 | FAIL | WF1 |
| W0878 | 5000 | 10000 | True | 4 | 0 | 5.0 | FAIL | WF1 |
| W0879 | 5000 | 10000 | True | 5 | 0 | 5.0 | FAIL | WF1 |
| W0880 | 5000 | 10000 | True | 6 | 0 | 5.0 | FAIL | WF1 |
| W0881 | 5000 | 10000 | True | 7 | 5 | 0.0 | FAIL | WF1 |
| W0884 | 5000 | 19999.99 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0885 | 5000 | 19999.99 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0886 | 5000 | 19999.99 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0887 | 5000 | 19999.99 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0888 | 5000 | 19999.99 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0889 | 5000 | 19999.99 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0890 | 5000 | 19999.99 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0893 | 5000 | 19999.99 | True | 1 | 5 | 0.0 | FAIL | WF1 |
| W0894 | 5000 | 19999.99 | True | 2 | 0 | 5.0 | FAIL | WF1 |
| W0895 | 5000 | 19999.99 | True | 3 | 0 | 5.0 | FAIL | WF1 |
| W0896 | 5000 | 19999.99 | True | 4 | 0 | 5.0 | FAIL | WF1 |
| W0897 | 5000 | 19999.99 | True | 5 | 0 | 5.0 | FAIL | WF1 |
| W0898 | 5000 | 19999.99 | True | 6 | 0 | 5.0 | FAIL | WF1 |
| W0899 | 5000 | 19999.99 | True | 7 | 5 | 0.0 | FAIL | WF1 |
| W0902 | 5000 | 20000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0903 | 5000 | 20000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0904 | 5000 | 20000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0905 | 5000 | 20000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0906 | 5000 | 20000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0907 | 5000 | 20000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0908 | 5000 | 20000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0911 | 5000 | 20000 | True | 1 | 5 | 0.0 | FAIL | WF1 |
| W0912 | 5000 | 20000 | True | 2 | 0 | 5.0 | FAIL | WF1 |
| W0913 | 5000 | 20000 | True | 3 | 0 | 5.0 | FAIL | WF1 |
| W0914 | 5000 | 20000 | True | 4 | 0 | 5.0 | FAIL | WF1 |
| W0915 | 5000 | 20000 | True | 5 | 0 | 5.0 | FAIL | WF1 |
| W0916 | 5000 | 20000 | True | 6 | 0 | 5.0 | FAIL | WF1 |
| W0917 | 5000 | 20000 | True | 7 | 5 | 0.0 | FAIL | WF1 |
| W0920 | 5000 | 20000.01 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W0921 | 5000 | 20000.01 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W0922 | 5000 | 20000.01 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W0923 | 5000 | 20000.01 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W0924 | 5000 | 20000.01 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W0925 | 5000 | 20000.01 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W0926 | 5000 | 20000.01 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W0929 | 5000 | 20000.01 | True | 1 | 5 | 0.0 | FAIL | WF1 |
| W0930 | 5000 | 20000.01 | True | 2 | 0 | 5.0 | FAIL | WF1 |
| W0931 | 5000 | 20000.01 | True | 3 | 0 | 5.0 | FAIL | WF1 |
| W0932 | 5000 | 20000.01 | True | 4 | 0 | 5.0 | FAIL | WF1 |
| W0933 | 5000 | 20000.01 | True | 5 | 0 | 5.0 | FAIL | WF1 |
| W0934 | 5000 | 20000.01 | True | 6 | 0 | 5.0 | FAIL | WF1 |
| W0935 | 5000 | 20000.01 | True | 7 | 5 | 0.0 | FAIL | WF1 |
| W0956 | 9999.99 | 0 | False | 1 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0957 | 9999.99 | 0 | False | 2 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0958 | 9999.99 | 0 | False | 3 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0959 | 9999.99 | 0 | False | 4 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0960 | 9999.99 | 0 | False | 5 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0961 | 9999.99 | 0 | False | 6 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0962 | 9999.99 | 0 | False | 7 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0965 | 9999.99 | 0 | True | 1 | 9.99999 | 0.0 | FAIL | WF1 |
| W0966 | 9999.99 | 0 | True | 2 | 0 | 9.99999 | FAIL | WF1 |
| W0967 | 9999.99 | 0 | True | 3 | 0 | 9.99999 | FAIL | WF1 |
| W0968 | 9999.99 | 0 | True | 4 | 0 | 9.99999 | FAIL | WF1 |
| W0969 | 9999.99 | 0 | True | 5 | 0 | 9.99999 | FAIL | WF1 |
| W0970 | 9999.99 | 0 | True | 6 | 0 | 9.99999 | FAIL | WF1 |
| W0971 | 9999.99 | 0 | True | 7 | 9.99999 | 0.0 | FAIL | WF1 |
| W0974 | 9999.99 | 0.01 | False | 1 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0975 | 9999.99 | 0.01 | False | 2 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0976 | 9999.99 | 0.01 | False | 3 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0977 | 9999.99 | 0.01 | False | 4 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0978 | 9999.99 | 0.01 | False | 5 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0979 | 9999.99 | 0.01 | False | 6 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0980 | 9999.99 | 0.01 | False | 7 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0983 | 9999.99 | 0.01 | True | 1 | 9.99999 | 0.0 | FAIL | WF1 |
| W0984 | 9999.99 | 0.01 | True | 2 | 0 | 9.99999 | FAIL | WF1 |
| W0985 | 9999.99 | 0.01 | True | 3 | 0 | 9.99999 | FAIL | WF1 |
| W0986 | 9999.99 | 0.01 | True | 4 | 0 | 9.99999 | FAIL | WF1 |
| W0987 | 9999.99 | 0.01 | True | 5 | 0 | 9.99999 | FAIL | WF1 |
| W0988 | 9999.99 | 0.01 | True | 6 | 0 | 9.99999 | FAIL | WF1 |
| W0989 | 9999.99 | 0.01 | True | 7 | 9.99999 | 0.0 | FAIL | WF1 |
| W0992 | 9999.99 | 999.99 | False | 1 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0993 | 9999.99 | 999.99 | False | 2 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0994 | 9999.99 | 999.99 | False | 3 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0995 | 9999.99 | 999.99 | False | 4 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0996 | 9999.99 | 999.99 | False | 5 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0997 | 9999.99 | 999.99 | False | 6 | 29.99997 | 19.99998 | FAIL | WF2 |
| W0998 | 9999.99 | 999.99 | False | 7 | 29.99997 | 19.99998 | FAIL | WF2 |
| W1001 | 9999.99 | 999.99 | True | 1 | 9.99999 | 0.0 | FAIL | WF1 |
| W1002 | 9999.99 | 999.99 | True | 2 | 0 | 9.99999 | FAIL | WF1 |
| W1003 | 9999.99 | 999.99 | True | 3 | 0 | 9.99999 | FAIL | WF1 |
| W1004 | 9999.99 | 999.99 | True | 4 | 0 | 9.99999 | FAIL | WF1 |
| W1005 | 9999.99 | 999.99 | True | 5 | 0 | 9.99999 | FAIL | WF1 |
| W1006 | 9999.99 | 999.99 | True | 6 | 0 | 9.99999 | FAIL | WF1 |
| W1007 | 9999.99 | 999.99 | True | 7 | 9.99999 | 0.0 | FAIL | WF1 |
| W1010 | 9999.99 | 1000 | False | 1 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1011 | 9999.99 | 1000 | False | 2 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1012 | 9999.99 | 1000 | False | 3 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1013 | 9999.99 | 1000 | False | 4 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1014 | 9999.99 | 1000 | False | 5 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1015 | 9999.99 | 1000 | False | 6 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1016 | 9999.99 | 1000 | False | 7 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1019 | 9999.99 | 1000 | True | 1 | 9.99999 | 0.0 | FAIL | WF1 |
| W1020 | 9999.99 | 1000 | True | 2 | 0 | 9.99999 | FAIL | WF1 |
| W1021 | 9999.99 | 1000 | True | 3 | 0 | 9.99999 | FAIL | WF1 |
| W1022 | 9999.99 | 1000 | True | 4 | 0 | 9.99999 | FAIL | WF1 |
| W1023 | 9999.99 | 1000 | True | 5 | 0 | 9.99999 | FAIL | WF1 |
| W1024 | 9999.99 | 1000 | True | 6 | 0 | 9.99999 | FAIL | WF1 |
| W1025 | 9999.99 | 1000 | True | 7 | 9.99999 | 0.0 | FAIL | WF1 |
| W1028 | 9999.99 | 1000.01 | False | 1 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1029 | 9999.99 | 1000.01 | False | 2 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1030 | 9999.99 | 1000.01 | False | 3 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1031 | 9999.99 | 1000.01 | False | 4 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1032 | 9999.99 | 1000.01 | False | 5 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1033 | 9999.99 | 1000.01 | False | 6 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1034 | 9999.99 | 1000.01 | False | 7 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1037 | 9999.99 | 1000.01 | True | 1 | 9.99999 | 0.0 | FAIL | WF1 |
| W1038 | 9999.99 | 1000.01 | True | 2 | 0 | 9.99999 | FAIL | WF1 |
| W1039 | 9999.99 | 1000.01 | True | 3 | 0 | 9.99999 | FAIL | WF1 |
| W1040 | 9999.99 | 1000.01 | True | 4 | 0 | 9.99999 | FAIL | WF1 |
| W1041 | 9999.99 | 1000.01 | True | 5 | 0 | 9.99999 | FAIL | WF1 |
| W1042 | 9999.99 | 1000.01 | True | 6 | 0 | 9.99999 | FAIL | WF1 |
| W1043 | 9999.99 | 1000.01 | True | 7 | 9.99999 | 0.0 | FAIL | WF1 |
| W1046 | 9999.99 | 4999.99 | False | 1 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1047 | 9999.99 | 4999.99 | False | 2 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1048 | 9999.99 | 4999.99 | False | 3 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1049 | 9999.99 | 4999.99 | False | 4 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1050 | 9999.99 | 4999.99 | False | 5 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1051 | 9999.99 | 4999.99 | False | 6 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1052 | 9999.99 | 4999.99 | False | 7 | 9.99999 | Within 1e-9 of expected | PASS | — |
| W1055 | 9999.99 | 4999.99 | True | 1 | 9.99999 | 0.0 | FAIL | WF1 |
| W1056 | 9999.99 | 4999.99 | True | 2 | 0 | 9.99999 | FAIL | WF1 |
| W1057 | 9999.99 | 4999.99 | True | 3 | 0 | 9.99999 | FAIL | WF1 |
| W1058 | 9999.99 | 4999.99 | True | 4 | 0 | 9.99999 | FAIL | WF1 |
| W1059 | 9999.99 | 4999.99 | True | 5 | 0 | 9.99999 | FAIL | WF1 |
| W1060 | 9999.99 | 4999.99 | True | 6 | 0 | 9.99999 | FAIL | WF1 |
| W1061 | 9999.99 | 4999.99 | True | 7 | 9.99999 | 0.0 | FAIL | WF1 |
| W1064 | 9999.99 | 5000 | False | 1 | 0 | 9.99999 | FAIL | WF3 |
| W1065 | 9999.99 | 5000 | False | 2 | 0 | 9.99999 | FAIL | WF3 |
| W1066 | 9999.99 | 5000 | False | 3 | 0 | 9.99999 | FAIL | WF3 |
| W1067 | 9999.99 | 5000 | False | 4 | 0 | 9.99999 | FAIL | WF3 |
| W1068 | 9999.99 | 5000 | False | 5 | 0 | 9.99999 | FAIL | WF3 |
| W1069 | 9999.99 | 5000 | False | 6 | 0 | 9.99999 | FAIL | WF3 |
| W1070 | 9999.99 | 5000 | False | 7 | 0 | 9.99999 | FAIL | WF3 |
| W1073 | 9999.99 | 5000 | True | 1 | 9.99999 | 0.0 | FAIL | WF1 |
| W1074 | 9999.99 | 5000 | True | 2 | 0 | 9.99999 | FAIL | WF1 |
| W1075 | 9999.99 | 5000 | True | 3 | 0 | 9.99999 | FAIL | WF1 |
| W1076 | 9999.99 | 5000 | True | 4 | 0 | 9.99999 | FAIL | WF1 |
| W1077 | 9999.99 | 5000 | True | 5 | 0 | 9.99999 | FAIL | WF1 |
| W1078 | 9999.99 | 5000 | True | 6 | 0 | 9.99999 | FAIL | WF1 |
| W1079 | 9999.99 | 5000 | True | 7 | 9.99999 | 0.0 | FAIL | WF1 |
| W1082 | 9999.99 | 5000.01 | False | 1 | 0 | 9.99999 | FAIL | WF3 |
| W1083 | 9999.99 | 5000.01 | False | 2 | 0 | 9.99999 | FAIL | WF3 |
| W1084 | 9999.99 | 5000.01 | False | 3 | 0 | 9.99999 | FAIL | WF3 |
| W1085 | 9999.99 | 5000.01 | False | 4 | 0 | 9.99999 | FAIL | WF3 |
| W1086 | 9999.99 | 5000.01 | False | 5 | 0 | 9.99999 | FAIL | WF3 |
| W1087 | 9999.99 | 5000.01 | False | 6 | 0 | 9.99999 | FAIL | WF3 |
| W1088 | 9999.99 | 5000.01 | False | 7 | 0 | 9.99999 | FAIL | WF3 |
| W1091 | 9999.99 | 5000.01 | True | 1 | 9.99999 | 0.0 | FAIL | WF1 |
| W1092 | 9999.99 | 5000.01 | True | 2 | 0 | 9.99999 | FAIL | WF1 |
| W1093 | 9999.99 | 5000.01 | True | 3 | 0 | 9.99999 | FAIL | WF1 |
| W1094 | 9999.99 | 5000.01 | True | 4 | 0 | 9.99999 | FAIL | WF1 |
| W1095 | 9999.99 | 5000.01 | True | 5 | 0 | 9.99999 | FAIL | WF1 |
| W1096 | 9999.99 | 5000.01 | True | 6 | 0 | 9.99999 | FAIL | WF1 |
| W1097 | 9999.99 | 5000.01 | True | 7 | 9.99999 | 0.0 | FAIL | WF1 |
| W1100 | 9999.99 | 10000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W1101 | 9999.99 | 10000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W1102 | 9999.99 | 10000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W1103 | 9999.99 | 10000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W1104 | 9999.99 | 10000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W1105 | 9999.99 | 10000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W1106 | 9999.99 | 10000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W1109 | 9999.99 | 10000 | True | 1 | 9.99999 | 0.0 | FAIL | WF1 |
| W1110 | 9999.99 | 10000 | True | 2 | 0 | 9.99999 | FAIL | WF1 |
| W1111 | 9999.99 | 10000 | True | 3 | 0 | 9.99999 | FAIL | WF1 |
| W1112 | 9999.99 | 10000 | True | 4 | 0 | 9.99999 | FAIL | WF1 |
| W1113 | 9999.99 | 10000 | True | 5 | 0 | 9.99999 | FAIL | WF1 |
| W1114 | 9999.99 | 10000 | True | 6 | 0 | 9.99999 | FAIL | WF1 |
| W1115 | 9999.99 | 10000 | True | 7 | 9.99999 | 0.0 | FAIL | WF1 |
| W1118 | 9999.99 | 19999.99 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W1119 | 9999.99 | 19999.99 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W1120 | 9999.99 | 19999.99 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W1121 | 9999.99 | 19999.99 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W1122 | 9999.99 | 19999.99 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W1123 | 9999.99 | 19999.99 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W1124 | 9999.99 | 19999.99 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W1127 | 9999.99 | 19999.99 | True | 1 | 9.99999 | 0.0 | FAIL | WF1 |
| W1128 | 9999.99 | 19999.99 | True | 2 | 0 | 9.99999 | FAIL | WF1 |
| W1129 | 9999.99 | 19999.99 | True | 3 | 0 | 9.99999 | FAIL | WF1 |
| W1130 | 9999.99 | 19999.99 | True | 4 | 0 | 9.99999 | FAIL | WF1 |
| W1131 | 9999.99 | 19999.99 | True | 5 | 0 | 9.99999 | FAIL | WF1 |
| W1132 | 9999.99 | 19999.99 | True | 6 | 0 | 9.99999 | FAIL | WF1 |
| W1133 | 9999.99 | 19999.99 | True | 7 | 9.99999 | 0.0 | FAIL | WF1 |
| W1136 | 9999.99 | 20000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W1137 | 9999.99 | 20000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W1138 | 9999.99 | 20000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W1139 | 9999.99 | 20000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W1140 | 9999.99 | 20000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W1141 | 9999.99 | 20000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W1142 | 9999.99 | 20000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W1145 | 9999.99 | 20000 | True | 1 | 9.99999 | 0.0 | FAIL | WF1 |
| W1146 | 9999.99 | 20000 | True | 2 | 0 | 9.99999 | FAIL | WF1 |
| W1147 | 9999.99 | 20000 | True | 3 | 0 | 9.99999 | FAIL | WF1 |
| W1148 | 9999.99 | 20000 | True | 4 | 0 | 9.99999 | FAIL | WF1 |
| W1149 | 9999.99 | 20000 | True | 5 | 0 | 9.99999 | FAIL | WF1 |
| W1150 | 9999.99 | 20000 | True | 6 | 0 | 9.99999 | FAIL | WF1 |
| W1151 | 9999.99 | 20000 | True | 7 | 9.99999 | 0.0 | FAIL | WF1 |
| W1154 | 9999.99 | 20000.01 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W1155 | 9999.99 | 20000.01 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W1156 | 9999.99 | 20000.01 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W1157 | 9999.99 | 20000.01 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W1158 | 9999.99 | 20000.01 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W1159 | 9999.99 | 20000.01 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W1160 | 9999.99 | 20000.01 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W1163 | 9999.99 | 20000.01 | True | 1 | 9.99999 | 0.0 | FAIL | WF1 |
| W1164 | 9999.99 | 20000.01 | True | 2 | 0 | 9.99999 | FAIL | WF1 |
| W1165 | 9999.99 | 20000.01 | True | 3 | 0 | 9.99999 | FAIL | WF1 |
| W1166 | 9999.99 | 20000.01 | True | 4 | 0 | 9.99999 | FAIL | WF1 |
| W1167 | 9999.99 | 20000.01 | True | 5 | 0 | 9.99999 | FAIL | WF1 |
| W1168 | 9999.99 | 20000.01 | True | 6 | 0 | 9.99999 | FAIL | WF1 |
| W1169 | 9999.99 | 20000.01 | True | 7 | 9.99999 | 0.0 | FAIL | WF1 |
| W1190 | 10000 | 0 | False | 1 | 30 | 20.0 | FAIL | WF2 |
| W1191 | 10000 | 0 | False | 2 | 30 | 20.0 | FAIL | WF2 |
| W1192 | 10000 | 0 | False | 3 | 30 | 20.0 | FAIL | WF2 |
| W1193 | 10000 | 0 | False | 4 | 30 | 20.0 | FAIL | WF2 |
| W1194 | 10000 | 0 | False | 5 | 30 | 20.0 | FAIL | WF2 |
| W1195 | 10000 | 0 | False | 6 | 30 | 20.0 | FAIL | WF2 |
| W1196 | 10000 | 0 | False | 7 | 30 | 20.0 | FAIL | WF2 |
| W1199 | 10000 | 0 | True | 1 | 10 | 0.0 | FAIL | WF1 |
| W1200 | 10000 | 0 | True | 2 | 0 | 10.0 | FAIL | WF1 |
| W1201 | 10000 | 0 | True | 3 | 0 | 10.0 | FAIL | WF1 |
| W1202 | 10000 | 0 | True | 4 | 0 | 10.0 | FAIL | WF1 |
| W1203 | 10000 | 0 | True | 5 | 0 | 10.0 | FAIL | WF1 |
| W1204 | 10000 | 0 | True | 6 | 0 | 10.0 | FAIL | WF1 |
| W1205 | 10000 | 0 | True | 7 | 10 | 0.0 | FAIL | WF1 |
| W1208 | 10000 | 0.01 | False | 1 | 30 | 20.0 | FAIL | WF2 |
| W1209 | 10000 | 0.01 | False | 2 | 30 | 20.0 | FAIL | WF2 |
| W1210 | 10000 | 0.01 | False | 3 | 30 | 20.0 | FAIL | WF2 |
| W1211 | 10000 | 0.01 | False | 4 | 30 | 20.0 | FAIL | WF2 |
| W1212 | 10000 | 0.01 | False | 5 | 30 | 20.0 | FAIL | WF2 |
| W1213 | 10000 | 0.01 | False | 6 | 30 | 20.0 | FAIL | WF2 |
| W1214 | 10000 | 0.01 | False | 7 | 30 | 20.0 | FAIL | WF2 |
| W1217 | 10000 | 0.01 | True | 1 | 10 | 0.0 | FAIL | WF1 |
| W1218 | 10000 | 0.01 | True | 2 | 0 | 10.0 | FAIL | WF1 |
| W1219 | 10000 | 0.01 | True | 3 | 0 | 10.0 | FAIL | WF1 |
| W1220 | 10000 | 0.01 | True | 4 | 0 | 10.0 | FAIL | WF1 |
| W1221 | 10000 | 0.01 | True | 5 | 0 | 10.0 | FAIL | WF1 |
| W1222 | 10000 | 0.01 | True | 6 | 0 | 10.0 | FAIL | WF1 |
| W1223 | 10000 | 0.01 | True | 7 | 10 | 0.0 | FAIL | WF1 |
| W1226 | 10000 | 999.99 | False | 1 | 30 | 20.0 | FAIL | WF2 |
| W1227 | 10000 | 999.99 | False | 2 | 30 | 20.0 | FAIL | WF2 |
| W1228 | 10000 | 999.99 | False | 3 | 30 | 20.0 | FAIL | WF2 |
| W1229 | 10000 | 999.99 | False | 4 | 30 | 20.0 | FAIL | WF2 |
| W1230 | 10000 | 999.99 | False | 5 | 30 | 20.0 | FAIL | WF2 |
| W1231 | 10000 | 999.99 | False | 6 | 30 | 20.0 | FAIL | WF2 |
| W1232 | 10000 | 999.99 | False | 7 | 30 | 20.0 | FAIL | WF2 |
| W1235 | 10000 | 999.99 | True | 1 | 10 | 0.0 | FAIL | WF1 |
| W1236 | 10000 | 999.99 | True | 2 | 0 | 10.0 | FAIL | WF1 |
| W1237 | 10000 | 999.99 | True | 3 | 0 | 10.0 | FAIL | WF1 |
| W1238 | 10000 | 999.99 | True | 4 | 0 | 10.0 | FAIL | WF1 |
| W1239 | 10000 | 999.99 | True | 5 | 0 | 10.0 | FAIL | WF1 |
| W1240 | 10000 | 999.99 | True | 6 | 0 | 10.0 | FAIL | WF1 |
| W1241 | 10000 | 999.99 | True | 7 | 10 | 0.0 | FAIL | WF1 |
| W1244 | 10000 | 1000 | False | 1 | 10 | Within 1e-9 of expected | PASS | — |
| W1245 | 10000 | 1000 | False | 2 | 10 | Within 1e-9 of expected | PASS | — |
| W1246 | 10000 | 1000 | False | 3 | 10 | Within 1e-9 of expected | PASS | — |
| W1247 | 10000 | 1000 | False | 4 | 10 | Within 1e-9 of expected | PASS | — |
| W1248 | 10000 | 1000 | False | 5 | 10 | Within 1e-9 of expected | PASS | — |
| W1249 | 10000 | 1000 | False | 6 | 10 | Within 1e-9 of expected | PASS | — |
| W1250 | 10000 | 1000 | False | 7 | 10 | Within 1e-9 of expected | PASS | — |
| W1253 | 10000 | 1000 | True | 1 | 10 | 0.0 | FAIL | WF1 |
| W1254 | 10000 | 1000 | True | 2 | 0 | 10.0 | FAIL | WF1 |
| W1255 | 10000 | 1000 | True | 3 | 0 | 10.0 | FAIL | WF1 |
| W1256 | 10000 | 1000 | True | 4 | 0 | 10.0 | FAIL | WF1 |
| W1257 | 10000 | 1000 | True | 5 | 0 | 10.0 | FAIL | WF1 |
| W1258 | 10000 | 1000 | True | 6 | 0 | 10.0 | FAIL | WF1 |
| W1259 | 10000 | 1000 | True | 7 | 10 | 0.0 | FAIL | WF1 |
| W1262 | 10000 | 1000.01 | False | 1 | 10 | Within 1e-9 of expected | PASS | — |
| W1263 | 10000 | 1000.01 | False | 2 | 10 | Within 1e-9 of expected | PASS | — |
| W1264 | 10000 | 1000.01 | False | 3 | 10 | Within 1e-9 of expected | PASS | — |
| W1265 | 10000 | 1000.01 | False | 4 | 10 | Within 1e-9 of expected | PASS | — |
| W1266 | 10000 | 1000.01 | False | 5 | 10 | Within 1e-9 of expected | PASS | — |
| W1267 | 10000 | 1000.01 | False | 6 | 10 | Within 1e-9 of expected | PASS | — |
| W1268 | 10000 | 1000.01 | False | 7 | 10 | Within 1e-9 of expected | PASS | — |
| W1271 | 10000 | 1000.01 | True | 1 | 10 | 0.0 | FAIL | WF1 |
| W1272 | 10000 | 1000.01 | True | 2 | 0 | 10.0 | FAIL | WF1 |
| W1273 | 10000 | 1000.01 | True | 3 | 0 | 10.0 | FAIL | WF1 |
| W1274 | 10000 | 1000.01 | True | 4 | 0 | 10.0 | FAIL | WF1 |
| W1275 | 10000 | 1000.01 | True | 5 | 0 | 10.0 | FAIL | WF1 |
| W1276 | 10000 | 1000.01 | True | 6 | 0 | 10.0 | FAIL | WF1 |
| W1277 | 10000 | 1000.01 | True | 7 | 10 | 0.0 | FAIL | WF1 |
| W1280 | 10000 | 4999.99 | False | 1 | 10 | Within 1e-9 of expected | PASS | — |
| W1281 | 10000 | 4999.99 | False | 2 | 10 | Within 1e-9 of expected | PASS | — |
| W1282 | 10000 | 4999.99 | False | 3 | 10 | Within 1e-9 of expected | PASS | — |
| W1283 | 10000 | 4999.99 | False | 4 | 10 | Within 1e-9 of expected | PASS | — |
| W1284 | 10000 | 4999.99 | False | 5 | 10 | Within 1e-9 of expected | PASS | — |
| W1285 | 10000 | 4999.99 | False | 6 | 10 | Within 1e-9 of expected | PASS | — |
| W1286 | 10000 | 4999.99 | False | 7 | 10 | Within 1e-9 of expected | PASS | — |
| W1289 | 10000 | 4999.99 | True | 1 | 10 | 0.0 | FAIL | WF1 |
| W1290 | 10000 | 4999.99 | True | 2 | 0 | 10.0 | FAIL | WF1 |
| W1291 | 10000 | 4999.99 | True | 3 | 0 | 10.0 | FAIL | WF1 |
| W1292 | 10000 | 4999.99 | True | 4 | 0 | 10.0 | FAIL | WF1 |
| W1293 | 10000 | 4999.99 | True | 5 | 0 | 10.0 | FAIL | WF1 |
| W1294 | 10000 | 4999.99 | True | 6 | 0 | 10.0 | FAIL | WF1 |
| W1295 | 10000 | 4999.99 | True | 7 | 10 | 0.0 | FAIL | WF1 |
| W1298 | 10000 | 5000 | False | 1 | 0 | 10.0 | FAIL | WF3 |
| W1299 | 10000 | 5000 | False | 2 | 0 | 10.0 | FAIL | WF3 |
| W1300 | 10000 | 5000 | False | 3 | 0 | 10.0 | FAIL | WF3 |
| W1301 | 10000 | 5000 | False | 4 | 0 | 10.0 | FAIL | WF3 |
| W1302 | 10000 | 5000 | False | 5 | 0 | 10.0 | FAIL | WF3 |
| W1303 | 10000 | 5000 | False | 6 | 0 | 10.0 | FAIL | WF3 |
| W1304 | 10000 | 5000 | False | 7 | 0 | 10.0 | FAIL | WF3 |
| W1307 | 10000 | 5000 | True | 1 | 10 | 0.0 | FAIL | WF1 |
| W1308 | 10000 | 5000 | True | 2 | 0 | 10.0 | FAIL | WF1 |
| W1309 | 10000 | 5000 | True | 3 | 0 | 10.0 | FAIL | WF1 |
| W1310 | 10000 | 5000 | True | 4 | 0 | 10.0 | FAIL | WF1 |
| W1311 | 10000 | 5000 | True | 5 | 0 | 10.0 | FAIL | WF1 |
| W1312 | 10000 | 5000 | True | 6 | 0 | 10.0 | FAIL | WF1 |
| W1313 | 10000 | 5000 | True | 7 | 10 | 0.0 | FAIL | WF1 |
| W1316 | 10000 | 5000.01 | False | 1 | 0 | 10.0 | FAIL | WF3 |
| W1317 | 10000 | 5000.01 | False | 2 | 0 | 10.0 | FAIL | WF3 |
| W1318 | 10000 | 5000.01 | False | 3 | 0 | 10.0 | FAIL | WF3 |
| W1319 | 10000 | 5000.01 | False | 4 | 0 | 10.0 | FAIL | WF3 |
| W1320 | 10000 | 5000.01 | False | 5 | 0 | 10.0 | FAIL | WF3 |
| W1321 | 10000 | 5000.01 | False | 6 | 0 | 10.0 | FAIL | WF3 |
| W1322 | 10000 | 5000.01 | False | 7 | 0 | 10.0 | FAIL | WF3 |
| W1325 | 10000 | 5000.01 | True | 1 | 10 | 0.0 | FAIL | WF1 |
| W1326 | 10000 | 5000.01 | True | 2 | 0 | 10.0 | FAIL | WF1 |
| W1327 | 10000 | 5000.01 | True | 3 | 0 | 10.0 | FAIL | WF1 |
| W1328 | 10000 | 5000.01 | True | 4 | 0 | 10.0 | FAIL | WF1 |
| W1329 | 10000 | 5000.01 | True | 5 | 0 | 10.0 | FAIL | WF1 |
| W1330 | 10000 | 5000.01 | True | 6 | 0 | 10.0 | FAIL | WF1 |
| W1331 | 10000 | 5000.01 | True | 7 | 10 | 0.0 | FAIL | WF1 |
| W1334 | 10000 | 10000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W1335 | 10000 | 10000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W1336 | 10000 | 10000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W1337 | 10000 | 10000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W1338 | 10000 | 10000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W1339 | 10000 | 10000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W1340 | 10000 | 10000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W1343 | 10000 | 10000 | True | 1 | 10 | 0.0 | FAIL | WF1 |
| W1344 | 10000 | 10000 | True | 2 | 0 | 10.0 | FAIL | WF1 |
| W1345 | 10000 | 10000 | True | 3 | 0 | 10.0 | FAIL | WF1 |
| W1346 | 10000 | 10000 | True | 4 | 0 | 10.0 | FAIL | WF1 |
| W1347 | 10000 | 10000 | True | 5 | 0 | 10.0 | FAIL | WF1 |
| W1348 | 10000 | 10000 | True | 6 | 0 | 10.0 | FAIL | WF1 |
| W1349 | 10000 | 10000 | True | 7 | 10 | 0.0 | FAIL | WF1 |
| W1352 | 10000 | 19999.99 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W1353 | 10000 | 19999.99 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W1354 | 10000 | 19999.99 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W1355 | 10000 | 19999.99 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W1356 | 10000 | 19999.99 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W1357 | 10000 | 19999.99 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W1358 | 10000 | 19999.99 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W1361 | 10000 | 19999.99 | True | 1 | 10 | 0.0 | FAIL | WF1 |
| W1362 | 10000 | 19999.99 | True | 2 | 0 | 10.0 | FAIL | WF1 |
| W1363 | 10000 | 19999.99 | True | 3 | 0 | 10.0 | FAIL | WF1 |
| W1364 | 10000 | 19999.99 | True | 4 | 0 | 10.0 | FAIL | WF1 |
| W1365 | 10000 | 19999.99 | True | 5 | 0 | 10.0 | FAIL | WF1 |
| W1366 | 10000 | 19999.99 | True | 6 | 0 | 10.0 | FAIL | WF1 |
| W1367 | 10000 | 19999.99 | True | 7 | 10 | 0.0 | FAIL | WF1 |
| W1370 | 10000 | 20000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W1371 | 10000 | 20000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W1372 | 10000 | 20000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W1373 | 10000 | 20000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W1374 | 10000 | 20000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W1375 | 10000 | 20000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W1376 | 10000 | 20000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W1379 | 10000 | 20000 | True | 1 | 10 | 0.0 | FAIL | WF1 |
| W1380 | 10000 | 20000 | True | 2 | 0 | 10.0 | FAIL | WF1 |
| W1381 | 10000 | 20000 | True | 3 | 0 | 10.0 | FAIL | WF1 |
| W1382 | 10000 | 20000 | True | 4 | 0 | 10.0 | FAIL | WF1 |
| W1383 | 10000 | 20000 | True | 5 | 0 | 10.0 | FAIL | WF1 |
| W1384 | 10000 | 20000 | True | 6 | 0 | 10.0 | FAIL | WF1 |
| W1385 | 10000 | 20000 | True | 7 | 10 | 0.0 | FAIL | WF1 |
| W1388 | 10000 | 20000.01 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W1389 | 10000 | 20000.01 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W1390 | 10000 | 20000.01 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W1391 | 10000 | 20000.01 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W1392 | 10000 | 20000.01 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W1393 | 10000 | 20000.01 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W1394 | 10000 | 20000.01 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W1397 | 10000 | 20000.01 | True | 1 | 10 | 0.0 | FAIL | WF1 |
| W1398 | 10000 | 20000.01 | True | 2 | 0 | 10.0 | FAIL | WF1 |
| W1399 | 10000 | 20000.01 | True | 3 | 0 | 10.0 | FAIL | WF1 |
| W1400 | 10000 | 20000.01 | True | 4 | 0 | 10.0 | FAIL | WF1 |
| W1401 | 10000 | 20000.01 | True | 5 | 0 | 10.0 | FAIL | WF1 |
| W1402 | 10000 | 20000.01 | True | 6 | 0 | 10.0 | FAIL | WF1 |
| W1403 | 10000 | 20000.01 | True | 7 | 10 | 0.0 | FAIL | WF1 |
| W1424 | 10000.01 | 0 | False | 1 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1425 | 10000.01 | 0 | False | 2 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1426 | 10000.01 | 0 | False | 3 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1427 | 10000.01 | 0 | False | 4 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1428 | 10000.01 | 0 | False | 5 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1429 | 10000.01 | 0 | False | 6 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1430 | 10000.01 | 0 | False | 7 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1433 | 10000.01 | 0 | True | 1 | 10.00001 | 0.0 | FAIL | WF1 |
| W1434 | 10000.01 | 0 | True | 2 | 0 | 10.00001 | FAIL | WF1 |
| W1435 | 10000.01 | 0 | True | 3 | 0 | 10.00001 | FAIL | WF1 |
| W1436 | 10000.01 | 0 | True | 4 | 0 | 10.00001 | FAIL | WF1 |
| W1437 | 10000.01 | 0 | True | 5 | 0 | 10.00001 | FAIL | WF1 |
| W1438 | 10000.01 | 0 | True | 6 | 0 | 10.00001 | FAIL | WF1 |
| W1439 | 10000.01 | 0 | True | 7 | 10.00001 | 0.0 | FAIL | WF1 |
| W1442 | 10000.01 | 0.01 | False | 1 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1443 | 10000.01 | 0.01 | False | 2 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1444 | 10000.01 | 0.01 | False | 3 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1445 | 10000.01 | 0.01 | False | 4 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1446 | 10000.01 | 0.01 | False | 5 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1447 | 10000.01 | 0.01 | False | 6 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1448 | 10000.01 | 0.01 | False | 7 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1451 | 10000.01 | 0.01 | True | 1 | 10.00001 | 0.0 | FAIL | WF1 |
| W1452 | 10000.01 | 0.01 | True | 2 | 0 | 10.00001 | FAIL | WF1 |
| W1453 | 10000.01 | 0.01 | True | 3 | 0 | 10.00001 | FAIL | WF1 |
| W1454 | 10000.01 | 0.01 | True | 4 | 0 | 10.00001 | FAIL | WF1 |
| W1455 | 10000.01 | 0.01 | True | 5 | 0 | 10.00001 | FAIL | WF1 |
| W1456 | 10000.01 | 0.01 | True | 6 | 0 | 10.00001 | FAIL | WF1 |
| W1457 | 10000.01 | 0.01 | True | 7 | 10.00001 | 0.0 | FAIL | WF1 |
| W1460 | 10000.01 | 999.99 | False | 1 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1461 | 10000.01 | 999.99 | False | 2 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1462 | 10000.01 | 999.99 | False | 3 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1463 | 10000.01 | 999.99 | False | 4 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1464 | 10000.01 | 999.99 | False | 5 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1465 | 10000.01 | 999.99 | False | 6 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1466 | 10000.01 | 999.99 | False | 7 | 30.00003 | 20.00002 | FAIL | WF2 |
| W1469 | 10000.01 | 999.99 | True | 1 | 10.00001 | 0.0 | FAIL | WF1 |
| W1470 | 10000.01 | 999.99 | True | 2 | 0 | 10.00001 | FAIL | WF1 |
| W1471 | 10000.01 | 999.99 | True | 3 | 0 | 10.00001 | FAIL | WF1 |
| W1472 | 10000.01 | 999.99 | True | 4 | 0 | 10.00001 | FAIL | WF1 |
| W1473 | 10000.01 | 999.99 | True | 5 | 0 | 10.00001 | FAIL | WF1 |
| W1474 | 10000.01 | 999.99 | True | 6 | 0 | 10.00001 | FAIL | WF1 |
| W1475 | 10000.01 | 999.99 | True | 7 | 10.00001 | 0.0 | FAIL | WF1 |
| W1478 | 10000.01 | 1000 | False | 1 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1479 | 10000.01 | 1000 | False | 2 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1480 | 10000.01 | 1000 | False | 3 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1481 | 10000.01 | 1000 | False | 4 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1482 | 10000.01 | 1000 | False | 5 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1483 | 10000.01 | 1000 | False | 6 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1484 | 10000.01 | 1000 | False | 7 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1487 | 10000.01 | 1000 | True | 1 | 10.00001 | 0.0 | FAIL | WF1 |
| W1488 | 10000.01 | 1000 | True | 2 | 0 | 10.00001 | FAIL | WF1 |
| W1489 | 10000.01 | 1000 | True | 3 | 0 | 10.00001 | FAIL | WF1 |
| W1490 | 10000.01 | 1000 | True | 4 | 0 | 10.00001 | FAIL | WF1 |
| W1491 | 10000.01 | 1000 | True | 5 | 0 | 10.00001 | FAIL | WF1 |
| W1492 | 10000.01 | 1000 | True | 6 | 0 | 10.00001 | FAIL | WF1 |
| W1493 | 10000.01 | 1000 | True | 7 | 10.00001 | 0.0 | FAIL | WF1 |
| W1496 | 10000.01 | 1000.01 | False | 1 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1497 | 10000.01 | 1000.01 | False | 2 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1498 | 10000.01 | 1000.01 | False | 3 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1499 | 10000.01 | 1000.01 | False | 4 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1500 | 10000.01 | 1000.01 | False | 5 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1501 | 10000.01 | 1000.01 | False | 6 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1502 | 10000.01 | 1000.01 | False | 7 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1505 | 10000.01 | 1000.01 | True | 1 | 10.00001 | 0.0 | FAIL | WF1 |
| W1506 | 10000.01 | 1000.01 | True | 2 | 0 | 10.00001 | FAIL | WF1 |
| W1507 | 10000.01 | 1000.01 | True | 3 | 0 | 10.00001 | FAIL | WF1 |
| W1508 | 10000.01 | 1000.01 | True | 4 | 0 | 10.00001 | FAIL | WF1 |
| W1509 | 10000.01 | 1000.01 | True | 5 | 0 | 10.00001 | FAIL | WF1 |
| W1510 | 10000.01 | 1000.01 | True | 6 | 0 | 10.00001 | FAIL | WF1 |
| W1511 | 10000.01 | 1000.01 | True | 7 | 10.00001 | 0.0 | FAIL | WF1 |
| W1514 | 10000.01 | 4999.99 | False | 1 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1515 | 10000.01 | 4999.99 | False | 2 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1516 | 10000.01 | 4999.99 | False | 3 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1517 | 10000.01 | 4999.99 | False | 4 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1518 | 10000.01 | 4999.99 | False | 5 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1519 | 10000.01 | 4999.99 | False | 6 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1520 | 10000.01 | 4999.99 | False | 7 | 10.00001 | Within 1e-9 of expected | PASS | — |
| W1523 | 10000.01 | 4999.99 | True | 1 | 10.00001 | 0.0 | FAIL | WF1 |
| W1524 | 10000.01 | 4999.99 | True | 2 | 0 | 10.00001 | FAIL | WF1 |
| W1525 | 10000.01 | 4999.99 | True | 3 | 0 | 10.00001 | FAIL | WF1 |
| W1526 | 10000.01 | 4999.99 | True | 4 | 0 | 10.00001 | FAIL | WF1 |
| W1527 | 10000.01 | 4999.99 | True | 5 | 0 | 10.00001 | FAIL | WF1 |
| W1528 | 10000.01 | 4999.99 | True | 6 | 0 | 10.00001 | FAIL | WF1 |
| W1529 | 10000.01 | 4999.99 | True | 7 | 10.00001 | 0.0 | FAIL | WF1 |
| W1532 | 10000.01 | 5000 | False | 1 | 0 | 10.00001 | FAIL | WF3 |
| W1533 | 10000.01 | 5000 | False | 2 | 0 | 10.00001 | FAIL | WF3 |
| W1534 | 10000.01 | 5000 | False | 3 | 0 | 10.00001 | FAIL | WF3 |
| W1535 | 10000.01 | 5000 | False | 4 | 0 | 10.00001 | FAIL | WF3 |
| W1536 | 10000.01 | 5000 | False | 5 | 0 | 10.00001 | FAIL | WF3 |
| W1537 | 10000.01 | 5000 | False | 6 | 0 | 10.00001 | FAIL | WF3 |
| W1538 | 10000.01 | 5000 | False | 7 | 0 | 10.00001 | FAIL | WF3 |
| W1541 | 10000.01 | 5000 | True | 1 | 10.00001 | 0.0 | FAIL | WF1 |
| W1542 | 10000.01 | 5000 | True | 2 | 0 | 10.00001 | FAIL | WF1 |
| W1543 | 10000.01 | 5000 | True | 3 | 0 | 10.00001 | FAIL | WF1 |
| W1544 | 10000.01 | 5000 | True | 4 | 0 | 10.00001 | FAIL | WF1 |
| W1545 | 10000.01 | 5000 | True | 5 | 0 | 10.00001 | FAIL | WF1 |
| W1546 | 10000.01 | 5000 | True | 6 | 0 | 10.00001 | FAIL | WF1 |
| W1547 | 10000.01 | 5000 | True | 7 | 10.00001 | 0.0 | FAIL | WF1 |
| W1550 | 10000.01 | 5000.01 | False | 1 | 0 | 10.00001 | FAIL | WF3 |
| W1551 | 10000.01 | 5000.01 | False | 2 | 0 | 10.00001 | FAIL | WF3 |
| W1552 | 10000.01 | 5000.01 | False | 3 | 0 | 10.00001 | FAIL | WF3 |
| W1553 | 10000.01 | 5000.01 | False | 4 | 0 | 10.00001 | FAIL | WF3 |
| W1554 | 10000.01 | 5000.01 | False | 5 | 0 | 10.00001 | FAIL | WF3 |
| W1555 | 10000.01 | 5000.01 | False | 6 | 0 | 10.00001 | FAIL | WF3 |
| W1556 | 10000.01 | 5000.01 | False | 7 | 0 | 10.00001 | FAIL | WF3 |
| W1559 | 10000.01 | 5000.01 | True | 1 | 10.00001 | 0.0 | FAIL | WF1 |
| W1560 | 10000.01 | 5000.01 | True | 2 | 0 | 10.00001 | FAIL | WF1 |
| W1561 | 10000.01 | 5000.01 | True | 3 | 0 | 10.00001 | FAIL | WF1 |
| W1562 | 10000.01 | 5000.01 | True | 4 | 0 | 10.00001 | FAIL | WF1 |
| W1563 | 10000.01 | 5000.01 | True | 5 | 0 | 10.00001 | FAIL | WF1 |
| W1564 | 10000.01 | 5000.01 | True | 6 | 0 | 10.00001 | FAIL | WF1 |
| W1565 | 10000.01 | 5000.01 | True | 7 | 10.00001 | 0.0 | FAIL | WF1 |
| W1568 | 10000.01 | 10000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W1569 | 10000.01 | 10000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W1570 | 10000.01 | 10000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W1571 | 10000.01 | 10000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W1572 | 10000.01 | 10000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W1573 | 10000.01 | 10000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W1574 | 10000.01 | 10000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W1577 | 10000.01 | 10000 | True | 1 | 10.00001 | 0.0 | FAIL | WF1 |
| W1578 | 10000.01 | 10000 | True | 2 | 0 | 10.00001 | FAIL | WF1 |
| W1579 | 10000.01 | 10000 | True | 3 | 0 | 10.00001 | FAIL | WF1 |
| W1580 | 10000.01 | 10000 | True | 4 | 0 | 10.00001 | FAIL | WF1 |
| W1581 | 10000.01 | 10000 | True | 5 | 0 | 10.00001 | FAIL | WF1 |
| W1582 | 10000.01 | 10000 | True | 6 | 0 | 10.00001 | FAIL | WF1 |
| W1583 | 10000.01 | 10000 | True | 7 | 10.00001 | 0.0 | FAIL | WF1 |
| W1586 | 10000.01 | 19999.99 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W1587 | 10000.01 | 19999.99 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W1588 | 10000.01 | 19999.99 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W1589 | 10000.01 | 19999.99 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W1590 | 10000.01 | 19999.99 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W1591 | 10000.01 | 19999.99 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W1592 | 10000.01 | 19999.99 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W1595 | 10000.01 | 19999.99 | True | 1 | 10.00001 | 0.0 | FAIL | WF1 |
| W1596 | 10000.01 | 19999.99 | True | 2 | 0 | 10.00001 | FAIL | WF1 |
| W1597 | 10000.01 | 19999.99 | True | 3 | 0 | 10.00001 | FAIL | WF1 |
| W1598 | 10000.01 | 19999.99 | True | 4 | 0 | 10.00001 | FAIL | WF1 |
| W1599 | 10000.01 | 19999.99 | True | 5 | 0 | 10.00001 | FAIL | WF1 |
| W1600 | 10000.01 | 19999.99 | True | 6 | 0 | 10.00001 | FAIL | WF1 |
| W1601 | 10000.01 | 19999.99 | True | 7 | 10.00001 | 0.0 | FAIL | WF1 |
| W1604 | 10000.01 | 20000 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W1605 | 10000.01 | 20000 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W1606 | 10000.01 | 20000 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W1607 | 10000.01 | 20000 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W1608 | 10000.01 | 20000 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W1609 | 10000.01 | 20000 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W1610 | 10000.01 | 20000 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W1613 | 10000.01 | 20000 | True | 1 | 10.00001 | 0.0 | FAIL | WF1 |
| W1614 | 10000.01 | 20000 | True | 2 | 0 | 10.00001 | FAIL | WF1 |
| W1615 | 10000.01 | 20000 | True | 3 | 0 | 10.00001 | FAIL | WF1 |
| W1616 | 10000.01 | 20000 | True | 4 | 0 | 10.00001 | FAIL | WF1 |
| W1617 | 10000.01 | 20000 | True | 5 | 0 | 10.00001 | FAIL | WF1 |
| W1618 | 10000.01 | 20000 | True | 6 | 0 | 10.00001 | FAIL | WF1 |
| W1619 | 10000.01 | 20000 | True | 7 | 10.00001 | 0.0 | FAIL | WF1 |
| W1622 | 10000.01 | 20000.01 | False | 1 | 0 | Within 1e-9 of expected | PASS | — |
| W1623 | 10000.01 | 20000.01 | False | 2 | 0 | Within 1e-9 of expected | PASS | — |
| W1624 | 10000.01 | 20000.01 | False | 3 | 0 | Within 1e-9 of expected | PASS | — |
| W1625 | 10000.01 | 20000.01 | False | 4 | 0 | Within 1e-9 of expected | PASS | — |
| W1626 | 10000.01 | 20000.01 | False | 5 | 0 | Within 1e-9 of expected | PASS | — |
| W1627 | 10000.01 | 20000.01 | False | 6 | 0 | Within 1e-9 of expected | PASS | — |
| W1628 | 10000.01 | 20000.01 | False | 7 | 0 | Within 1e-9 of expected | PASS | — |
| W1631 | 10000.01 | 20000.01 | True | 1 | 10.00001 | 0.0 | FAIL | WF1 |
| W1632 | 10000.01 | 20000.01 | True | 2 | 0 | 10.00001 | FAIL | WF1 |
| W1633 | 10000.01 | 20000.01 | True | 3 | 0 | 10.00001 | FAIL | WF1 |
| W1634 | 10000.01 | 20000.01 | True | 4 | 0 | 10.00001 | FAIL | WF1 |
| W1635 | 10000.01 | 20000.01 | True | 5 | 0 | 10.00001 | FAIL | WF1 |
| W1636 | 10000.01 | 20000.01 | True | 6 | 0 | 10.00001 | FAIL | WF1 |
| W1637 | 10000.01 | 20000.01 | True | 7 | 10.00001 | 0.0 | FAIL | WF1 |

### Exploratory cases (no conformance oracle)

| ID | Amount | Balance | Student | Day | Expected fee | Observed | Result | Fault |
|---|---|---|---|---|---|---|---|---|
| W0001 | -0.01 | -0.01 | False | 0 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0002 | -0.01 | -0.01 | False | 1 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0003 | -0.01 | -0.01 | False | 2 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0004 | -0.01 | -0.01 | False | 3 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0005 | -0.01 | -0.01 | False | 4 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0006 | -0.01 | -0.01 | False | 5 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0007 | -0.01 | -0.01 | False | 6 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0008 | -0.01 | -0.01 | False | 7 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0009 | -0.01 | -0.01 | False | 8 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0010 | -0.01 | -0.01 | True | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0011 | -0.01 | -0.01 | True | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0012 | -0.01 | -0.01 | True | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0013 | -0.01 | -0.01 | True | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0014 | -0.01 | -0.01 | True | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0015 | -0.01 | -0.01 | True | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0016 | -0.01 | -0.01 | True | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0017 | -0.01 | -0.01 | True | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0018 | -0.01 | -0.01 | True | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0019 | -0.01 | 0 | False | 0 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0020 | -0.01 | 0 | False | 1 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0021 | -0.01 | 0 | False | 2 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0022 | -0.01 | 0 | False | 3 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0023 | -0.01 | 0 | False | 4 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0024 | -0.01 | 0 | False | 5 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0025 | -0.01 | 0 | False | 6 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0026 | -0.01 | 0 | False | 7 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0027 | -0.01 | 0 | False | 8 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0028 | -0.01 | 0 | True | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0029 | -0.01 | 0 | True | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0030 | -0.01 | 0 | True | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0031 | -0.01 | 0 | True | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0032 | -0.01 | 0 | True | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0033 | -0.01 | 0 | True | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0034 | -0.01 | 0 | True | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0035 | -0.01 | 0 | True | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0036 | -0.01 | 0 | True | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0037 | -0.01 | 0.01 | False | 0 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0038 | -0.01 | 0.01 | False | 1 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0039 | -0.01 | 0.01 | False | 2 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0040 | -0.01 | 0.01 | False | 3 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0041 | -0.01 | 0.01 | False | 4 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0042 | -0.01 | 0.01 | False | 5 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0043 | -0.01 | 0.01 | False | 6 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0044 | -0.01 | 0.01 | False | 7 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0045 | -0.01 | 0.01 | False | 8 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0046 | -0.01 | 0.01 | True | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0047 | -0.01 | 0.01 | True | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0048 | -0.01 | 0.01 | True | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0049 | -0.01 | 0.01 | True | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0050 | -0.01 | 0.01 | True | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0051 | -0.01 | 0.01 | True | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0052 | -0.01 | 0.01 | True | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0053 | -0.01 | 0.01 | True | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0054 | -0.01 | 0.01 | True | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0055 | -0.01 | 999.99 | False | 0 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0056 | -0.01 | 999.99 | False | 1 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0057 | -0.01 | 999.99 | False | 2 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0058 | -0.01 | 999.99 | False | 3 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0059 | -0.01 | 999.99 | False | 4 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0060 | -0.01 | 999.99 | False | 5 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0061 | -0.01 | 999.99 | False | 6 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0062 | -0.01 | 999.99 | False | 7 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0063 | -0.01 | 999.99 | False | 8 | Unspecified | returned -2.0E-5 | OBSERVED / SKIP | — |
| W0064 | -0.01 | 999.99 | True | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0065 | -0.01 | 999.99 | True | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0066 | -0.01 | 999.99 | True | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0067 | -0.01 | 999.99 | True | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0068 | -0.01 | 999.99 | True | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0069 | -0.01 | 999.99 | True | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0070 | -0.01 | 999.99 | True | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0071 | -0.01 | 999.99 | True | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0072 | -0.01 | 999.99 | True | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0073 | -0.01 | 1000 | False | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0074 | -0.01 | 1000 | False | 1 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0075 | -0.01 | 1000 | False | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0076 | -0.01 | 1000 | False | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0077 | -0.01 | 1000 | False | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0078 | -0.01 | 1000 | False | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0079 | -0.01 | 1000 | False | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0080 | -0.01 | 1000 | False | 7 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0081 | -0.01 | 1000 | False | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0082 | -0.01 | 1000 | True | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0083 | -0.01 | 1000 | True | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0084 | -0.01 | 1000 | True | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0085 | -0.01 | 1000 | True | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0086 | -0.01 | 1000 | True | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0087 | -0.01 | 1000 | True | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0088 | -0.01 | 1000 | True | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0089 | -0.01 | 1000 | True | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0090 | -0.01 | 1000 | True | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0091 | -0.01 | 1000.01 | False | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0092 | -0.01 | 1000.01 | False | 1 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0093 | -0.01 | 1000.01 | False | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0094 | -0.01 | 1000.01 | False | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0095 | -0.01 | 1000.01 | False | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0096 | -0.01 | 1000.01 | False | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0097 | -0.01 | 1000.01 | False | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0098 | -0.01 | 1000.01 | False | 7 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0099 | -0.01 | 1000.01 | False | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0100 | -0.01 | 1000.01 | True | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0101 | -0.01 | 1000.01 | True | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0102 | -0.01 | 1000.01 | True | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0103 | -0.01 | 1000.01 | True | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0104 | -0.01 | 1000.01 | True | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0105 | -0.01 | 1000.01 | True | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0106 | -0.01 | 1000.01 | True | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0107 | -0.01 | 1000.01 | True | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0108 | -0.01 | 1000.01 | True | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0109 | -0.01 | 4999.99 | False | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0110 | -0.01 | 4999.99 | False | 1 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0111 | -0.01 | 4999.99 | False | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0112 | -0.01 | 4999.99 | False | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0113 | -0.01 | 4999.99 | False | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0114 | -0.01 | 4999.99 | False | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0115 | -0.01 | 4999.99 | False | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0116 | -0.01 | 4999.99 | False | 7 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0117 | -0.01 | 4999.99 | False | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0118 | -0.01 | 4999.99 | True | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0119 | -0.01 | 4999.99 | True | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0120 | -0.01 | 4999.99 | True | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0121 | -0.01 | 4999.99 | True | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0122 | -0.01 | 4999.99 | True | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0123 | -0.01 | 4999.99 | True | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0124 | -0.01 | 4999.99 | True | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0125 | -0.01 | 4999.99 | True | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0126 | -0.01 | 4999.99 | True | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0127 | -0.01 | 5000 | False | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0128 | -0.01 | 5000 | False | 1 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0129 | -0.01 | 5000 | False | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0130 | -0.01 | 5000 | False | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0131 | -0.01 | 5000 | False | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0132 | -0.01 | 5000 | False | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0133 | -0.01 | 5000 | False | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0134 | -0.01 | 5000 | False | 7 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0135 | -0.01 | 5000 | False | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0136 | -0.01 | 5000 | True | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0137 | -0.01 | 5000 | True | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0138 | -0.01 | 5000 | True | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0139 | -0.01 | 5000 | True | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0140 | -0.01 | 5000 | True | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0141 | -0.01 | 5000 | True | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0142 | -0.01 | 5000 | True | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0143 | -0.01 | 5000 | True | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0144 | -0.01 | 5000 | True | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0145 | -0.01 | 5000.01 | False | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0146 | -0.01 | 5000.01 | False | 1 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0147 | -0.01 | 5000.01 | False | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0148 | -0.01 | 5000.01 | False | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0149 | -0.01 | 5000.01 | False | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0150 | -0.01 | 5000.01 | False | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0151 | -0.01 | 5000.01 | False | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0152 | -0.01 | 5000.01 | False | 7 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0153 | -0.01 | 5000.01 | False | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0154 | -0.01 | 5000.01 | True | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0155 | -0.01 | 5000.01 | True | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0156 | -0.01 | 5000.01 | True | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0157 | -0.01 | 5000.01 | True | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0158 | -0.01 | 5000.01 | True | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0159 | -0.01 | 5000.01 | True | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0160 | -0.01 | 5000.01 | True | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0161 | -0.01 | 5000.01 | True | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0162 | -0.01 | 5000.01 | True | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0163 | -0.01 | 10000 | False | 0 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0164 | -0.01 | 10000 | False | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0165 | -0.01 | 10000 | False | 2 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0166 | -0.01 | 10000 | False | 3 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0167 | -0.01 | 10000 | False | 4 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0168 | -0.01 | 10000 | False | 5 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0169 | -0.01 | 10000 | False | 6 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0170 | -0.01 | 10000 | False | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0171 | -0.01 | 10000 | False | 8 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0172 | -0.01 | 10000 | True | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0173 | -0.01 | 10000 | True | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0174 | -0.01 | 10000 | True | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0175 | -0.01 | 10000 | True | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0176 | -0.01 | 10000 | True | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0177 | -0.01 | 10000 | True | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0178 | -0.01 | 10000 | True | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0179 | -0.01 | 10000 | True | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0180 | -0.01 | 10000 | True | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0181 | -0.01 | 19999.99 | False | 0 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0182 | -0.01 | 19999.99 | False | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0183 | -0.01 | 19999.99 | False | 2 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0184 | -0.01 | 19999.99 | False | 3 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0185 | -0.01 | 19999.99 | False | 4 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0186 | -0.01 | 19999.99 | False | 5 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0187 | -0.01 | 19999.99 | False | 6 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0188 | -0.01 | 19999.99 | False | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0189 | -0.01 | 19999.99 | False | 8 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0190 | -0.01 | 19999.99 | True | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0191 | -0.01 | 19999.99 | True | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0192 | -0.01 | 19999.99 | True | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0193 | -0.01 | 19999.99 | True | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0194 | -0.01 | 19999.99 | True | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0195 | -0.01 | 19999.99 | True | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0196 | -0.01 | 19999.99 | True | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0197 | -0.01 | 19999.99 | True | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0198 | -0.01 | 19999.99 | True | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0199 | -0.01 | 20000 | False | 0 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0200 | -0.01 | 20000 | False | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0201 | -0.01 | 20000 | False | 2 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0202 | -0.01 | 20000 | False | 3 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0203 | -0.01 | 20000 | False | 4 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0204 | -0.01 | 20000 | False | 5 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0205 | -0.01 | 20000 | False | 6 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0206 | -0.01 | 20000 | False | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0207 | -0.01 | 20000 | False | 8 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0208 | -0.01 | 20000 | True | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0209 | -0.01 | 20000 | True | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0210 | -0.01 | 20000 | True | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0211 | -0.01 | 20000 | True | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0212 | -0.01 | 20000 | True | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0213 | -0.01 | 20000 | True | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0214 | -0.01 | 20000 | True | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0215 | -0.01 | 20000 | True | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0216 | -0.01 | 20000 | True | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0217 | -0.01 | 20000.01 | False | 0 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0218 | -0.01 | 20000.01 | False | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0219 | -0.01 | 20000.01 | False | 2 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0220 | -0.01 | 20000.01 | False | 3 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0221 | -0.01 | 20000.01 | False | 4 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0222 | -0.01 | 20000.01 | False | 5 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0223 | -0.01 | 20000.01 | False | 6 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0224 | -0.01 | 20000.01 | False | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0225 | -0.01 | 20000.01 | False | 8 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0226 | -0.01 | 20000.01 | True | 0 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0227 | -0.01 | 20000.01 | True | 1 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0228 | -0.01 | 20000.01 | True | 2 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0229 | -0.01 | 20000.01 | True | 3 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0230 | -0.01 | 20000.01 | True | 4 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0231 | -0.01 | 20000.01 | True | 5 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0232 | -0.01 | 20000.01 | True | 6 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0233 | -0.01 | 20000.01 | True | 7 | Unspecified | returned -0.0 | OBSERVED / SKIP | — |
| W0234 | -0.01 | 20000.01 | True | 8 | Unspecified | returned -1.0E-5 | OBSERVED / SKIP | — |
| W0235 | 0 | -0.01 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0236 | 0 | -0.01 | False | 1 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0237 | 0 | -0.01 | False | 2 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0238 | 0 | -0.01 | False | 3 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0239 | 0 | -0.01 | False | 4 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0240 | 0 | -0.01 | False | 5 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0241 | 0 | -0.01 | False | 6 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0242 | 0 | -0.01 | False | 7 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0243 | 0 | -0.01 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0244 | 0 | -0.01 | True | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0245 | 0 | -0.01 | True | 1 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0246 | 0 | -0.01 | True | 2 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0247 | 0 | -0.01 | True | 3 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0248 | 0 | -0.01 | True | 4 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0249 | 0 | -0.01 | True | 5 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0250 | 0 | -0.01 | True | 6 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0251 | 0 | -0.01 | True | 7 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0252 | 0 | -0.01 | True | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0253 | 0 | 0 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0261 | 0 | 0 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0262 | 0 | 0 | True | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0270 | 0 | 0 | True | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0271 | 0 | 0.01 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0279 | 0 | 0.01 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0280 | 0 | 0.01 | True | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0288 | 0 | 0.01 | True | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0289 | 0 | 999.99 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0297 | 0 | 999.99 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0298 | 0 | 999.99 | True | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0306 | 0 | 999.99 | True | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0307 | 0 | 1000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0315 | 0 | 1000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0316 | 0 | 1000 | True | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0324 | 0 | 1000 | True | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0325 | 0 | 1000.01 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0333 | 0 | 1000.01 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0334 | 0 | 1000.01 | True | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0342 | 0 | 1000.01 | True | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0343 | 0 | 4999.99 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0351 | 0 | 4999.99 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0352 | 0 | 4999.99 | True | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0360 | 0 | 4999.99 | True | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0361 | 0 | 5000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0369 | 0 | 5000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0370 | 0 | 5000 | True | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0378 | 0 | 5000 | True | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0379 | 0 | 5000.01 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0387 | 0 | 5000.01 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0388 | 0 | 5000.01 | True | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0396 | 0 | 5000.01 | True | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0397 | 0 | 10000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0405 | 0 | 10000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0406 | 0 | 10000 | True | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0414 | 0 | 10000 | True | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0415 | 0 | 19999.99 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0423 | 0 | 19999.99 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0424 | 0 | 19999.99 | True | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0432 | 0 | 19999.99 | True | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0433 | 0 | 20000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0441 | 0 | 20000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0442 | 0 | 20000 | True | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0450 | 0 | 20000 | True | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0451 | 0 | 20000.01 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0459 | 0 | 20000.01 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0460 | 0 | 20000.01 | True | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0468 | 0 | 20000.01 | True | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0469 | 0.01 | -0.01 | False | 0 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0470 | 0.01 | -0.01 | False | 1 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0471 | 0.01 | -0.01 | False | 2 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0472 | 0.01 | -0.01 | False | 3 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0473 | 0.01 | -0.01 | False | 4 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0474 | 0.01 | -0.01 | False | 5 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0475 | 0.01 | -0.01 | False | 6 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0476 | 0.01 | -0.01 | False | 7 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0477 | 0.01 | -0.01 | False | 8 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0478 | 0.01 | -0.01 | True | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0479 | 0.01 | -0.01 | True | 1 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0480 | 0.01 | -0.01 | True | 2 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0481 | 0.01 | -0.01 | True | 3 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0482 | 0.01 | -0.01 | True | 4 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0483 | 0.01 | -0.01 | True | 5 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0484 | 0.01 | -0.01 | True | 6 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0485 | 0.01 | -0.01 | True | 7 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0486 | 0.01 | -0.01 | True | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0487 | 0.01 | 0 | False | 0 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0495 | 0.01 | 0 | False | 8 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0496 | 0.01 | 0 | True | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0504 | 0.01 | 0 | True | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0505 | 0.01 | 0.01 | False | 0 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0513 | 0.01 | 0.01 | False | 8 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0514 | 0.01 | 0.01 | True | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0522 | 0.01 | 0.01 | True | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0523 | 0.01 | 999.99 | False | 0 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0531 | 0.01 | 999.99 | False | 8 | Unspecified | returned 2.0E-5 | OBSERVED / SKIP | — |
| W0532 | 0.01 | 999.99 | True | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0540 | 0.01 | 999.99 | True | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0541 | 0.01 | 1000 | False | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0549 | 0.01 | 1000 | False | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0550 | 0.01 | 1000 | True | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0558 | 0.01 | 1000 | True | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0559 | 0.01 | 1000.01 | False | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0567 | 0.01 | 1000.01 | False | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0568 | 0.01 | 1000.01 | True | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0576 | 0.01 | 1000.01 | True | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0577 | 0.01 | 4999.99 | False | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0585 | 0.01 | 4999.99 | False | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0586 | 0.01 | 4999.99 | True | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0594 | 0.01 | 4999.99 | True | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0595 | 0.01 | 5000 | False | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0603 | 0.01 | 5000 | False | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0604 | 0.01 | 5000 | True | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0612 | 0.01 | 5000 | True | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0613 | 0.01 | 5000.01 | False | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0621 | 0.01 | 5000.01 | False | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0622 | 0.01 | 5000.01 | True | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0630 | 0.01 | 5000.01 | True | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0631 | 0.01 | 10000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0639 | 0.01 | 10000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0640 | 0.01 | 10000 | True | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0648 | 0.01 | 10000 | True | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0649 | 0.01 | 19999.99 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0657 | 0.01 | 19999.99 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0658 | 0.01 | 19999.99 | True | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0666 | 0.01 | 19999.99 | True | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0667 | 0.01 | 20000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0675 | 0.01 | 20000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0676 | 0.01 | 20000 | True | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0684 | 0.01 | 20000 | True | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0685 | 0.01 | 20000.01 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0693 | 0.01 | 20000.01 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0694 | 0.01 | 20000.01 | True | 0 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0702 | 0.01 | 20000.01 | True | 8 | Unspecified | returned 1.0E-5 | OBSERVED / SKIP | — |
| W0703 | 5000 | -0.01 | False | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0704 | 5000 | -0.01 | False | 1 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0705 | 5000 | -0.01 | False | 2 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0706 | 5000 | -0.01 | False | 3 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0707 | 5000 | -0.01 | False | 4 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0708 | 5000 | -0.01 | False | 5 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0709 | 5000 | -0.01 | False | 6 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0710 | 5000 | -0.01 | False | 7 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0711 | 5000 | -0.01 | False | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0712 | 5000 | -0.01 | True | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0713 | 5000 | -0.01 | True | 1 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0714 | 5000 | -0.01 | True | 2 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0715 | 5000 | -0.01 | True | 3 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0716 | 5000 | -0.01 | True | 4 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0717 | 5000 | -0.01 | True | 5 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0718 | 5000 | -0.01 | True | 6 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0719 | 5000 | -0.01 | True | 7 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0720 | 5000 | -0.01 | True | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0721 | 5000 | 0 | False | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0729 | 5000 | 0 | False | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0730 | 5000 | 0 | True | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0738 | 5000 | 0 | True | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0739 | 5000 | 0.01 | False | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0747 | 5000 | 0.01 | False | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0748 | 5000 | 0.01 | True | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0756 | 5000 | 0.01 | True | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0757 | 5000 | 999.99 | False | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0765 | 5000 | 999.99 | False | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W0766 | 5000 | 999.99 | True | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0774 | 5000 | 999.99 | True | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0775 | 5000 | 1000 | False | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0783 | 5000 | 1000 | False | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0784 | 5000 | 1000 | True | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0792 | 5000 | 1000 | True | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0793 | 5000 | 1000.01 | False | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0801 | 5000 | 1000.01 | False | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0802 | 5000 | 1000.01 | True | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0810 | 5000 | 1000.01 | True | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0811 | 5000 | 4999.99 | False | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0819 | 5000 | 4999.99 | False | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0820 | 5000 | 4999.99 | True | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0828 | 5000 | 4999.99 | True | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0829 | 5000 | 5000 | False | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0837 | 5000 | 5000 | False | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0838 | 5000 | 5000 | True | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0846 | 5000 | 5000 | True | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0847 | 5000 | 5000.01 | False | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0855 | 5000 | 5000.01 | False | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0856 | 5000 | 5000.01 | True | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0864 | 5000 | 5000.01 | True | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0865 | 5000 | 10000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0873 | 5000 | 10000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0874 | 5000 | 10000 | True | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0882 | 5000 | 10000 | True | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0883 | 5000 | 19999.99 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0891 | 5000 | 19999.99 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0892 | 5000 | 19999.99 | True | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0900 | 5000 | 19999.99 | True | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0901 | 5000 | 20000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0909 | 5000 | 20000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0910 | 5000 | 20000 | True | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0918 | 5000 | 20000 | True | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0919 | 5000 | 20000.01 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0927 | 5000 | 20000.01 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0928 | 5000 | 20000.01 | True | 0 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0936 | 5000 | 20000.01 | True | 8 | Unspecified | returned 5.0 | OBSERVED / SKIP | — |
| W0937 | 9999.99 | -0.01 | False | 0 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0938 | 9999.99 | -0.01 | False | 1 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0939 | 9999.99 | -0.01 | False | 2 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0940 | 9999.99 | -0.01 | False | 3 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0941 | 9999.99 | -0.01 | False | 4 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0942 | 9999.99 | -0.01 | False | 5 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0943 | 9999.99 | -0.01 | False | 6 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0944 | 9999.99 | -0.01 | False | 7 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0945 | 9999.99 | -0.01 | False | 8 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0946 | 9999.99 | -0.01 | True | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W0947 | 9999.99 | -0.01 | True | 1 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0948 | 9999.99 | -0.01 | True | 2 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W0949 | 9999.99 | -0.01 | True | 3 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W0950 | 9999.99 | -0.01 | True | 4 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W0951 | 9999.99 | -0.01 | True | 5 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W0952 | 9999.99 | -0.01 | True | 6 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W0953 | 9999.99 | -0.01 | True | 7 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W0954 | 9999.99 | -0.01 | True | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W0955 | 9999.99 | 0 | False | 0 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0963 | 9999.99 | 0 | False | 8 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0964 | 9999.99 | 0 | True | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W0972 | 9999.99 | 0 | True | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W0973 | 9999.99 | 0.01 | False | 0 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0981 | 9999.99 | 0.01 | False | 8 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0982 | 9999.99 | 0.01 | True | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W0990 | 9999.99 | 0.01 | True | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W0991 | 9999.99 | 999.99 | False | 0 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W0999 | 9999.99 | 999.99 | False | 8 | Unspecified | returned 19.99998 | OBSERVED / SKIP | — |
| W1000 | 9999.99 | 999.99 | True | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1008 | 9999.99 | 999.99 | True | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1009 | 9999.99 | 1000 | False | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1017 | 9999.99 | 1000 | False | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1018 | 9999.99 | 1000 | True | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1026 | 9999.99 | 1000 | True | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1027 | 9999.99 | 1000.01 | False | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1035 | 9999.99 | 1000.01 | False | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1036 | 9999.99 | 1000.01 | True | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1044 | 9999.99 | 1000.01 | True | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1045 | 9999.99 | 4999.99 | False | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1053 | 9999.99 | 4999.99 | False | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1054 | 9999.99 | 4999.99 | True | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1062 | 9999.99 | 4999.99 | True | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1063 | 9999.99 | 5000 | False | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1071 | 9999.99 | 5000 | False | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1072 | 9999.99 | 5000 | True | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1080 | 9999.99 | 5000 | True | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1081 | 9999.99 | 5000.01 | False | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1089 | 9999.99 | 5000.01 | False | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1090 | 9999.99 | 5000.01 | True | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1098 | 9999.99 | 5000.01 | True | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1099 | 9999.99 | 10000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1107 | 9999.99 | 10000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1108 | 9999.99 | 10000 | True | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1116 | 9999.99 | 10000 | True | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1117 | 9999.99 | 19999.99 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1125 | 9999.99 | 19999.99 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1126 | 9999.99 | 19999.99 | True | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1134 | 9999.99 | 19999.99 | True | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1135 | 9999.99 | 20000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1143 | 9999.99 | 20000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1144 | 9999.99 | 20000 | True | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1152 | 9999.99 | 20000 | True | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1153 | 9999.99 | 20000.01 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1161 | 9999.99 | 20000.01 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1162 | 9999.99 | 20000.01 | True | 0 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1170 | 9999.99 | 20000.01 | True | 8 | Unspecified | returned 9.99999 | OBSERVED / SKIP | — |
| W1171 | 10000 | -0.01 | False | 0 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1172 | 10000 | -0.01 | False | 1 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1173 | 10000 | -0.01 | False | 2 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1174 | 10000 | -0.01 | False | 3 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1175 | 10000 | -0.01 | False | 4 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1176 | 10000 | -0.01 | False | 5 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1177 | 10000 | -0.01 | False | 6 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1178 | 10000 | -0.01 | False | 7 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1179 | 10000 | -0.01 | False | 8 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1180 | 10000 | -0.01 | True | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1181 | 10000 | -0.01 | True | 1 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1182 | 10000 | -0.01 | True | 2 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1183 | 10000 | -0.01 | True | 3 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1184 | 10000 | -0.01 | True | 4 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1185 | 10000 | -0.01 | True | 5 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1186 | 10000 | -0.01 | True | 6 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1187 | 10000 | -0.01 | True | 7 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1188 | 10000 | -0.01 | True | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1189 | 10000 | 0 | False | 0 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1197 | 10000 | 0 | False | 8 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1198 | 10000 | 0 | True | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1206 | 10000 | 0 | True | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1207 | 10000 | 0.01 | False | 0 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1215 | 10000 | 0.01 | False | 8 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1216 | 10000 | 0.01 | True | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1224 | 10000 | 0.01 | True | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1225 | 10000 | 999.99 | False | 0 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1233 | 10000 | 999.99 | False | 8 | Unspecified | returned 20.0 | OBSERVED / SKIP | — |
| W1234 | 10000 | 999.99 | True | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1242 | 10000 | 999.99 | True | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1243 | 10000 | 1000 | False | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1251 | 10000 | 1000 | False | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1252 | 10000 | 1000 | True | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1260 | 10000 | 1000 | True | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1261 | 10000 | 1000.01 | False | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1269 | 10000 | 1000.01 | False | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1270 | 10000 | 1000.01 | True | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1278 | 10000 | 1000.01 | True | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1279 | 10000 | 4999.99 | False | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1287 | 10000 | 4999.99 | False | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1288 | 10000 | 4999.99 | True | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1296 | 10000 | 4999.99 | True | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1297 | 10000 | 5000 | False | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1305 | 10000 | 5000 | False | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1306 | 10000 | 5000 | True | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1314 | 10000 | 5000 | True | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1315 | 10000 | 5000.01 | False | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1323 | 10000 | 5000.01 | False | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1324 | 10000 | 5000.01 | True | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1332 | 10000 | 5000.01 | True | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1333 | 10000 | 10000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1341 | 10000 | 10000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1342 | 10000 | 10000 | True | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1350 | 10000 | 10000 | True | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1351 | 10000 | 19999.99 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1359 | 10000 | 19999.99 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1360 | 10000 | 19999.99 | True | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1368 | 10000 | 19999.99 | True | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1369 | 10000 | 20000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1377 | 10000 | 20000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1378 | 10000 | 20000 | True | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1386 | 10000 | 20000 | True | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1387 | 10000 | 20000.01 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1395 | 10000 | 20000.01 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1396 | 10000 | 20000.01 | True | 0 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1404 | 10000 | 20000.01 | True | 8 | Unspecified | returned 10.0 | OBSERVED / SKIP | — |
| W1405 | 10000.01 | -0.01 | False | 0 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1406 | 10000.01 | -0.01 | False | 1 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1407 | 10000.01 | -0.01 | False | 2 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1408 | 10000.01 | -0.01 | False | 3 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1409 | 10000.01 | -0.01 | False | 4 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1410 | 10000.01 | -0.01 | False | 5 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1411 | 10000.01 | -0.01 | False | 6 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1412 | 10000.01 | -0.01 | False | 7 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1413 | 10000.01 | -0.01 | False | 8 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1414 | 10000.01 | -0.01 | True | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1415 | 10000.01 | -0.01 | True | 1 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1416 | 10000.01 | -0.01 | True | 2 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1417 | 10000.01 | -0.01 | True | 3 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1418 | 10000.01 | -0.01 | True | 4 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1419 | 10000.01 | -0.01 | True | 5 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1420 | 10000.01 | -0.01 | True | 6 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1421 | 10000.01 | -0.01 | True | 7 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1422 | 10000.01 | -0.01 | True | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1423 | 10000.01 | 0 | False | 0 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1431 | 10000.01 | 0 | False | 8 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1432 | 10000.01 | 0 | True | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1440 | 10000.01 | 0 | True | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1441 | 10000.01 | 0.01 | False | 0 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1449 | 10000.01 | 0.01 | False | 8 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1450 | 10000.01 | 0.01 | True | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1458 | 10000.01 | 0.01 | True | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1459 | 10000.01 | 999.99 | False | 0 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1467 | 10000.01 | 999.99 | False | 8 | Unspecified | returned 20.00002 | OBSERVED / SKIP | — |
| W1468 | 10000.01 | 999.99 | True | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1476 | 10000.01 | 999.99 | True | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1477 | 10000.01 | 1000 | False | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1485 | 10000.01 | 1000 | False | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1486 | 10000.01 | 1000 | True | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1494 | 10000.01 | 1000 | True | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1495 | 10000.01 | 1000.01 | False | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1503 | 10000.01 | 1000.01 | False | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1504 | 10000.01 | 1000.01 | True | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1512 | 10000.01 | 1000.01 | True | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1513 | 10000.01 | 4999.99 | False | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1521 | 10000.01 | 4999.99 | False | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1522 | 10000.01 | 4999.99 | True | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1530 | 10000.01 | 4999.99 | True | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1531 | 10000.01 | 5000 | False | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1539 | 10000.01 | 5000 | False | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1540 | 10000.01 | 5000 | True | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1548 | 10000.01 | 5000 | True | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1549 | 10000.01 | 5000.01 | False | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1557 | 10000.01 | 5000.01 | False | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1558 | 10000.01 | 5000.01 | True | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1566 | 10000.01 | 5000.01 | True | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1567 | 10000.01 | 10000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1575 | 10000.01 | 10000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1576 | 10000.01 | 10000 | True | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1584 | 10000.01 | 10000 | True | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1585 | 10000.01 | 19999.99 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1593 | 10000.01 | 19999.99 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1594 | 10000.01 | 19999.99 | True | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1602 | 10000.01 | 19999.99 | True | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1603 | 10000.01 | 20000 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1611 | 10000.01 | 20000 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1612 | 10000.01 | 20000 | True | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1620 | 10000.01 | 20000 | True | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1621 | 10000.01 | 20000.01 | False | 0 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1629 | 10000.01 | 20000.01 | False | 8 | Unspecified | returned 0.0 | OBSERVED / SKIP | — |
| W1630 | 10000.01 | 20000.01 | True | 0 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |
| W1638 | 10000.01 | 20000.01 | True | 8 | Unspecified | returned 10.00001 | OBSERVED / SKIP | — |

<!-- END GENERATED WITHDRAWAL RESULTS -->

### 3. Deposit
| ID | Amount | Balance | Student | Expected Fee | Actual Fee | Pass/Fail |
|---|---|---|---|---|---|---|

### 4. Transfer
| ID | Amount | From Balance | To Balance | Student | Expected Fee | Actual Fee | Pass/Fail |
|---|---|---|---|---|---|---|---|
