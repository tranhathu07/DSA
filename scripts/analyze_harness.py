"""Thống kê và vẽ biểu đồ cho kết quả của study.harness.Grid (hoặc nhiều đợt gộp lại).

Đơn vị độc lập: JVM fork.  Mức 1: trung bình 10 iteration trong một fork.
Mức 2: trung bình các fork của một dataset.  Mức 3: trung bình các dataset cùng
(algo, phân bố, n, cutoff); SD/CI tính giữa các dataset (nhiều seed) hoặc giữa các fork (dataset xác định).

Dùng: python scripts/analyze_harness.py results/grid-A results/grid-B --output results/analysis
"""
import argparse, glob, os, re
from pathlib import Path
import numpy as np, pandas as pd
from scipy import stats
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

plt.rcParams.update({'font.family': 'serif', 'font.serif': ['Times New Roman', 'Liberation Serif', 'DejaVu Serif'],
                     'font.size': 11, 'axes.grid': True, 'grid.alpha': .25, 'mathtext.fontset': 'stix'})
ALGO_NAME = {'merge': 'Mergesort', 'quick': 'Quicksort'}
DIST_NAME = {'random': 'ngẫu nhiên', 'nearly_sorted': 'gần tăng dần', 'duplicates': 'nhiều giá trị trùng',
             'ascending': 'tăng dần', 'descending': 'giảm dần'}

ap = argparse.ArgumentParser()
ap.add_argument('inputs', nargs='+', type=Path)
ap.add_argument('--output', type=Path, default=Path('results/analysis'))
ap.add_argument('--pilot', type=Path, default=None)
args = ap.parse_args()
out = args.output; out.mkdir(parents=True, exist_ok=True)

# ---------- đọc dữ liệu thô ----------
rows = []
for d in args.inputs:
    for f in sorted((d / 'jobs').glob('*.csv')):
        algo, dataset, cutoff, fork = f.stem.split('__')
        kind, size, seed = dataset.rsplit('_', 2)
        df = pd.read_csv(f, names=['phase', 'iteration', 'time_ns'])
        df = df[df.phase == 'M']
        df = df.assign(algo=algo, dataset=dataset, distribution=kind, n=int(size), seed=int(seed),
                       cutoff=int(cutoff), fork=int(fork), run=d.name)
        rows.append(df)
raw = pd.concat(rows, ignore_index=True)
raw.drop(columns='phase').to_csv(out / 'raw.csv', index=False)

K = ['algo', 'distribution', 'n', 'dataset', 'cutoff']
forks = raw.groupby(K + ['fork'], as_index=False).time_ns.mean()            # mức 1
forks.to_csv(out / 'forks.csv', index=False)
ds = forks.groupby(K, as_index=False).time_ns.agg(mean='mean', sd='std', nforks='count')   # mức 2
ds.to_csv(out / 'datasets.csv', index=False)

# tỉ lệ thời gian so với cutoff = 0 trong cùng dataset (so sánh ghép cặp theo dataset)
base = ds[ds.cutoff == 0].set_index(['algo', 'dataset'])['mean']
ds['ratio0'] = [r['mean'] / base[(r.algo, r.dataset)] for _, r in ds.iterrows()]

def tci(x, conf=.95):
    x = np.asarray(x, float); n = len(x)
    if n < 2: return np.nan
    return stats.t.ppf((1 + conf) / 2, n - 1) * x.std(ddof=1) / np.sqrt(n)

G = ['algo', 'distribution', 'n', 'cutoff']
rec = []
for key, g in ds.groupby(G):
    algo, dist, n, c = key
    if len(g) > 1: vals, basis = g['mean'].values, 'dataset'
    else:
        vals = forks[(forks.algo == algo) & (forks.distribution == dist) & (forks.n == n) & (forks.cutoff == c)].time_ns.values
        basis = 'fork'
    rr = g.ratio0.values
    rec.append(dict(algo=algo, distribution=dist, n=n, cutoff=c, mean_ms=vals.mean() / 1e6, sd_ms=vals.std(ddof=1) / 1e6,
                    ci95_ms=tci(vals) / 1e6, k=len(vals), basis=basis, ratio0=np.exp(np.log(rr).mean()),
                    ratio0_ci=np.exp(np.log(rr).mean() + tci(np.log(rr))) / np.exp(np.log(rr).mean()) if len(rr) > 1 else np.nan,
                    cv_pct=100 * vals.std(ddof=1) / vals.mean()))
