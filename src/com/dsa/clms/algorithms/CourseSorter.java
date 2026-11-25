package com.dsa.clms.algorithms;

import com.dsa.clms.model.Course;
import com.dsa.clms.model.Course.SortKey;
import com.dsa.clms.model.Course.SortOrder;

/**
 * Collection of 7 classic sorting algorithms implemented purely on Course[] arrays.
 * Every algorithm supports sorting by any Course.SortKey in ASC or DESC order,
 * while measuring comparisons, swaps/writes, passes, and execution time.
 */
public final class CourseSorter {

    public enum Algorithm {
        BUBBLE("Bubble Sort", true, "O(n) best / O(n^2) avg & worst", "O(1)"),
        SELECTION("Selection Sort", false, "O(n^2) best, avg & worst", "O(1)"),
        INSERTION("Insertion Sort", true, "O(n) best / O(n^2) avg & worst", "O(1)"),
        SHELL("Shell Sort (Knuth)", false, "O(n log n) best / O(n^(3/2))", "O(1)"),
        MERGE("Merge Sort", true, "O(n log n) all cases", "O(n)"),
        QUICK("Quick Sort (Median-of-3)", false, "O(n log n) avg / O(n^2) worst", "O(log n)"),
        HEAP("Heap Sort", false, "O(n log n) all cases", "O(1)");

        private final String displayName;
        private final boolean stable;
        private final String timeComplexity;
        private final String spaceComplexity;

        Algorithm(String displayName, boolean stable, String timeComplexity, String spaceComplexity) {
            this.displayName = displayName;
            this.stable = stable;
            this.timeComplexity = timeComplexity;
            this.spaceComplexity = spaceComplexity;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isStable() {
            return stable;
        }

        public String getTimeComplexity() {
            return timeComplexity;
        }

        public String getSpaceComplexity() {
            return spaceComplexity;
        }
    }

    private CourseSorter() {}

    public static SortResult sort(
            Course[] input, Algorithm algorithm, SortKey key, SortOrder order, boolean trace) {
        switch (algorithm) {
            case BUBBLE:
                return bubbleSort(input, key, order, trace);
            case SELECTION:
                return selectionSort(input, key, order, trace);
            case INSERTION:
                return insertionSort(input, key, order, trace);
            case SHELL:
                return shellSort(input, key, order, trace);
            case MERGE:
                return mergeSort(input, key, order, trace);
            case QUICK:
                return quickSort(input, key, order, trace);
            case HEAP:
                return heapSort(input, key, order, trace);
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }

    public static SortResult bubbleSort(Course[] input, SortKey key, SortOrder order, boolean trace) {
        Course[] arr = copyArrayRefs(input);
        int n = arr.length;
        long comparisons = 0;
        long swaps = 0;
        long passes = 0;
        StringBuilder log = trace ? new StringBuilder() : null;

        long start = System.nanoTime();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            passes++;
            for (int j = 0; j < n - 1 - i; j++) {
                comparisons++;
                if (arr[j].compareTo(arr[j + 1], key, order) > 0) {
                    swap(arr, j, j + 1);
                    swaps++;
                    swapped = true;
                }
            }
            if (trace && i < 10) {
                log.append(String.format("  Pass %d complete (total comparisons=%d, swaps=%d)\n",
                        passes, comparisons, swaps));
            }
            if (!swapped) {
                if (trace) {
                    log.append("  Early exit: array already sorted at pass " + passes + "\n");
                }
                break;
            }
        }
        long elapsed = System.nanoTime() - start;

        return new SortResult(
                Algorithm.BUBBLE.getDisplayName(), key, order, n,
                comparisons, swaps, passes, elapsed,
                Algorithm.BUBBLE.isStable(), Algorithm.BUBBLE.getTimeComplexity(),
                Algorithm.BUBBLE.getSpaceComplexity(), arr, logStr(log));
    }

