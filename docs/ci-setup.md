# CI setup and remaining GitHub configuration

The local dev branch contains the imported course snapshot and Person 2 work.
The initial CI commit 54ed1a6 is preserved; main is unchanged. No push, merge,
or branch-protection API update was performed during this work.

## Implemented workflow

- Push to dev: run Checkstyle and informational tests.
- PR targeting main: opened/synchronize/reopened/labeled/unlabeled triggers.
- Exactly one phase label is mandatory; unrelated labels are allowed.
- phase:tests: tolerate JUnit failures; compile/build failures remain fatal.
- phase:fix: every test must pass; test failures fail the tests job.
- lint always runs mvn -B checkstyle:check on Java 11 Temurin.
- JUnit failure summary and JaCoCo coverage are separate informational PR
  comments, updated on reruns; raw XML, full per-test Markdown, and HTML
  coverage are uploaded as an artifact. Comment/artifact failures are
  informational and cannot turn a successful test run into a blocking failure.
- Coverage is regenerated after strict failures because a failed test phase
  may prevent the lifecycle-bound JaCoCo report goal from running.

The report scripts use only Python's standard library. github-script uses the
workflow token; it does not require a personal access token. The specification
and original PR template are retained from the professor's main branch.

## Before the first push

Review the local changes and commit on dev. Push only with the repository
owner's approval. No main change is needed. The first dev push will create
GitHub Actions check contexts; inspect its artifacts and logs.

## GitHub settings to finish after the first approved push

1. In repository Settings → Branches (or the existing main ruleset), inspect
   the existing protection rather than creating a conflicting second rule.
2. Protect main. Require a pull request, **3 approving reviews** for this
   four-person team, and dismissal of stale approvals after new commits.
3. Enable required status checks and select **lint** and **tests** from this
   CI workflow. lint must always block; tests must also be required so invalid
   labels, compilation problems, and phase:fix failures block merging.
   In phase:tests, assertion failures leave tests successful by design.
4. Ensure protection is enforced and bypass permissions do not undermine the
   intended workflow. Verify the saved settings; they have not been inspected
   or changed from this environment.
5. Create labels phase:tests and phase:fix if missing. Open the graded PR from
   dev to main only once the team's other testing sections are ready; apply
   exactly phase:tests. Use the provided template and incorporate the Person 2
   draft in person2-pr-draft.md.
6. Verify two report comments, downloadable artifacts, and required checks on
   the live PR. All three teammates approve after the final changes. Merge
   only with the owner's approval and submit both URLs to OWL.

Local verification covers YAML parsing, seven phase-label scenarios, Java 11
compilation/JUnit execution, Checkstyle, report generation, and byte comparison
of all 58 production source files against upstream. A live Actions run and
saved GitHub protection settings cannot be certified before those remote steps.

Official references consulted:
- https://docs.github.com/en/actions/reference/workflows-and-actions/workflow-syntax
- https://github.com/actions/setup-java
- https://github.com/actions/github-script
