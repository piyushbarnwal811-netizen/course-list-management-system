# Course List Management System (DSA Project in Java)

A comprehensive, modular **Data Structures & Algorithms (DSA)** project built in **Java** implementing a **Course List Management System** on top of a custom raw-array ADT (`CourseArray`) — without using `java.util.ArrayList`, `Arrays.sort`, or `Collections.sort`.

## Project Architecture

```text
src/com/dsa/clms/
├── Main.java                       # Application entry point
├── model/
│   └── Course.java                 # Course entity + multi-key comparator + CSV codec
├── ds/
│   └── CourseArray.java            # Custom array-backed ADT (manual shifts, 2x resize, stats)
├── algorithms/
│   ├── CourseSearcher.java         # 9 array searching modes with comparisons & trace logs
│   ├── CourseSorter.java           # 7 sorting algorithms with comparisons, swaps & trace logs
│   ├── SearchResult.java           # Search metrics & matched course results
│   ├── SortResult.java             # Sort metrics & execution snapshot
│   └── AlgorithmBenchmark.java     # Side-by-side sort/search benchmarker & dataset generator
├── data/
│   ├── SampleCourseData.java       # 24 preloaded university courses (unsorted initially)
│   └── CsvStorage.java             # Save/load CourseArray to/from CSV
└── ui/
    ├── ConsoleUI.java              # Formatted ASCII tables, metrics reports & input validation
    └── MenuController.java         # Interactive menu-driven CLI controller

test/com/dsa/clms/
└── CourseManagementTest.java       # Automated test suite for ADT, Search, Sort & CSV
```

## Key Features

### 1. Custom Array ADT (`CourseArray`)
- Backed directly by `Course[]` with configurable **auto-resize (2x doubling)** or **fixed-capacity mode**.
- **Insert at End** ($O(1)$ amortized), **Insert at Index** ($O(n)$ with right-shift counter), **Delete by Index / Code** ($O(n)$ with left-shift counter), **Update at Index** ($O(1)$).
- **In-place Array Reversal** ($O(n)$ time, $O(1)$ space) and **Manual Capacity Resizing**.
- Tracks live **load factor**, **resize count**, **total insertions/deletions**, and **shifts in last operation**.

### 2. Searching Algorithms (`CourseSearcher`)
Includes optional **Step-by-Step Trace Mode** and exact **comparison counters**:
- **Linear Search** (by Course Code) — $O(n)$, works on unsorted arrays.
- **Sentinel Linear Search** — $O(n)$, removes per-iteration bounds checks using a sentinel at `arr[n]`.
- **Binary Search (Iterative & Recursive)** — $O(\log n)$, requires code-sorted array.
- **Jump Search** — $O(\sqrt{n})$, block jumps of $\lfloor\sqrt{n}\rfloor$.
- **Exponential Search** — $O(\log i)$, exponential range probing followed by binary search.
- **Interpolation Search** (by numeric Course ID) — $O(\log\log n)$ average on uniform IDs.
- **Multi-Match Keyword Filter** — scans Code, Title, Department, and Instructor.
- **Credit Range Search** — automatically uses $O(\log n + k)$ binary lower/upper bounds if sorted by credits, or $O(n)$ linear scan otherwise.

### 3. Sorting Algorithms (`CourseSorter`)
Supports sorting by **7 keys** (`Course ID`, `Course Code`, `Title`, `Department`, `Credits`, `Enrolled`, `Rating`) in **Ascending** or **Descending** order:

| Algorithm | Best | Average | Worst | Extra Space | Stable |
| :--- | :--- | :--- | :--- | :--- | :--- |
| Bubble Sort (Early Exit) | $O(n)$ | $O(n^2)$ | $O(n^2)$ | $O(1)$ | Yes |
| Selection Sort | $O(n^2)$ | $O(n^2)$ | $O(n^2)$ | $O(1)$ | No |
| Insertion Sort | $O(n)$ | $O(n^2)$ | $O(n^2)$ | $O(1)$ | Yes |
| Shell Sort (Knuth Gaps) | $O(n \log n)$ | $O(n^{3/2})$ | $O(n^{3/2})$ | $O(1)$ | No |
| Merge Sort | $O(n \log n)$ | $O(n \log n)$ | $O(n \log n)$ | $O(n)$ | Yes |
| Quick Sort (Median-of-3) | $O(n \log n)$ | $O(n \log n)$ | $O(n^2)$ | $O(\log n)$ | No |
| Heap Sort | $O(n \log n)$ | $O(n \log n)$ | $O(n \log n)$ | $O(1)$ | No |

### 4. Benchmark Lab, Analytics & CSV Persistence
- **Benchmark Lab**: Compare all 7 sorting algorithms or all 6 code-search algorithms side-by-side on the live dataset or synthetic datasets up to 15,000 courses.
- **Catalog Analytics**: Department breakdown, average credits/ratings, top-rated & highest-enrolled courses via raw array traversal.
- **CSV Save/Load**: Export or import course lists (`data/courses.csv`).

## How to Compile & Run (Windows PowerShell)

```powershell
# 1. Create output directory and compile source + tests
New-Item -ItemType Directory -Force -Path out | Out-Null
$sources = Get-ChildItem -Recurse -Filter *.java src,test | ForEach-Object { $_.FullName }
javac -d out $sources

# 2. Run automated test suite
java -cp out com.dsa.clms.CourseManagementTest

# 3. Launch the interactive Course List Management System
java -cp out com.dsa.clms.Main
```
