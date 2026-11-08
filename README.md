# CS4472 ATM - Assignment Repository

Course project for CS4472B Software Specification, Testing, and Quality
Assurance (Western University). An ATM system backed by a small bank server,
used as the system under test for the course assignments.

See [TESTING.md](TESTING.md) for the required test/fix workflow, pull requests,
and continuous integration. Your team implements the CI workflow specified
there; writing it is your first commit to your own copy of this repository.

Build: Java 11, Maven (`mvn -B test`).

The demo database: the application reads `bank.db` (SQLite), which is intentionally
not committed to the repository. Recreate it any time from the seed script:

```bash
sqlite3 bank.db < bank-seed.sql
```

(The test suite does not need the database; collaborators are stubbed with Mockito.)