    public static SortResult selectionSort(Course[] input, SortKey key, SortOrder order, boolean trace) {
        Course[] arr = copyArrayRefs(input);
        int n = arr.length;
        long comparisons = 0;
        long swaps = 0;
        long passes = 0;
        StringBuilder log = trace ? new StringBuilder() : null;

        long start = System.nanoTime();
        for (int i = 0; i < n - 1; i++) {
            int extremeIdx = i;
            passes++;
            for (int j = i + 1; j < n; j++) {
                comparisons++;
                if (arr[j].compareTo(arr[extremeIdx], key, order) < 0) {
                    extremeIdx = j;
                }
            }
            if (extremeIdx != i) {
                swap(arr, i, extremeIdx);
                swaps++;
            }
            if (trace && i < 10) {
                log.append(String.format("  Pass %d: placed %s at index %d (selected from [%d..%d])\n",
                        passes, arr[i].getCourseCode(), i, i, n - 1));
            }
        }
        long elapsed = System.nanoTime() - start;

        return new SortResult(
                Algorithm.SELECTION.getDisplayName(), key, order, n,
                comparisons, swaps, passes, elapsed,
                Algorithm.SELECTION.isStable(), Algorithm.SELECTION.getTimeComplexity(),
                Algorithm.SELECTION.getSpaceComplexity(), arr, logStr(log));
    }

    public static SortResult insertionSort(Course[] input, SortKey key, SortOrder order, boolean trace) {
        Course[] arr = copyArrayRefs(input);
        int n = arr.length;
        long comparisons = 0;
        long writes = 0;
        long passes = 0;
        StringBuilder log = trace ? new StringBuilder() : null;

        long start = System.nanoTime();
        for (int i = 1; i < n; i++) {
            Course current = arr[i];
            int j = i - 1;
            passes++;

            while (j >= 0) {
                comparisons++;
                if (arr[j].compareTo(current, key, order) > 0) {
                    arr[j + 1] = arr[j];
                    writes++;
                    j--;
                } else {
                    break;
                }
            }
            arr[j + 1] = current;
            writes++;

            if (trace && i <= 10) {
                log.append(String.format("  Pass %d: inserted %s at index %d\n",
                        passes, current.getCourseCode(), j + 1));
            }
        }
        long elapsed = System.nanoTime() - start;

        return new SortResult(
                Algorithm.INSERTION.getDisplayName(), key, order, n,
                comparisons, writes, passes, elapsed,
                Algorithm.INSERTION.isStable(), Algorithm.INSERTION.getTimeComplexity(),
                Algorithm.INSERTION.getSpaceComplexity(), arr, logStr(log));
    }

    public static SortResult shellSort(Course[] input, SortKey key, SortOrder order, boolean trace) {
        Course[] arr = copyArrayRefs(input);
        int n = arr.length;
        long comparisons = 0;
        long writes = 0;
        long passes = 0;
        StringBuilder log = trace ? new StringBuilder() : null;

        long start = System.nanoTime();
        int gap = 1;
        while (gap < n / 3) {
            gap = 3 * gap + 1; // Knuth sequence: 1, 4, 13, 40, ...
        }

        while (gap >= 1) {
            passes++;
            for (int i = gap; i < n; i++) {
                Course temp = arr[i];
                int j = i;
                while (j >= gap) {
                    comparisons++;
                    if (arr[j - gap].compareTo(temp, key, order) > 0) {
                        arr[j] = arr[j - gap];
                        writes++;
                        j -= gap;
                    } else {
                        break;
                    }
                }
                arr[j] = temp;
                writes++;
            }
            if (trace) {
                log.append(String.format("  Gap=%d pass complete (comparisons=%d, writes=%d)\n",
                        gap, comparisons, writes));
            }
            gap /= 3;
        }
        long elapsed = System.nanoTime() - start;

        return new SortResult(
                Algorithm.SHELL.getDisplayName(), key, order, n,
                comparisons, writes, passes, elapsed,
                Algorithm.SHELL.isStable(), Algorithm.SHELL.getTimeComplexity(),
                Algorithm.SHELL.getSpaceComplexity(), arr, logStr(log));
    }

