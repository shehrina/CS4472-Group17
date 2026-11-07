# Person 2 failure log — Assignment 3 retention

Date: November 4, 2026 (America/Toronto). Test IDs and per-case observations
are preserved in assignment1.md; production method remains unchanged.

| Failure ID | First failing case in grid | Suspected fault | Discovery / diagnosis timing |
|---|---|---|---|
| WF1 | W0497 | Reversed student weekday/weekend rates, lines 12–15 | Suspected during initial source inspection, before execution; confirmed in the same first JUnit run. Individual diagnosis duration was not timed. |
| WF2 | W0488 | 0.002 rather than 0.003 below 1000, line 19 | Suspected during initial source inspection, before execution; confirmed in the same first JUnit run. Individual diagnosis duration was not timed. |
| WF3 | W0596 | 10000 instead of 5000 threshold, line 20 | Suspected during initial source inspection, before execution; confirmed in the same first JUnit run. Individual diagnosis duration was not timed. |

Rough session chronology: repository inspection/import and test preparation
occurred around 02:10–02:18; Checkstyle finished 02:18:19; the first suite
finished 02:19:51 and Maven reported 9.496 seconds including downloads.
These are session/build timestamps, not measured per-fault discovery times.
Source inspection preceded test execution, so this was not a blind experiment;
expected values nevertheless follow the assignment specification alone.
Do not replace missing timing measurements with invented estimates. For future
sessions, record start, first failing run, and diagnosis completion per fault.

## November 6 revision — 28-case threshold-focused suite

The original W0001–W1638 IDs above refer to the superseded expanded suite,
retained here as historical discovery records. Current documentation/test IDs:

| Fault | Current representative | Current failing cases |
|---|---|---|
| WF1 | W03 | W03–W04, W07–W08, W11–W12, W15–W16, W19–W20, W23–W24, W27–W28 |
| WF2 | W01 | W01–W02 |
| WF3 | W21 | W21–W22, W25–W26 |

This revision removes arbitrary amount/balance sampling caps and exploratory
cases; it does not discover new faults. New per-fault discovery/diagnosis times
are therefore not claimed. Production code remains unchanged.
