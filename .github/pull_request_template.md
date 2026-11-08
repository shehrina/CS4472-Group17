## Pull request

<!-- 1. Pick exactly ONE phase label for this PR: `phase:tests` or `phase:fix`. -->
<!-- 2. Fill in the section for your phase and delete the other one. -->

### Phase
- [ ] `phase:tests` - this PR adds new tests (failures are expected and reported)
- [ ] `phase:fix` - this PR fixes localized faults (the whole suite must pass)

### Summary
<!-- What does this PR change and why? 3-5 sentences. -->

### For `phase:tests` PRs
<!-- For each new test class: which technique produced the test cases, and which
     specification section each test traces to. -->
<!-- Fault localization: for every failing test, which fault (file, method,
     statement/condition) do you believe it exposes, and why? One line each. -->

| Failing test | Suspected fault (file, method, line) | One-line justification |
|---|---|---|
| | | |

### For `phase:fix` PRs
<!-- For each fault fixed: what was wrong, what you changed, and which
     previously-failing tests now pass. -->

| Fault | Fix (file, method) | Tests now passing |
|---|---|---|
| | | |

### Checklist
- [ ] Exactly one of the `phase:tests` / `phase:fix` labels is applied
- [ ] `mvn -B checkstyle:check` passes locally
- [ ] New tests are JUnit 5 and live under `src/test/java`
- [ ] (phase:tests) Every test cites the specification section it traces to
- [ ] (phase:fix) The full suite passes locally (`mvn -B test`)
