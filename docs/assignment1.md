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
TODO

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
| ID | Amount | Balance | Student | Day | Expected Fee | Actual Fee | Pass/Fail |
|---|---|---|---|---|---|---|---|

### 3. Deposit
| ID | Amount | Balance | Student | Expected Fee | Actual Fee | Pass/Fail |
|---|---|---|---|---|---|---|

### 4. Transfer
| ID | Amount | From Balance | To Balance | Student | Expected Fee | Actual Fee | Pass/Fail |
|---|---|---|---|---|---|---|---|
