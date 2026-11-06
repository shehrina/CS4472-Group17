"""Attach observed Surefire outcomes to the documented withdrawal case grid."""
from pathlib import Path
from collections import Counter
from itertools import product
import re
import xml.etree.ElementTree as ET


def generate():
    amounts = [-0.01, 0, 0.01, 5000, 9999.99, 10000, 10000.01]
    balances = [-0.01, 0, 0.01, 999.99, 1000, 1000.01, 4999.99,
                5000, 5000.01, 10000, 19999.99, 20000, 20000.01]
    groups = {False: [], True: []}
    for index, values in enumerate(product(amounts, balances, [False, True], range(9)), 1):
        amount, balance, student, day = values
        exploratory = amount < 0 or balance < 0 or day not in range(1, 8)
        groups[exploratory].append((f'W{index:04}', *values))
    xml = Path('target/surefire-reports/TEST-bank.utils.WithdrawalFeeBoundaryTest.xml')
    root = ET.parse(xml).getroot()
    rows = {False: [], True: []}
    counts = Counter()
    first = {}
    seen = set()
    for case in root.findall('testcase'):
        name = case.get('name')
        exploratory = name.startswith('observeUnspecifiedInputs')
        index = int(re.search(r'\[(\d+)\]$', name).group(1)) - 1
        case_id, amount, balance, student, day = groups[exploratory][index]
        assert case_id not in seen
        seen.add(case_id)
        fault = '—'
        if exploratory:
            skipped = case.find('skipped')
            assert skipped is not None
            message = skipped.get('message') or skipped.text or ''
            observed = message.split('no specified oracle; ', 1)[1].split('\n')[0]
            expected, status = 'Unspecified', 'OBSERVED / SKIP'
        else:
            if student:
                rate = 0.001 if day in (1, 7) else 0
            else:
                rate = 0.003 if balance < 1000 else 0.001 if balance < 5000 else 0
            expected = f'{amount * rate:.10g}'
            failure = case.find('failure')
            assert case.find('error') is None and case.find('skipped') is None
            if failure is None:
                status = 'PASS'
                observed = 'Within 1e-9 of expected'
            else:
                message = failure.get('message', '')
                assert case_id in message
                status = 'FAIL'
                observed = re.search(r'but was: <([^>]+)>', message).group(1)
                fault = 'WF1' if student else 'WF2' if balance < 1000 else 'WF3'
                counts[fault] += 1
                first.setdefault(fault, case_id)
        counts[status] += 1
        rows[exploratory].append((case_id, amount, balance, student, day,
                                  expected, observed, status, fault))
    assert len(seen) == sum(len(group) for group in groups.values())
    text = f'\nObserved withdrawal totals: **{counts["PASS"]} PASS, {counts["FAIL"]} FAIL, '
    text += f'{counts["OBSERVED / SKIP"]} exploratory SKIP**, no errors.\n\n'
    text += 'PASS observations are bounded by the assertion tolerance; exact successful return values were not logged.\n'
    for exploratory, title in [(False, 'Specification-derived cases'), (True, 'Exploratory cases (no conformance oracle)')]:
        text += f'\n### {title}\n\n'
        text += '| ID | Amount | Balance | Student | Day | Expected fee | Observed | Result | Fault |\n'
        text += '|---|---|---|---|---|---|---|---|---|\n'
        for row in sorted(rows[exploratory]):
            text += '| ' + ' | '.join(str(value) for value in row) + ' |\n'
    path = Path('docs/assignment1.md')
    marker = '<!-- GENERATED RESULTS: replaced by scripts/withdrawal_results.py -->'
    original = path.read_text()
    end_marker = '<!-- END GENERATED WITHDRAWAL RESULTS -->'
    prefix, existing = original.split(marker, 1)
    suffix = existing.split(end_marker, 1)[1] if end_marker in existing else ''
    path.write_text(prefix + marker + '\n' + text + '\n' + end_marker + suffix)
    print(dict(counts), 'representative cases:', first)
    log = Path('docs/failure-log.md')
    content = log.read_text()
    for fault, case_id in first.items():
        content = content.replace(f'| {fault} | See generated representative below |',
                                  f'| {fault} | {case_id} |')
    log.write_text(content)


if __name__ == '__main__':
    generate()