S = pd.DataFrame(rec)
S['speedup_pct'] = (1 - S.ratio0) * 100        # dương = nhanh hơn cutoff 0
S.to_csv(out / 'summary.csv', index=False)

# ---------- cutoff tốt nhất + kiểm định so với cutoff tốt nhất ----------
# Chuẩn hoá mỗi fork-mean theo trung bình dataset ở cutoff tốt nhất, rồi Welch t giữa 2 nhóm.
best_rows = []
for (algo, dist, n), g in S.groupby(['algo', 'distribution', 'n']):
    cbest = int(g.loc[g.mean_ms.idxmin(), 'cutoff'])
    f = forks[(forks.algo == algo) & (forks.distribution == dist) & (forks.n == n)].copy()
    ref = f[f.cutoff == cbest].groupby('dataset').time_ns.mean()
    f['rel'] = f.time_ns / f.dataset.map(ref)
    rb = f[f.cutoff == cbest].rel.values
    equiv = []
    for c in sorted(f.cutoff.unique()):
        if c == cbest: continue
        rc = f[f.cutoff == c].rel.values
        p = stats.ttest_ind(rc, rb, equal_var=False).pvalue
        if p >= .05: equiv.append(c)
    get = lambda c: g[g.cutoff == c].iloc[0]
    row = dict(algo=algo, distribution=dist, n=n, best_cutoff=cbest, best_ms=get(cbest).mean_ms,
               ms_c0=get(0).mean_ms, speedup_best_pct=get(cbest).speedup_pct,
               speedup_c7_pct=get(7).speedup_pct if (g.cutoff == 7).any() else np.nan,
               speedup_c10_pct=get(10).speedup_pct if (g.cutoff == 10).any() else np.nan,
               not_distinguishable=' '.join(map(str, [cbest] + equiv)))
    best_rows.append(row)
B = pd.DataFrame(best_rows); B.to_csv(out / 'best_cutoff.csv', index=False)

# ---------- vùng cutoff tốt chung: trung bình hình học của ratio0 trên các nhóm ----------
R = S.groupby(['algo', 'cutoff']).apply(lambda g: pd.Series({'gmean_ratio0': np.exp(np.log(g.ratio0).mean())}), include_groups=False).reset_index()
R['speedup_pct'] = (1 - R.gmean_ratio0) * 100
R.to_csv(out / 'overall_by_cutoff.csv', index=False)

# ---------- biểu đồ ----------
cuts = sorted(S.cutoff.unique()); pos = {c: i for i, c in enumerate(cuts)}
def xticks(ax):
    ax.set_xticks(range(len(cuts))); ax.set_xticklabels(cuts, fontsize=9)
    ax.set_xlabel('CUTOFF (số phần tử của đoạn dùng insertion sort)')
    for c, col in ((7, 'tab:green'), (10, 'tab:red')):
        if c in pos: ax.axvline(pos[c], color=col, ls='--', lw=1, alpha=.7)

figdir = out / 'fig'; figdir.mkdir(exist_ok=True)
sizes = sorted(S.n.unique()); algos = [a for a in ('merge', 'quick') if a in S.algo.unique()]
fmt = lambda n: f'{n:,}'.replace(',', '.')

# H1: thời gian tuyệt đối Mean ± SD, dữ liệu ngẫu nhiên, mỗi thuật toán × kích thước
fig, axes = plt.subplots(len(algos), len(sizes), figsize=(4.3 * len(sizes), 3.4 * len(algos)), squeeze=False)
for i, a in enumerate(algos):
    for j, n in enumerate(sizes):
        ax = axes[i, j]; g = S[(S.algo == a) & (S.n == n) & (S.distribution == 'random')].sort_values('cutoff')
        if g.empty: ax.axis('off'); continue
        ax.errorbar([pos[c] for c in g.cutoff], g.mean_ms, yerr=g.sd_ms, marker='o', ms=4, capsize=3, lw=1.2)
        xticks(ax); ax.set_ylabel('Thời gian (ms)'); ax.set_title(f'{ALGO_NAME[a]}, N = {fmt(n)}', fontsize=11)
        if i < len(algos) - 1: ax.set_xlabel('')