    public static SortResult mergeSort(Course[] input, SortKey key, SortOrder order, boolean trace) {
        Course[] arr = copyArrayRefs(input);
        int n = arr.length;
        Course[] aux = new Course[n];
        long[] stats = new long[3]; // [0]=comparisons, [1]=writes, [2]=mergeCalls
        StringBuilder log = trace ? new StringBuilder() : null;

        long start = System.nanoTime();
        if (n > 1) {
            mergeSortRec(arr, aux, 0, n - 1, key, order, stats, log, 0);
        }
        long elapsed = System.nanoTime() - start;

        return new SortResult(
                Algorithm.MERGE.getDisplayName(), key, order, n,
                stats[0], stats[1], stats[2], elapsed,
                Algorithm.MERGE.isStable(), Algorithm.MERGE.getTimeComplexity(),
                Algorithm.MERGE.getSpaceComplexity(), arr, logStr(log));
    }

    private static void mergeSortRec(
            Course[] arr, Course[] aux, int left, int right,
            SortKey key, SortOrder order, long[] stats, StringBuilder log, int depth) {
        if (left >= right) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSortRec(arr, aux, left, mid, key, order, stats, log, depth + 1);
        mergeSortRec(arr, aux, mid + 1, right, key, order, stats, log, depth + 1);
        merge(arr, aux, left, mid, right, key, order, stats, log, depth);
    }

    private static void merge(
            Course[] arr, Course[] aux, int left, int mid, int right,
            SortKey key, SortOrder order, long[] stats, StringBuilder log, int depth) {
        stats[2]++;
        for (int k = left; k <= right; k++) {
            aux[k] = arr[k];
        }

        int i = left;
        int j = mid + 1;
        for (int k = left; k <= right; k++) {
            if (i > mid) {
                arr[k] = aux[j++];
                stats[1]++;
            } else if (j > right) {
                arr[k] = aux[i++];
                stats[1]++;
            } else {
                stats[0]++;
                // <= 0 preserves stability for equal keys
                if (aux[i].compareTo(aux[j], key, order) <= 0) {
                    arr[k] = aux[i++];
                } else {
                    arr[k] = aux[j++];
                }
                stats[1]++;
            }
        }
        if (log != null && stats[2] <= 12) {
            log.append(String.format("  Merged [%d..%d] + [%d..%d] (depth=%d)\n",
                    left, mid, mid + 1, right, depth));
        }
    }

    public static SortResult quickSort(Course[] input, SortKey key, SortOrder order, boolean trace) {
        Course[] arr = copyArrayRefs(input);
        int n = arr.length;
        long[] stats = new long[3]; // [0]=comparisons, [1]=swaps, [2]=partitions
        StringBuilder log = trace ? new StringBuilder() : null;

        long start = System.nanoTime();
        if (n > 1) {
            quickSortRec(arr, 0, n - 1, key, order, stats, log);
        }
        long elapsed = System.nanoTime() - start;

        return new SortResult(
                Algorithm.QUICK.getDisplayName(), key, order, n,
                stats[0], stats[1], stats[2], elapsed,
                Algorithm.QUICK.isStable(), Algorithm.QUICK.getTimeComplexity(),
                Algorithm.QUICK.getSpaceComplexity(), arr, logStr(log));
    }

    private static void quickSortRec(
            Course[] arr, int low, int high,
            SortKey key, SortOrder order, long[] stats, StringBuilder log) {
        if (low >= high) {
            return;
        }
        int pivotIndex = partitionMedianOfThree(arr, low, high, key, order, stats, log);
        quickSortRec(arr, low, pivotIndex - 1, key, order, stats, log);
        quickSortRec(arr, pivotIndex + 1, high, key, order, stats, log);
    }

