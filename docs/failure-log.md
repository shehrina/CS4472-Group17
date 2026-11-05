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
