import os
import matplotlib.pyplot as plt
import pandas as pd

# Set clean aesthetic styling
plt.style.use('seaborn-v0_8-whitegrid' if 'seaborn-v0_8-whitegrid' in plt.style.available else 'default')
plt.rcParams['font.sans-serif'] = 'Helvetica', 'Arial', 'DejaVu Sans'
plt.rcParams['axes.edgecolor'] = '#cccccc'
plt.rcParams['axes.linewidth'] = 0.8

RESULTS_DIR = 'results'
PLOTS_DIR = os.path.join(RESULTS_DIR, 'plots')
os.makedirs(PLOTS_DIR, exist_ok=True)

df = pd.read_csv(os.path.join(RESULTS_DIR, 'results.csv'))

# -------------------------------------------------------------
# WORKLOAD 1: Random Access
# -------------------------------------------------------------
w1 = df[df['workload'] == 'W1']
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(13, 5))

for struct, color, marker in [('DynamicArray', '#1f77b4', 'o'), ('MyLinkedList', '#d62728', 's')]:
    sub = w1[w1['structure'] == struct].sort_values('n')
    ax1.plot(sub['n'], sub['time_ms'], marker=marker, color=color, linewidth=2, markersize=7, label=struct)
    ax2.plot(sub['n'], sub['steps'], marker=marker, color=color, linewidth=2, markersize=7, label=struct)

ax1.set_xscale('log')
ax1.set_yscale('log')
ax1.set_title('W1: Random Access - Execution Time vs n', fontsize=12, fontweight='bold')
ax1.set_xlabel('Structure Size (n)', fontsize=11)
ax1.set_ylabel('Execution Time (ms, log scale)', fontsize=11)
ax1.grid(True, linestyle='--', alpha=0.6)
ax1.legend(fontsize=10)

ax2.set_xscale('log')
ax2.set_yscale('log')
ax2.set_title('W1: Random Access - Traversal Steps vs n', fontsize=12, fontweight='bold')
ax2.set_xlabel('Structure Size (n)', fontsize=11)
ax2.set_ylabel('Total Steps (log scale)', fontsize=11)
ax2.grid(True, linestyle='--', alpha=0.6)
ax2.legend(fontsize=10)

plt.tight_layout()
plt.savefig(os.path.join(PLOTS_DIR, 'w1_random_access.png'), dpi=300)
plt.close()
print("Generated w1_random_access.png")

# -------------------------------------------------------------
# WORKLOAD 2: Search (contains)
# -------------------------------------------------------------
w2 = df[df['workload'] == 'W2']
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(13, 5))

for struct, color, marker in [('DynamicArray', '#1f77b4', 'o'), ('MyLinkedList', '#d62728', 's')]:
    sub = w2[w2['structure'] == struct].sort_values('n')
    ax1.plot(sub['n'], sub['time_ms'], marker=marker, color=color, linewidth=2, markersize=7, label=struct)
    ax2.plot(sub['n'], sub['comparisons'], marker=marker, color=color, linewidth=2, markersize=7, label=f"{struct} (Comparisons)")

ax1.set_xscale('log')
ax1.set_yscale('log')
ax1.set_title('W2: Search - Execution Time vs n', fontsize=12, fontweight='bold')
ax1.set_xlabel('Structure Size (n)', fontsize=11)
ax1.set_ylabel('Execution Time (ms, log scale)', fontsize=11)
ax1.grid(True, linestyle='--', alpha=0.6)
ax1.legend(fontsize=10)

ax2.set_xscale('log')
ax2.set_yscale('log')
ax2.set_title('W2: Search - Element Comparisons vs n', fontsize=12, fontweight='bold')
ax2.set_xlabel('Structure Size (n)', fontsize=11)
ax2.set_ylabel('Comparisons (log scale)', fontsize=11)
ax2.grid(True, linestyle='--', alpha=0.6)
ax2.legend(fontsize=10)

