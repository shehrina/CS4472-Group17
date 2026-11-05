# How testing works in this repository

This repository is the starting point for the course assignments. Each team
works in its own copy. All graded work enters the `main` branch through pull
requests from `dev`, in two phases. Your team also implements the continuous
integration workflow described below; that implementation is your first commit.

## Branches

- `main` is protected. You cannot push to it directly. Everything arrives
  via a pull request from `dev`, reviewed and approved by your teammates.
- `dev` is where day-to-day work lands. Pushes to `dev` run CI in
  informational mode. Open the graded PRs from `dev` into `main`.

## Your first commit: the CI workflow

Create `.github/workflows/ci.yml` in your repository. It must:

1. Trigger on pull requests targeting `main` (opened, synchronize, reopened,
   labeled, unlabeled) and on pushes to `dev`.
2. Run a **lint** job on Java 11 (Temurin distribution) executing
   `mvn -B checkstyle:check` against `checkstyle.xml`. Lint failures always
   block merging.
3. Run a **tests** job on Java 11 that first inspects the PR's labels and
   requires **exactly one** of `phase:tests` or `phase:fix`; any other
   combination fails the run with an error message.
   - In `phase:tests`, run the suite with test failures tolerated
     (`mvn -B -Dmaven.test.failure.ignore=true test`): new tests are
     *expected* to fail, because the code under test contains faults and
     finding them is the point.
   - In `phase:fix`, run `mvn -B test`: the **entire** suite must pass.
   - On pushes to `dev` (no PR, no label), run the suite with failures
     tolerated, informational only.
4. Publish the JUnit results as a PR check/comment and the JaCoCo coverage
   report as a PR comment. Both are informational and never block merging.

Start from the [GitHub Actions documentation](https://docs.github.com/en/actions)
for workflow syntax, `actions/checkout`, and `actions/setup-java`. The required
*behavior* above is the specification; the implementation is yours.

## The two phases

Every graded pull request carries exactly one label:

- **`phase:tests`** - the PR adds new JUnit tests derived from the
  specification (Assignment 1) or from the code structure (Assignment 2).
  Tests are *expected* to fail: the code under test contains faults, and
  finding them is the point. CI checks that the code compiles, that
  Checkstyle passes, and that the test suite runs to completion; it then
  posts the list of failures as a PR comment. Failing tests do **not**
  block merging in this phase.
- **`phase:fix`** - the PR fixes the faults your tests localized. Here the
  gate is strict: the **entire** test suite must pass (`mvn -B test`),
  plus Checkstyle. A red suite blocks merging.

## Reviews

A PR to `main` can only be merged after **every teammate except the author**
has approved it: configure branch protection on `main` to require
`team size - 1` approving reviews. Approvals are dismissed when new commits
are pushed (enable "dismiss stale pull request approvals"), so review again
after fixes are pushed.

## The automated checks

| Check | What it does | Blocks merging? |
|---|---|---|
| Lint | Checkstyle (`checkstyle.xml`): unused imports, braces, line length, naming | Always |
| Tests | `mvn -B test`; failures ignored in `phase:tests`, fatal in `phase:fix` | In `phase:fix` |
| JUnit test report | Posts pass/fail per test as a PR comment | Never (informational) |
| Coverage | JaCoCo report posted as a PR comment | Never (informational) |

## Running things locally

```bash
mvn -B test                 # run the suite (fails the build on test failure)
mvn -B checkstyle:check     # run the linter
```

Use Java 11. The JaCoCo HTML report lands in
`target/site/jacoco/index.html` after `mvn -B test`.

## Test doubles

`Mockito` is on the test classpath. When a white-box target calls
collaborators (`FeesCalculator`, `DBHandler`), stub them so the target is
tested in isolation. Note: Mockito's strict stubbing rejects back-to-back
`when(...)` stubbings of the same method; prefer
`doReturn(x).when(mock)...` in that case.
