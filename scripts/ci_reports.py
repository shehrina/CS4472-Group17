"""Render actual Surefire XML and JaCoCo XML as informational PR comments."""
from pathlib import Path
import xml.etree.ElementTree as ET


def render():
    reports = sorted(Path('target/surefire-reports').glob('TEST-*.xml'))
    rows = []
    totals = {'PASS': 0, 'FAIL': 0, 'ERROR': 0, 'SKIP': 0}
    for report in reports:
        for case in ET.parse(report).getroot().iter('testcase'):
            status = 'PASS'
            detail = ''
            for tag, label in [('failure', 'FAIL'), ('error', 'ERROR'), ('skipped', 'SKIP')]:
                node = case.find(tag)
                if node is not None:
                    status = label
                    detail = (node.get('message') or node.text or '').split('\n')[0]
                    break
            totals[status] += 1
            name = case.get('classname', '') + '.' + case.get('name', '')
            rows.append((name, status, detail))
    summary = ', '.join(f'{key}: {value}' for key, value in totals.items())
    text = '<!-- cs4472-junit -->\n## JUnit results (informational)\n\n' + summary + '\n\n'
    if not reports:
        text += 'No Surefire XML reports were produced. Check the build log.\n'
    else:
        text += 'Full per-test results and failure details are in the CI report artifact.\n\n'
        failures = [row for row in rows if row[1] in ('FAIL', 'ERROR')]
        text += '| Test | Status | Detail |\n|---|---|---|\n'
        for name, status, detail in failures[:100]:
            cells = [value.replace('|', '\\|').replace('<', '&lt;')[:350]
                     for value in (name, status, detail)]
            text += '| ' + ' | '.join(cells) + ' |\n'
        if len(failures) > 100:
            text += f'\nShowing 100 of {len(failures)} failures; see artifact for all results.\n'
    Path('target/junit-comment.md').write_text(text[:60000])
    coverage = '<!-- cs4472-coverage -->\n## JaCoCo coverage (informational)\n\n'
    xml = Path('target/site/jacoco/jacoco.xml')
    if xml.exists():
        coverage += '| Metric | Covered | Missed | Coverage |\n|---|---|---|---|\n'
        for counter in ET.parse(xml).getroot().findall('counter'):
            covered, missed = int(counter.get('covered')), int(counter.get('missed'))
            percentage = f'{100 * covered / (covered + missed):.2f}%' if covered + missed else 'N/A'
            coverage += f'| {counter.get("type")} | {covered} | {missed} | {percentage} |\n'
        coverage += '\nDownload the CI report artifact for the HTML report.\n'
    else:
        coverage += 'No coverage report was produced. Check the build log.\n'
    Path('target/coverage-comment.md').write_text(coverage)
    full = '| Test | Status | Detail |\n|---|---|---|\n'
    for name, status, detail in rows:
        full += '| ' + ' | '.join(v.replace('|', '\\|').replace('<', '&lt;')
                                  for v in (name, status, detail)) + ' |\n'
    Path('target/junit-results.md').write_text(full)


if __name__ == '__main__':
    Path('target').mkdir(exist_ok=True)
    render()