plt.tight_layout()
plt.savefig(os.path.join(PLOTS_DIR, 'w2_search.png'), dpi=300)
plt.close()
print("Generated w2_search.png")

# -------------------------------------------------------------
# WORKLOAD 3: Insert & Remove (Head and Middle)
# -------------------------------------------------------------
w3 = df[df['workload'] == 'W3']
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(13, 5))

# Head vs Middle Time
for variant, linestyle in [('head', '-'), ('middle', '--')]:
    for struct, color, marker in [('DynamicArray', '#1f77b4', 'o'), ('MyLinkedList', '#d62728', 's')]:
        sub = w3[(w3['variant'] == variant) & (w3['structure'] == struct)].sort_values('n')
        label = f"{struct} ({variant})"
        ax1.plot(sub['n'], sub['time_ms'], marker=marker, linestyle=linestyle, color=color,
                 linewidth=2, markersize=6, label=label)

ax1.set_xscale('log')
ax1.set_yscale('log')
ax1.set_title('W3: Insert & Remove - Execution Time vs n', fontsize=12, fontweight='bold')
ax1.set_xlabel('Structure Size (n)', fontsize=11)
ax1.set_ylabel('Execution Time (ms, log scale)', fontsize=11)
ax1.grid(True, linestyle='--', alpha=0.6)
ax1.legend(fontsize=9)

# Operations (Moves & Steps)
for variant, linestyle in [('head', '-'), ('middle', '--')]:
    for struct, color, marker in [('DynamicArray', '#1f77b4', 'o'), ('MyLinkedList', '#d62728', 's')]:
        sub = w3[(w3['variant'] == variant) & (w3['structure'] == struct)].sort_values('n')
        # Total operations = moves + steps
        total_ops = sub['moves'] + sub['steps']
        label = f"{struct} ({variant})"
        ax2.plot(sub['n'], total_ops, marker=marker, linestyle=linestyle, color=color,
                 linewidth=2, markersize=6, label=label)

ax2.set_xscale('log')
ax2.set_yscale('log')
ax2.set_title('W3: Insert & Remove - Physical Ops (Moves+Steps) vs n', fontsize=12, fontweight='bold')
ax2.set_xlabel('Structure Size (n)', fontsize=11)
ax2.set_ylabel('Operations (Moves + Steps, log scale)', fontsize=11)
ax2.grid(True, linestyle='--', alpha=0.6)
ax2.legend(fontsize=9)

plt.tight_layout()
plt.savefig(os.path.join(PLOTS_DIR, 'w3_insert_remove.png'), dpi=300)
plt.close()
print("Generated w3_insert_remove.png")

# -------------------------------------------------------------
# WORKLOAD 4: Priority Processing (MinHeap)
# -------------------------------------------------------------
w4 = df[df['workload'] == 'W4'].sort_values('n')
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(13, 5))

ax1.plot(w4['n'], w4['time_ms'], marker='^', color='#2ca02c', linewidth=2, markersize=7, label='MinHeap Total Time')
ax1.set_xscale('log')
ax1.set_yscale('log')
ax1.set_title('W4: Priority Processing - Time vs n', fontsize=12, fontweight='bold')
ax1.set_xlabel('Structure Size (n)', fontsize=11)
ax1.set_ylabel('Execution Time (ms, log scale)', fontsize=11)
ax1.grid(True, linestyle='--', alpha=0.6)
ax1.legend(fontsize=10)

ax2.plot(w4['n'], w4['comparisons'], marker='o', color='#9467bd', linewidth=2, markersize=7, label='Comparisons')
ax2.plot(w4['n'], w4['moves'], marker='s', color='#ff7f0e', linewidth=2, markersize=7, label='Moves (Swaps * 2)')
ax2.set_xscale('log')
ax2.set_yscale('log')
ax2.set_title('W4: Priority Processing - Physical Ops vs n', fontsize=12, fontweight='bold')
ax2.set_xlabel('Structure Size (n)', fontsize=11)
ax2.set_ylabel('Count (log scale)', fontsize=11)
ax2.grid(True, linestyle='--', alpha=0.6)
ax2.legend(fontsize=10)