fig.suptitle('Dữ liệu ngẫu nhiên: Mean ± SD theo CUTOFF (đường nét đứt xanh = 7, đỏ = 10)', y=1.0)
fig.tight_layout(); fig.savefig(figdir / 'fig1_abs_random.png', dpi=200); plt.close(fig)

# H2: tỉ lệ so với cutoff 0 (dữ liệu ngẫu nhiên), có dải tin cậy 95% giữa các dataset
fig, axes = plt.subplots(1, len(algos), figsize=(5.6 * len(algos), 4.2), squeeze=False, sharey=False)
for i, a in enumerate(algos):
    ax = axes[0, i]
    for n in sizes:
        g = S[(S.algo == a) & (S.n == n) & (S.distribution == 'random')].sort_values('cutoff')
        if g.empty: continue
        x = [pos[c] for c in g.cutoff]; ax.plot(x, g.ratio0, marker='o', ms=3.5, label=f'N = {fmt(n)}')
        lo = g.ratio0 / g.ratio0_ci.fillna(1); hi = g.ratio0 * g.ratio0_ci.fillna(1)
        ax.fill_between(x, lo, hi, alpha=.15)
    ax.axhline(1, color='k', lw=.8); xticks(ax); ax.set_ylabel('Thời gian tương đối so với CUTOFF = 0')
    ax.set_title(f'{ALGO_NAME[a]} (dữ liệu ngẫu nhiên)'); ax.legend()
fig.tight_layout(); fig.savefig(figdir / 'fig2_ratio_random.png', dpi=200); plt.close(fig)

# H3: tất cả phân bố — tỉ lệ so với cutoff 0, mỗi hàng 1 thuật toán, mỗi cột 1 kích thước
fig, axes = plt.subplots(len(algos), len(sizes), figsize=(4.4 * len(sizes), 3.5 * len(algos)), squeeze=False)
for i, a in enumerate(algos):
    for j, n in enumerate(sizes):
        ax = axes[i, j]
        for dist in DIST_NAME:
            g = S[(S.algo == a) & (S.n == n) & (S.distribution == dist)].sort_values('cutoff')
            if g.empty: continue
            ax.plot([pos[c] for c in g.cutoff], g.ratio0, marker='o', ms=3, label=DIST_NAME[dist])
        ax.axhline(1, color='k', lw=.8); xticks(ax); ax.set_title(f'{ALGO_NAME[a]}, N = {fmt(n)}', fontsize=11)
        ax.set_ylabel('Tương đối so với CUTOFF = 0')
        if i < len(algos) - 1: ax.set_xlabel('')
        if i == 0 and j == 0: ax.legend(fontsize=8)
fig.tight_layout(); fig.savefig(figdir / 'fig3_ratio_all_dist.png', dpi=200); plt.close(fig)

# H4: heatmap tỉ lệ (mỗi thuật toán): hàng = phân bố × n, cột = cutoff
for a in algos:
    g = S[S.algo == a]; idx = [(d, n) for d in DIST_NAME for n in sizes if ((g.distribution == d) & (g.n == n)).any()]
    M = np.array([[g[(g.distribution == d) & (g.n == n) & (g.cutoff == c)].ratio0.mean() for c in cuts] for d, n in idx])
    fig, ax = plt.subplots(figsize=(1 + .55 * len(cuts), .45 * len(idx) + 1.4))
    im = ax.imshow(M, aspect='auto', cmap='RdYlGn_r', vmin=.7, vmax=1.3); ax.grid(False)
    ax.set_xticks(range(len(cuts))); ax.set_xticklabels(cuts); ax.set_yticks(range(len(idx)))
    ax.set_yticklabels([f'{DIST_NAME[d]}, {fmt(n)}' for d, n in idx], fontsize=9)
    for r in range(M.shape[0]):
        for cidx in range(M.shape[1]): ax.text(cidx, r, f'{M[r, cidx]:.2f}', ha='center', va='center', fontsize=6.5)
    ax.set_xlabel('CUTOFF'); ax.set_title(f'{ALGO_NAME[a]}: thời gian tương đối so với CUTOFF = 0 (xanh = nhanh hơn)')
    fig.colorbar(im, ax=ax, fraction=.03); fig.tight_layout(); fig.savefig(figdir / f'fig4_heatmap_{a}.png', dpi=200); plt.close(fig)

