"""Update only the withdrawal table from the 28-case Surefire report."""
from collections import Counter
from itertools import product
from pathlib import Path
import re
import xml.etree.ElementTree as ET


def generate():
    balances = [999.99, 1000, 1000.01, 3000, 4999.99, 5000, 5000.01]
    grid = list(product(balances, [False, True], [2, 7]))
    root = ET.parse('target/surefire-reports/TEST-bank.utils.WithdrawalFeeBoundaryTest.xml').getroot()
    rows, counts, seen = [], Counter(), set()
    for case in root.findall('testcase'):
        index = int(re.search(r'\[(\d+)\]$', case.get('name')).group(1)) - 1
        assert index not in seen
        seen.add(index)
        balance, student, day = grid[index]
        case_id = f'W{index + 1:02d}'
        rate = (0.001 if day == 7 else 0) if student else (
            0.003 if balance < 1000 else 0.001 if balance < 5000 else 0)
        expected = f'{200 * rate:g}'
        assert case.find('error') is None and case.find('skipped') is None
        failure = case.find('failure')
        if failure is None:
            status, observed, fault = 'Pass', expected + ' (within tolerance)', '—'
        else:
            message = failure.get('message', '')
            assert case_id + ' ==>' in message
            status = 'Fail'
            observed = re.search(r'but was: <([^>]+)>', message).group(1)
            fault = 'WF1' if student else 'WF2' if balance < 1000 else 'WF3'
            counts[fault] += 1
        counts[status] += 1
        rows.append((case_id, 200, balance, student, 'Saturday' if day == 7 else 'Monday',
                     expected, observed, status, fault))
    assert len(seen) == len(grid) == 28
    text = f'\n**Results:** {counts["Pass"]} passed, {counts["Fail"]} failed; no errors or skipped cases.\n\n'
    text += '| ID | Amount | Balance | Student | Day | Expected fee | Actual fee | Pass/Fail | Fault |\n'
    text += '|---|---|---|---|---|---|---|---|---|\n'
    text += ''.join('| ' + ' | '.join(map(str, row)) + ' |\n' for row in sorted(rows))
    path = Path('docs/assignment1.md')
    marker = '<!-- GENERATED RESULTS: replaced by scripts/withdrawal_results.py -->'
    end_marker = '<!-- END GENERATED WITHDRAWAL RESULTS -->'
    prefix, rest = path.read_text().split(marker, 1)
    suffix = rest.split(end_marker, 1)[1]
    path.write_text(prefix + marker + '\n' + text + '\n' + end_marker + suffix)
    print(dict(counts))


if __name__ == '__main__':
    generate()
