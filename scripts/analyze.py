"""Convert JMH JSON rawData to CSV and descriptive Mean +/- SD charts."""
import argparse, json
from pathlib import Path
import pandas as pd
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

p = argparse.ArgumentParser()
p.add_argument('input', type=Path)
p.add_argument('--output', type=Path, default=Path('results/summary'))
args = p.parse_args()
args.output.mkdir(parents=True, exist_ok=True)
rows = []
for file in sorted(args.input.glob('*.json')):
    for result in json.loads(file.read_text(encoding='utf-8')):
        metric = result['primaryMetric']
        if metric['scoreUnit'] != 'ns/op':
            raise ValueError('Expected ns/op: ' + str(file))
        dataset = result['params']['dataset']
        kind, size, seed = dataset.rsplit('_', 2)
        for fork, values in enumerate(metric['rawData']):
            for iteration, time in enumerate(values):
                rows.append(dict(algo=result['params'].get('algo','merge'), dataset=dataset, distribution=kind, n=int(size), seed=int(seed),
                                 cutoff=int(result['params']['cutoff']), fork=fork,
                                 iteration=iteration, time_ns=time, source=file.name))
if not rows:
    raise ValueError('No measurement data found')
raw = pd.DataFrame(rows)
raw.to_csv(args.output / 'raw.csv', index=False)
keys = ['algo', 'dataset', 'distribution', 'n', 'seed', 'cutoff']
forks = raw.groupby(keys + ['source', 'fork'], as_index=False).time_ns.mean()
datasets = forks.groupby(keys).time_ns.agg(['mean', 'std', 'count']).reset_index()
datasets.to_csv(args.output / 'datasets.csv', index=False)
summary = []
for (algo, n, kind, cutoff), group in datasets.groupby(['algo', 'n', 'distribution', 'cutoff']):
    values = group['mean'] if len(group) > 1 else forks[(forks.algo == algo) & (forks.n == n) & (forks.distribution == kind) & (forks.cutoff == cutoff)].time_ns
    summary.append(dict(algo=algo, n=n, distribution=kind, cutoff=cutoff, mean=values.mean(), sd=values.std(ddof=1),
                        count=len(values), sd_basis='datasets' if len(group) > 1 else 'forks'))
summary = pd.DataFrame(summary)
summary.to_csv(args.output / 'summary.csv', index=False)
for (algo, n, kind), group in summary.groupby(['algo', 'n', 'distribution']):
    group = group.sort_values('cutoff')
    fig, ax = plt.subplots(figsize=(8, 4.5))
    ax.errorbar(group.cutoff, group['mean'] / 1e6, yerr=group.sd / 1e6, marker='o', capsize=3)
    ax.set(xlabel='CUTOFF (elements)', ylabel='Time (ms)', title=f'{algo} {kind}, N={n}: Mean ± SD ({group.sd_basis.iloc[0]})')
    ax.grid(alpha=.25)
    fig.tight_layout()
    fig.savefig(args.output / f'{algo}_{kind}_{n}.png', dpi=180)
    plt.close(fig)