# H5: boxplot fork-mean chuẩn hoá tại cutoff 0, 7, 10 và cutoff tốt nhất (dữ liệu ngẫu nhiên)
fig, axes = plt.subplots(len(algos), len(sizes), figsize=(4.2 * len(sizes), 3.4 * len(algos)), squeeze=False)
for i, a in enumerate(algos):
    for j, n in enumerate(sizes):
        ax = axes[i, j]; f = forks[(forks.algo == a) & (forks.n == n) & (forks.distribution == 'random')]
        if f.empty: ax.axis('off'); continue
        cb = int(B[(B.algo == a) & (B.n == n) & (B.distribution == 'random')].best_cutoff.iloc[0])
        sel = sorted({0, 7, 10, cb}); ref = f[f.cutoff == 0].groupby('dataset').time_ns.mean()
        f = f.assign(rel=f.time_ns / f.dataset.map(ref))
        ax.boxplot([f[f.cutoff == c].rel.values for c in sel], tick_labels=[f'{c}' + (' *' if c == cb else '') for c in sel], widths=.55)
        ax.axhline(1, color='k', lw=.8); ax.set_title(f'{ALGO_NAME[a]}, N = {fmt(n)}', fontsize=11)
        ax.set_ylabel('Fork-mean / mean(CUTOFF 0)'); ax.set_xlabel('CUTOFF (* = tốt nhất)')
fig.tight_layout(); fig.savefig(figdir / 'fig5_box_random.png', dpi=200); plt.close(fig)

# H6: so sánh hai thuật toán — speedup tổng hợp theo cutoff (trung bình hình học qua mọi nhóm)
fig, ax = plt.subplots(figsize=(6.4, 4))
for a in algos:
    g = R[R.algo == a].sort_values('cutoff'); ax.plot([pos[c] for c in g.cutoff], g.speedup_pct, marker='o', label=ALGO_NAME[a])
ax.axhline(0, color='k', lw=.8); xticks(ax); ax.set_ylabel('Nhanh hơn CUTOFF = 0 (%)'); ax.legend()
ax.set_title('Tổng hợp mọi dataset: mức cải thiện trung bình hình học')
fig.tight_layout(); fig.savefig(figdir / 'fig6_overall.png', dpi=200); plt.close(fig)

# H7: pilot warm-up
if args.pilot and (args.pilot / 'pilot_all.csv').exists():
    p = pd.read_csv(args.pilot / 'pilot_all.csv'); w = p[p.phase == 'W']
    fig, axes = plt.subplots(1, 2, figsize=(10, 3.8), sharey=False)
    for ax, a in zip(axes, ('merge', 'quick')):
        for ds_, g in w[(w.algo == a) & (w.cutoff == 10)].groupby('dataset'):
            if ds_.startswith('duplicates'): continue
            ref = g[g.it >= 80].ns.median()
            m = g.groupby('it').ns.median() / ref
            ax.plot(m.index, m.values, label=f'{ds_.split("_")[1]} phần tử')
        ax.axhline(1, color='k', lw=.8); ax.set_yscale('log'); ax.set_xlabel('Số thứ tự vòng warm-up')
        ax.set_ylabel('Thời gian / median(vòng 80–99)'); ax.set_title(f'{ALGO_NAME[a]}, CUTOFF = 10'); ax.legend(fontsize=8)
        ax.axvline(30, color='r', ls=':', lw=1)
    fig.tight_layout(); fig.savefig(figdir / 'fig7_pilot_warmup.png', dpi=200); plt.close(fig)

print(B.to_string()); print(R.to_string())