    private static int partitionMedianOfThree(
            Course[] arr, int low, int high,
            SortKey key, SortOrder order, long[] stats, StringBuilder log) {
        stats[2]++;
        int mid = low + (high - low) / 2;

        stats[0]++;
        if (arr[mid].compareTo(arr[low], key, order) < 0) {
            swap(arr, low, mid);
            stats[1]++;
        }
        stats[0]++;
        if (arr[high].compareTo(arr[low], key, order) < 0) {
            swap(arr, low, high);
            stats[1]++;
        }
        stats[0]++;
        if (arr[mid].compareTo(arr[high], key, order) < 0) {
            swap(arr, mid, high);
            stats[1]++;
        }

        // Median is placed at arr[high] as pivot
        Course pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            stats[0]++;
            if (arr[j].compareTo(pivot, key, order) <= 0) {
                i++;
                if (i != j) {
                    swap(arr, i, j);
                    stats[1]++;
                }
            }
        }
        if (i + 1 != high) {
            swap(arr, i + 1, high);
            stats[1]++;
        }
        if (log != null && stats[2] <= 12) {
            log.append(String.format("  Partition [%d..%d] pivot=%s placed at index %d\n",
                    low, high, pivot.getCourseCode(), i + 1));
        }
        return i + 1;
    }

    public static SortResult heapSort(Course[] input, SortKey key, SortOrder order, boolean trace) {
        Course[] arr = copyArrayRefs(input);
        int n = arr.length;
        long[] stats = new long[3]; // [0]=comparisons, [1]=swaps, [2]=passes/extractions
        StringBuilder log = trace ? new StringBuilder() : null;

        long start = System.nanoTime();
        // Build max-heap (relative to SortKey/SortOrder)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i, key, order, stats);
        }
        if (log != null && n > 0) {
            log.append(String.format("  Heap built: root=%s\n", arr[0].getCourseCode()));
        }

        // Extract max element one by one
        for (int i = n - 1; i > 0; i--) {
            stats[2]++;
            swap(arr, 0, i);
            stats[1]++;
            heapify(arr, i, 0, key, order, stats);
            if (log != null && stats[2] <= 10) {
                log.append(String.format("  Extracted %s -> placed at index %d\n",
                        arr[i].getCourseCode(), i));
            }
        }
        long elapsed = System.nanoTime() - start;

        return new SortResult(
                Algorithm.HEAP.getDisplayName(), key, order, n,
                stats[0], stats[1], stats[2], elapsed,
                Algorithm.HEAP.isStable(), Algorithm.HEAP.getTimeComplexity(),
                Algorithm.HEAP.getSpaceComplexity(), arr, logStr(log));
    }

    private static void heapify(
            Course[] arr, int heapSize, int rootIdx,
            SortKey key, SortOrder order, long[] stats) {
        int largest = rootIdx;
        int left = 2 * rootIdx + 1;
        int right = 2 * rootIdx + 2;

        if (left < heapSize) {
            stats[0]++;
            if (arr[left].compareTo(arr[largest], key, order) > 0) {
                largest = left;
            }
        }
        if (right < heapSize) {
            stats[0]++;
            if (arr[right].compareTo(arr[largest], key, order) > 0) {
                largest = right;
            }
        }
        if (largest != rootIdx) {
            swap(arr, rootIdx, largest);
            stats[1]++;
            heapify(arr, heapSize, largest, key, order, stats);
        }
    }

    private static Course[] copyArrayRefs(Course[] input) {
        Course[] copy = new Course[input.length];
        for (int i = 0; i < input.length; i++) {
            copy[i] = input[i];
        }
        return copy;
    }

    private static void swap(Course[] arr, int i, int j) {
        Course temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private static String logStr(StringBuilder sb) {
        return sb == null ? "" : sb.toString();
    }
}
