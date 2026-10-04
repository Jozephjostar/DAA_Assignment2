# DAA Assignment 2: Data Structures Benchmark & Analysis Report

**Course:** Design and Analysis of Algorithms  
**Instructor:** Taubakabyl Nurlybek  
**Student:** Nursultan Maratov  
**Group:** SE-2523  
**Date:** October 2026  

---

## 1. Complexity Analysis Table

| Data Structure | Operation | Best Case | Average Case | Worst Case | Auxiliary Space | Justification |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **DynamicArray** | `add(x)` | $\Theta(1)$ | $\Theta(1)$ | $\Theta(n)$ | $\Theta(1)$ | Amortized $O(1)$ due to doubling capacity; worst case $\Theta(n)$ during resize. |
| **DynamicArray** | `add(index, x)` | $\Theta(1)$ | $\Theta(n)$ | $\Theta(n)$ | $\Theta(1)$ | Adding at head shifts all $n$ elements; adding at tail is $O(1)$ without resize. |
| **DynamicArray** | `remove(index)` | $\Theta(1)$ | $\Theta(n)$ | $\Theta(n)$ | $\Theta(1)$ | Removing index 0 shifts $n-1$ elements left; removing from tail shifts 0 elements. |
| **DynamicArray** | `get(index)` | $\Theta(1)$ | $\Theta(1)$ | $\Theta(1)$ | $\Theta(1)$ | Direct array index access using memory offset formula in constant time. |
| **DynamicArray** | `contains(x)` | $\Theta(1)$ | $\Theta(n)$ | $\Theta(n)$ | $\Theta(1)$ | Best case finds element at index 0; worst case scans all $n$ cells. |
| **MyLinkedList** | `add(x)` | $\Theta(1)$ | $\Theta(1)$ | $\Theta(1)$ | $\Theta(1)$ | Appending at tail updates `tail.next` and `tail` in constant time. |
| **MyLinkedList** | `add(index, x)` | $\Theta(1)$ | $\Theta(n)$ | $\Theta(n)$ | $\Theta(1)$ | Adding at head is $O(1)$; other indices require traversing up to $index-1$ nodes. |
| **MyLinkedList** | `remove(index)` | $\Theta(1)$ | $\Theta(n)$ | $\Theta(n)$ | $\Theta(1)$ | Removing head is $O(1)$; removing other indices requires traversal. |
| **MyLinkedList** | `get(index)` | $\Theta(1)$ | $\Theta(n)$ | $\Theta(n)$ | $\Theta(1)$ | Requires iterating through node references from `head` up to the given index. |
| **MyLinkedList** | `contains(x)` | $\Theta(1)$ | $\Theta(n)$ | $\Theta(n)$ | $\Theta(1)$ | Traverses list node by node from `head` until element is found or list ends. |
| **MinHeap** | `insert(x)` | $\Theta(1)$ | $\Theta(1)$ | $\Theta(\log n)$ | $\Theta(1)$ | Appends at end and bubbles up; tree height is at most $\lfloor \log_2 n \rfloor$. |
| **MinHeap** | `peekMin()` | $\Theta(1)$ | $\Theta(1)$ | $\Theta(1)$ | $\Theta(1)$ | Root element `heap[0]` always holds the minimum value. |
| **MinHeap** | `extractMin()` | $\Theta(1)$ | $\Theta(\log n)$ | $\Theta(\log n)$ | $\Theta(1)$ | Moves last element to root and restores heap property via bubble-down. |
| **MinHeap** | `buildHeap(array)` | $\Theta(n)$ | $\Theta(n)$ | $\Theta(n)$ | $\Theta(1)$ | Bottom-up heapify runs in linear time because most nodes are near the leaves. |

---

## 2. Loop Invariant Proofs

### Proof 1: `DynamicArray.contains(int x)`

```java
for (int i = 0; i < size; i++) {
    if (data[i] == x) {
        return true;
    }
}
return false;
```

* **Invariant:** Before each iteration $i$, the target value $x$ is not present in the subarray `data[0 .. i - 1]`.
* **Initialization:** Before the first iteration ($i = 0$), the subarray `data[0 .. -1]` is empty. Therefore, no match has been found yet, and the invariant holds.
* **Maintenance:** During iteration $i$, we compare `data[i]` with $x$. If `data[i] == x`, we return `true` immediately. If `data[i] != x`, then $x$ is not in `data[0 .. i]`, so the invariant holds before iteration $i + 1$.
* **Termination:** The loop terminates if $x$ is found (returning `true`), or when $i = \text{size}$. When $i = \text{size}$, the invariant guarantees that $x$ is not in `data[0 .. size - 1]` (the entire array). The method then returns `false`.
* **Conclusion:** The method returns `true` if and only if $x$ exists in the array, proving the operation is correct.

---

### Proof 2: `MinHeap.bubbleDown(int i)`

```java
while (true) {
    int left = 2 * i + 1;
    int right = 2 * i + 2;
    int smallest = i;
    if (left < size && heap[left] < heap[smallest]) smallest = left;
    if (right < size && heap[right] < heap[smallest]) smallest = right;
    if (smallest != i) {
        swap(i, smallest);
        i = smallest;
    } else {
        break;
    }
}
```

* **Invariant:** At the start of each iteration, the min-heap property holds throughout the entire tree, except possibly between node $i$ and its immediate children.
* **Initialization:** When `extractMin()` moves the last element to index $0$, both child subtrees of the root remain valid min-heaps. Only the root at $i = 0$ may violate the heap property with its children. Thus, the invariant holds initially.
* **Maintenance:** In each step, we find the smallest value among node $i$ and its children. If node $i$ is not the smallest, we swap it with the smaller child. This makes the parent smaller than both children. Any potential violation is moved down to the swapped child position, preserving the invariant for the next iteration.
* **Termination:** The loop terminates when node $i$ is smaller than both children (or becomes a leaf). In this case, no violations remain.
* **Conclusion:** Every swap moves the larger element one level down until parent $\le$ child holds everywhere, proving that the heap property is restored.

