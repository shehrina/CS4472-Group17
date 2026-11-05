## Pull request

### Phase

- [x] phase:tests — adds tests; assertion failures are expected and reported.

### Summary

Imports the professor's ATM main snapshot while preserving the team's initial
CI commit, and completes phase-aware CI reporting. Adds Person 2's withdrawal
fee tests using Robust Worst Case Boundary Value Analysis, with documented
sampling assumptions and separate exploratory observations for inputs without
a specified oracle. Production source is unchanged. This is Person 2's draft;
merge in the other team members' Session, deposit, and transfer descriptions
and evidence before using it for the full graded submission.

### For phase:tests PRs

`WithdrawalFeeBoundaryTest` traces to Assignment 1 → Withdrawal and Testing
Targets → FeesCalculator withdrawal. Full grid and actual per-case outcomes:
[assignment1.md](assignment1.md). 1008 conformance cases: 413 pass, 595 fail;
630 additional exploratory cases record behavior and abort without an oracle.
The three imported starter tests pass. Checkstyle has zero violations.

| Failing test | Suspected fault (file, method, line) | One-line justification |
|---|---|---|
| WF1: 420 cases; student, positive amount, valid day | bank/utils/FeesCalculator.java, calculateWithdrawalFee, lines 12–15 | Student weekend and weekday rate assignments are reversed relative to the specification. |
| WF2: 105 cases; non-student, positive amount, balance <1000 | Same class/method, line 19 | Implementation charges 0.002 × amount; specification requires 0.003 × amount. |
| WF3: 70 cases; non-student, positive amount, 5000 ≤ balance <10000 | Same class/method, line 20 | Implementation's <10000 condition charges fees where specification waives them from 5000 onward. |

Every failing case is assigned one of these IDs in the case table. Timing and
first failing representatives are retained in [failure-log.md](failure-log.md).

### Checklist

- [ ] Exactly phase:tests applied to the live PR
- [x] Checkstyle passes locally on Java 11
- [x] New tests are JUnit 5 under src/test/java
- [x] New withdrawal tests cite the assignment specification
- [ ] Other three members' deliverables integrated and verified
- [ ] Live CI reports and required lint/tests checks verified
- [ ] Three teammate approvals after final changes