plt.tight_layout()
plt.savefig(os.path.join(PLOTS_DIR, 'w4_priority_processing.png'), dpi=300)
plt.close()
print("Generated w4_priority_processing.png")

# -------------------------------------------------------------
# BONUS TASK A: Memory Footprint (JOL)
# -------------------------------------------------------------
if os.path.exists(os.path.join(RESULTS_DIR, 'memory.csv')):
    mem_df = pd.read_csv(os.path.join(RESULTS_DIR, 'memory.csv'))
    plt.figure(figsize=(8, 5))
    colors = {'DynamicArray': '#1f77b4', 'MyLinkedList': '#d62728', 'MinHeap': '#2ca02c'}
    markers = {'DynamicArray': 'o', 'MyLinkedList': 's', 'MinHeap': '^'}

    for struct in ['DynamicArray', 'MinHeap', 'MyLinkedList']:
        sub = mem_df[mem_df['structure'] == struct].sort_values('n')
        plt.plot(sub['n'], sub['megabytes'], marker=markers[struct], color=colors[struct],
                 linewidth=2, markersize=7, label=struct)

    plt.xscale('log')
    plt.yscale('log')
    plt.title('Bonus Task A: Memory Footprint (JOL) vs n', fontsize=12, fontweight='bold')
    plt.xlabel('Structure Size (n)', fontsize=11)
    plt.ylabel('Memory Consumption (MB, log scale)', fontsize=11)
    plt.grid(True, linestyle='--', alpha=0.6)
    plt.legend(fontsize=10)
    plt.tight_layout()
    plt.savefig(os.path.join(PLOTS_DIR, 'bonus_a_memory.png'), dpi=300)
    plt.close()
    print("Generated bonus_a_memory.png")

# -------------------------------------------------------------
# BONUS TASK B: Floyd's buildHeap vs N-Inserts
# -------------------------------------------------------------
if os.path.exists(os.path.join(RESULTS_DIR, 'heap_build.csv')):
    b_df = pd.read_csv(os.path.join(RESULTS_DIR, 'heap_build.csv'))
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(13, 5))

    for method, color, marker in [('N_Inserts', '#ff7f0e', 'o'), ('Floyd_O(n)', '#2ca02c', '^')]:
        sub = b_df[b_df['method'] == method].sort_values('n')
        label = "N Inserts O(n log n)" if method == 'N_Inserts' else "Floyd's buildHeap O(n)"
        ax1.plot(sub['n'], sub['time_ms'], marker=marker, color=color, linewidth=2, markersize=7, label=label)
        ax2.plot(sub['n'], sub['comparisons'], marker=marker, color=color, linewidth=2, markersize=7, label=label)

    ax1.set_xscale('log')
    ax1.set_yscale('log')
    ax1.set_title("Bonus B: Heap Construction Time vs n", fontsize=12, fontweight='bold')
    ax1.set_xlabel('Structure Size (n)', fontsize=11)
    ax1.set_ylabel('Execution Time (ms, log scale)', fontsize=11)
    ax1.grid(True, linestyle='--', alpha=0.6)
    ax1.legend(fontsize=10)

    ax2.set_xscale('log')
    ax2.set_yscale('log')
    ax2.set_title("Bonus B: Heap Construction Comparisons vs n", fontsize=12, fontweight='bold')
    ax2.set_xlabel('Structure Size (n)', fontsize=11)
    ax2.set_ylabel('Comparisons (log scale)', fontsize=11)
    ax2.grid(True, linestyle='--', alpha=0.6)
    ax2.legend(fontsize=10)

    plt.tight_layout()
    plt.savefig(os.path.join(PLOTS_DIR, 'bonus_b_build_heap.png'), dpi=300)
    plt.close()
    print("Generated bonus_b_build_heap.png")

print("All plots generated successfully in results/plots/!")