---

## 3. Workload Results & Plots

All benchmarks were run with seed `42` for $n \in \{100, 1\,000, 10\,000, 100\,000\}$, repeating each measurement 5 times after JVM warm-up and recording the median time.

### Workload 1: Random Access (10,000 `get(index)` calls)

![Workload 1 Random Access](results/plots/w1_random_access.png)

* **DynamicArray:** Stays flat at 10,000 steps for all $n$ ($0.02 - 0.04\text{ ms}$). Index access is constant time $O(1)$.
* **MyLinkedList:** For $n = 100\,000$, total steps reach over $500{,}000{,}000$ because accessing index $k$ requires walking from the head. Runtime grows to $801\text{ ms}$ (over 40,000x slower than array).

---

### Workload 2: Search (1,000 `contains(x)` calls)

![Workload 2 Search](results/plots/w2_search.png)

* Both structures perform almost identical numbers of comparisons ($\approx 74.7\text{ million}$ at $n = 100\,000$).
* However, `DynamicArray` takes $23.9\text{ ms}$, while `MyLinkedList` takes $126.8\text{ ms}$ (over 5x slower). This difference is caused by CPU cache locality and pointer chasing.

---

### Workload 3: Insert & Remove (Head and Middle)

![Workload 3 Insert and Remove](results/plots/w3_insert_remove.png)

* **Head (index 0):** `MyLinkedList` only updates pointers in $O(1)$ time ($0.007\text{ ms}$ at $n = 100\,000$). `DynamicArray` must shift all elements on each operation ($201\text{ million}$ moves, $38.6\text{ ms}$). Here, the linked list is over **5,000x faster**.
* **Middle (index $n/2$):** `DynamicArray` shifts elements in contiguous memory ($19.4\text{ ms}$). `MyLinkedList` must traverse $n/2$ nodes to reach the middle for each call, taking $158.8\text{ ms}$.

---

### Workload 4: Priority Processing (`MinHeap`)

![Workload 4 Priority Processing](results/plots/w4_priority_processing.png)

* Inserting $n$ elements and extracting them in order scales as $O(n \log n)$.
* At $n = 100\,000$, `MinHeap` finishes the entire process in just $10.1\text{ ms}$, confirming fast logarithmic behavior.

---

## 4. Bonus Tasks

### Task A: Memory Footprint (JOL)

![Bonus Task A Memory Footprint](results/plots/bonus_a_memory.png)

| Structure | $n = 100$ | $n = 1\,000$ | $n = 10\,000$ | $n = 100\,000$ |
| :--- | :--- | :--- | :--- | :--- |
| **DynamicArray** | $424\text{ B}$ | $4{,}024\text{ B}$ | $0.038\text{ MB}$ | **$0.382\text{ MB}$** |
| **MinHeap** | $424\text{ B}$ | $4{,}024\text{ B}$ | $0.038\text{ MB}$ | **$0.382\text{ MB}$** |
| **MyLinkedList** | $2{,}424\text{ B}$ | $24{,}024\text{ B}$ | $0.229\text{ MB}$ | **$2.289\text{ MB}$** |

* **Why Node Objects create 6x memory overhead:**
  * In `DynamicArray` and `MinHeap`, each integer takes 4 bytes in a flat primitive array.
  * In `MyLinkedList`, every element requires a `Node` object: 12 bytes object header + 4 bytes int value + 4 bytes `next` pointer + 4 bytes padding = **24 bytes per node**.
  * As measured by JOL, `MyLinkedList` uses exactly **6 times more memory** than `DynamicArray` ($2.289\text{ MB}$ vs $0.382\text{ MB}$ for $n = 100\,000$).

---

### Task B: Floyd's Bottom-Up `buildHeap` ($O(n)$) vs $N$ Inserts ($O(n \log n)$)

![Bonus Task B Build Heap](results/plots/bonus_b_build_heap.png)

| Size $n$ | $N$ Inserts Time | $N$ Inserts Comparisons | Floyd's $O(n)$ Time | Floyd's Comparisons | Speedup |
| :--- | :--- | :--- | :--- | :--- | :--- |
| $100$ | $0.003\text{ ms}$ | $194$ | $0.005\text{ ms}$ | $184$ | $0.6\times$ |
| $1\,000$ | $0.028\text{ ms}$ | $2{,}232$ | $0.056\text{ ms}$ | $1{,}857$ | $0.5\times$ |
| $10\,000$ | $0.308\text{ ms}$ | $22{,}593$ | $0.154\text{ ms}$ | $18{,}740$ | **$2.0\times$** |
| $100\,000$ | $1.660\text{ ms}$ | $227{,}662$ | $0.802\text{ ms}$ | $188{,}424$ | **$2.1\times$** |

* **Explanation:**
  * In $N$ separate inserts, elements are added at the bottom and bubble up toward the root through up to $\log n$ levels, leading to $O(n \log n)$ total work.
  * Floyd's algorithm starts from the lowest internal nodes ($(n/2) - 1$) and sifts them down. Because half of the nodes in a binary heap are leaves (doing 0 comparisons) and most remaining nodes are near the bottom, the majority of nodes only do 1 or 2 comparisons.
  * This makes Floyd's algorithm run in linear $O(n)$ time, running more than **2x faster** at $n = 100\,000$ ($0.802\text{ ms}$ vs $1.660\text{ ms}$).
