package com.dsa.clms.algorithms;

import com.dsa.clms.model.Course;

/**
 * Collection of classic array searching algorithms implemented from scratch on Course[].
 * Includes single-target exact searches, sorted-array searches, and multi-match range/keyword searches.
 */
public final class CourseSearcher {

    private CourseSearcher() {}

    /**
     * Standard Linear Search by Course Code. Works on unsorted arrays.
     * Time Complexity: O(n), Space: O(1).
     */
    public static SearchResult linearSearchByCode(Course[] arr, String courseCode, boolean trace) {
        String target = normalize(courseCode);
        StringBuilder log = trace ? new StringBuilder() : null;
        long comparisons = 0;
        int foundIdx = -1;

        long start = System.nanoTime();
        for (int i = 0; i < arr.length; i++) {
            comparisons++;
            if (trace) {
                log.append(String.format("  [Step %d] Index %d -> %s == %s ? ",
                        comparisons, i, arr[i].getCourseCode(), target));
            }
            if (arr[i].getCourseCode().equalsIgnoreCase(target)) {
                foundIdx = i;
                if (trace) {
                    log.append("MATCH!\n");
                }
                break;
            }
            if (trace) {
                log.append("No\n");
            }
        }
        long elapsed = System.nanoTime() - start;

        return buildSingleResult(
                "Linear Search",
                "Course Code = " + target,
                arr,
                foundIdx,
                comparisons,
                elapsed,
                "O(n)",
                "Works on unsorted array",
                log);
    }

    /**
     * Sentinel Linear Search by Course Code.
     * Places target sentinel at index n to eliminate loop bounds check (i < n) on every iteration.
     * Time Complexity: O(n), Space: O(1) extra on in-place buffer (O(n) copy here to keep caller array immutable).
     */
    public static SearchResult sentinelLinearSearchByCode(Course[] arr, String courseCode, boolean trace) {
        String target = normalize(courseCode);
        StringBuilder log = trace ? new StringBuilder() : null;
        long comparisons = 0;
        int foundIdx = -1;

        if (arr.length > 0) {
            Course[] sentinelArr = new Course[arr.length + 1];
            for (int i = 0; i < arr.length; i++) {
                sentinelArr[i] = arr[i];
            }
            sentinelArr[arr.length] = new Course(-1, target, "SENTINEL", "", "", 0, 0, 0, 0, 0.0);

            long start = System.nanoTime();
            int i = 0;
            while (true) {
                comparisons++;
                if (trace) {
                    log.append(String.format("  [Step %d] Index %d (%s)\n",
                            comparisons, i, i == arr.length ? "SENTINEL" : sentinelArr[i].getCourseCode()));
                }
                if (sentinelArr[i].getCourseCode().equalsIgnoreCase(target)) {
                    break;
                }
                i++;
            }
            long elapsed = System.nanoTime() - start;

            if (i < arr.length) {
                foundIdx = i;
            }
            return buildSingleResult(
                    "Sentinel Linear Search",
                    "Course Code = " + target,
                    arr,
                    foundIdx,
                    comparisons,
                    elapsed,
                    "O(n)",
                    "Eliminates per-step index bounds check using sentinel at arr[n]",
                    log);
        }

        return buildSingleResult(
                "Sentinel Linear Search",
                "Course Code = " + target,
                arr,
                -1,
                0,
                0,
                "O(n)",
                "Empty array",
                log);
    }

    /**
     * Iterative Binary Search by Course Code.
     * Precondition: arr MUST be sorted ascending by Course Code.
     * Time Complexity: O(log n), Space: O(1).
     */
    public static SearchResult binarySearchByCodeIterative(Course[] arr, String courseCode, boolean trace) {
        String target = normalize(courseCode);
        StringBuilder log = trace ? new StringBuilder() : null;
        long comparisons = 0;
        int foundIdx = -1;

        int low = 0;
        int high = arr.length - 1;

        long start = System.nanoTime();
        while (low <= high) {
            int mid = low + (high - low) / 2;
            comparisons++;
            int cmp = arr[mid].getCourseCode().compareToIgnoreCase(target);
            if (trace) {
                log.append(String.format("  [Step %d] low=%d, mid=%d (%s), high=%d -> cmp=%d\n",
                        comparisons, low, mid, arr[mid].getCourseCode(), high, cmp));
            }
            if (cmp == 0) {
                foundIdx = mid;
                break;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        long elapsed = System.nanoTime() - start;

        return buildSingleResult(
                "Binary Search (Iterative)",
                "Course Code = " + target,
                arr,
                foundIdx,
                comparisons,
                elapsed,
                "O(log n)",
                "Requires array sorted ascending by Course Code",
                log);
    }

    /**
     * Recursive Binary Search by Course Code.
     * Precondition: arr MUST be sorted ascending by Course Code.
     * Time Complexity: O(log n), Space: O(log n) recursion stack.
     */
    public static SearchResult binarySearchByCodeRecursive(Course[] arr, String courseCode, boolean trace) {
        String target = normalize(courseCode);
        StringBuilder log = trace ? new StringBuilder() : null;
        long[] comparisons = new long[1];

        long start = System.nanoTime();
        int foundIdx = binaryRecursiveHelper(arr, target, 0, arr.length - 1, comparisons, log);
        long elapsed = System.nanoTime() - start;

        return buildSingleResult(
                "Binary Search (Recursive)",
                "Course Code = " + target,
                arr,
                foundIdx,
                comparisons[0],
                elapsed,
                "O(log n)",
                "Divide & conquer recursion; O(log n) call stack",
                log);
    }

    private static int binaryRecursiveHelper(
            Course[] arr, String target, int low, int high, long[] comparisons, StringBuilder log) {
        if (low > high) {
            return -1;
        }
        int mid = low + (high - low) / 2;
        comparisons[0]++;
        int cmp = arr[mid].getCourseCode().compareToIgnoreCase(target);
        if (log != null) {
            log.append(String.format("  [Step %d] Recurse [%d..%d], mid=%d (%s), cmp=%d\n",
                    comparisons[0], low, high, mid, arr[mid].getCourseCode(), cmp));
        }
        if (cmp == 0) {
            return mid;
        } else if (cmp < 0) {
            return binaryRecursiveHelper(arr, target, mid + 1, high, comparisons, log);
        } else {
            return binaryRecursiveHelper(arr, target, low, mid - 1, comparisons, log);
        }
    }

    /**
     * Jump Search by Course Code on a sorted array.
     * Jumps in blocks of sqrt(n), then performs linear scan inside the candidate block.
     * Time Complexity: O(sqrt(n)), Space: O(1).
     */
    public static SearchResult jumpSearchByCode(Course[] arr, String courseCode, boolean trace) {
        String target = normalize(courseCode);
        StringBuilder log = trace ? new StringBuilder() : null;
        long comparisons = 0;
        int foundIdx = -1;
        int n = arr.length;

        long start = System.nanoTime();
        if (n > 0) {
            int step = Math.max(1, (int) Math.floor(Math.sqrt(n)));
            int prev = 0;
            int blockEnd = Math.min(step, n) - 1;

            while (prev < n) {
                comparisons++;
                int cmp = arr[blockEnd].getCourseCode().compareToIgnoreCase(target);
                if (trace) {
                    log.append(String.format("  [Jump] Block [%d..%d], checking arr[%d]=%s\n",
                            prev, blockEnd, blockEnd, arr[blockEnd].getCourseCode()));
                }
                if (cmp >= 0) {
                    break;
                }
                prev += step;
                if (prev >= n) {
                    break;
                }
                blockEnd = Math.min(prev + step, n) - 1;
            }

            int limit = Math.min(prev + step, n);
            for (int i = prev; i < limit; i++) {
                comparisons++;
                int cmp = arr[i].getCourseCode().compareToIgnoreCase(target);
                if (trace) {
                    log.append(String.format("  [Linear in block] Index %d (%s)\n",
                            i, arr[i].getCourseCode()));
                }
                if (cmp == 0) {
                    foundIdx = i;
                    break;
                }
                if (cmp > 0) {
                    break;
                }
            }
        }
        long elapsed = System.nanoTime() - start;

        return buildSingleResult(
                "Jump Search",
                "Course Code = " + target,
                arr,
                foundIdx,
                comparisons,
                elapsed,
                "O(sqrt(n))",
                "Optimal block size = floor(sqrt(n)); requires sorted array",
                log);
    }

    /**
     * Exponential Search by Course Code on a sorted array.
     * Finds range [bound/2 .. min(bound, n-1)] via powers of 2, then runs Binary Search.
     * Time Complexity: O(log i) where i is target position, Space: O(1).
     */
    public static SearchResult exponentialSearchByCode(Course[] arr, String courseCode, boolean trace) {
        String target = normalize(courseCode);
        StringBuilder log = trace ? new StringBuilder() : null;
        long comparisons = 0;
        int foundIdx = -1;
        int n = arr.length;

        long start = System.nanoTime();
        if (n > 0) {
            comparisons++;
            int firstCmp = arr[0].getCourseCode().compareToIgnoreCase(target);
            if (trace) {
                log.append(String.format("  [Step 1] Check index 0 (%s)\n", arr[0].getCourseCode()));
            }
            if (firstCmp == 0) {
                foundIdx = 0;
            } else if (firstCmp < 0) {
                int bound = 1;
                while (bound < n) {
                    comparisons++;
                    int cmp = arr[bound].getCourseCode().compareToIgnoreCase(target);
                    if (trace) {
                        log.append(String.format("  [Probe] bound=%d (%s)\n",
                                bound, arr[bound].getCourseCode()));
                    }
                    if (cmp >= 0) {
                        break;
                    }
                    bound *= 2;
                }

                int low = bound / 2;
                int high = Math.min(bound, n - 1);
                while (low <= high) {
                    int mid = low + (high - low) / 2;
                    comparisons++;
                    int cmp = arr[mid].getCourseCode().compareToIgnoreCase(target);
                    if (trace) {
                        log.append(String.format("  [Binary] [%d..%d] mid=%d (%s)\n",
                                low, high, mid, arr[mid].getCourseCode()));
                    }
                    if (cmp == 0) {
                        foundIdx = mid;
                        break;
                    } else if (cmp < 0) {
                        low = mid + 1;
                    } else {
                        high = mid - 1;
                    }
                }
            }
        }
        long elapsed = System.nanoTime() - start;

        return buildSingleResult(
                "Exponential Search",
                "Course Code = " + target,
                arr,
                foundIdx,
                comparisons,
                elapsed,
                "O(log i)",
                "Doubles search bound exponentially, then binary searches bounded range",
                log);
    }

    /**
     * Interpolation Search by numeric Course ID.
     * Precondition: arr MUST be sorted ascending by COURSE_ID.
     * Time Complexity: O(log log n) average for uniform IDs, O(n) worst case.
     */
    public static SearchResult interpolationSearchById(Course[] arr, int targetId, boolean trace) {
        StringBuilder log = trace ? new StringBuilder() : null;
        long comparisons = 0;
        int foundIdx = -1;

        int low = 0;
        int high = arr.length - 1;

        long start = System.nanoTime();
        while (low <= high
                && arr.length > 0
                && targetId >= arr[low].getCourseId()
                && targetId <= arr[high].getCourseId()) {

            if (low == high) {
                comparisons++;
                if (trace) {
                    log.append(String.format("  [Probe] Single index %d (ID=%d)\n",
                            low, arr[low].getCourseId()));
                }
                if (arr[low].getCourseId() == targetId) {
                    foundIdx = low;
                }
                break;
            }

            int lowVal = arr[low].getCourseId();
            int highVal = arr[high].getCourseId();
            if (highVal == lowVal) {
                comparisons++;
                if (arr[low].getCourseId() == targetId) {
                    foundIdx = low;
                }
                break;
            }

            long numerator = (long) (targetId - lowVal) * (high - low);
            int pos = low + (int) (numerator / (highVal - lowVal));
            if (pos < low) pos = low;
            if (pos > high) pos = high;

            comparisons++;
            int posVal = arr[pos].getCourseId();
            if (trace) {
                log.append(String.format("  [Probe %d] low=%d(%d), high=%d(%d) -> estimated pos=%d(ID=%d)\n",
                        comparisons, low, lowVal, high, highVal, pos, posVal));
            }

            if (posVal == targetId) {
                foundIdx = pos;
                break;
            } else if (posVal < targetId) {
                low = pos + 1;
            } else {
                high = pos - 1;
            }
        }
        long elapsed = System.nanoTime() - start;

        return buildSingleResult(
                "Interpolation Search",
                "Course ID = " + targetId,
                arr,
                foundIdx,
                comparisons,
                elapsed,
                "O(log log n) avg / O(n) worst",
                "Probes proportionally to numeric ID distribution; requires sorted by Course ID",
                log);
    }

    /**
     * Multi-match keyword search across Title, Code, Department, or Instructor.
     * Time Complexity: O(n), Space: O(k) for k matches.
     */
    public static SearchResult keywordSearch(Course[] arr, String keyword, boolean trace) {
        String q = normalize(keyword).toLowerCase();
        StringBuilder log = trace ? new StringBuilder() : null;
        long comparisons = 0;

        int[] tempIndices = new int[arr.length];
        int matchCount = 0;

        long start = System.nanoTime();
        for (int i = 0; i < arr.length; i++) {
            comparisons++;
            Course c = arr[i];
            boolean matched = !q.isEmpty() && (
                    c.getTitle().toLowerCase().contains(q)
                            || c.getCourseCode().toLowerCase().contains(q)
                            || c.getDepartment().toLowerCase().contains(q)
                            || c.getInstructor().toLowerCase().contains(q));
            if (matched) {
                tempIndices[matchCount++] = i;
                if (trace) {
                    log.append(String.format("  [Match] Index %d -> %s: %s\n", i, c.getCourseCode(), c.getTitle()));
                }
            }
        }
        long elapsed = System.nanoTime() - start;

        int[] indices = new int[matchCount];
        Course[] courses = new Course[matchCount];
        for (int i = 0; i < matchCount; i++) {
            indices[i] = tempIndices[i];
            courses[i] = arr[indices[i]];
        }

        return new SearchResult(
                "Linear Keyword Filter",
                "Keyword contains '" + keyword.trim() + "'",
                matchCount > 0 ? indices[0] : -1,
                indices,
                courses,
                comparisons,
                elapsed,
                "O(n)",
                "Matches across code, title, department, and instructor",
                log == null ? "" : log.toString());
    }

    /**
     * Credit range search: uses Binary Search bounds if already sorted ascending by CREDITS,
     * otherwise performs a linear scan.
     */
    public static SearchResult rangeSearchByCredits(
            Course[] arr, int minCredits, int maxCredits, boolean sortedAscByCredits, boolean trace) {
        StringBuilder log = trace ? new StringBuilder() : null;
        long comparisons = 0;
        long start = System.nanoTime();

        if (sortedAscByCredits && arr.length > 0) {
            // Lower bound: first index where credits >= minCredits
            int low = 0, high = arr.length;
            while (low < high) {
                int mid = low + (high - low) / 2;
                comparisons++;
                if (arr[mid].getCredits() < minCredits) {
                    low = mid + 1;
                } else {
                    high = mid;
                }
            }
            int lowerIdx = low;

            // Upper bound: first index where credits > maxCredits
            low = lowerIdx;
            high = arr.length;
            while (low < high) {
                int mid = low + (high - low) / 2;
                comparisons++;
                if (arr[mid].getCredits() <= maxCredits) {
                    low = mid + 1;
                } else {
                    high = mid;
                }
            }
            int upperIdx = low;

            int count = Math.max(0, upperIdx - lowerIdx);
            int[] indices = new int[count];
            Course[] courses = new Course[count];
            for (int i = 0; i < count; i++) {
                indices[i] = lowerIdx + i;
                courses[i] = arr[lowerIdx + i];
            }
            long elapsed = System.nanoTime() - start;
            if (trace) {
                log.append(String.format("  [Binary Bounds] Range [%d..%d] covers indices [%d..%d)\n",
                        minCredits, maxCredits, lowerIdx, upperIdx));
            }
            return new SearchResult(
                    "Binary Bounds Range Search",
                    "Credits in [" + minCredits + ", " + maxCredits + "]",
                    count > 0 ? indices[0] : -1,
                    indices,
                    courses,
                    comparisons,
                    elapsed,
                    "O(log n + k)",
                    "Used binary lower/upper bounds on credit-sorted array",
                    log == null ? "" : log.toString());
        } else {
            int[] tempIndices = new int[arr.length];
            int count = 0;
            for (int i = 0; i < arr.length; i++) {
                comparisons++;
                int cr = arr[i].getCredits();
                if (cr >= minCredits && cr <= maxCredits) {
                    tempIndices[count++] = i;
                    if (trace) {
                        log.append(String.format("  [Scan Match] Index %d -> %s (%d cr)\n",
                                i, arr[i].getCourseCode(), cr));
                    }
                }
            }
            long elapsed = System.nanoTime() - start;

            int[] indices = new int[count];
            Course[] courses = new Course[count];
            for (int i = 0; i < count; i++) {
                indices[i] = tempIndices[i];
                courses[i] = arr[indices[i]];
            }
            return new SearchResult(
                    "Linear Range Scan",
                    "Credits in [" + minCredits + ", " + maxCredits + "]",
                    count > 0 ? indices[0] : -1,
                    indices,
                    courses,
                    comparisons,
                    elapsed,
                    "O(n)",
                    "Array not sorted by Credits; used linear scan",
                    log == null ? "" : log.toString());
        }
    }

    private static SearchResult buildSingleResult(
            String algo,
            String query,
            Course[] arr,
            int foundIdx,
            long comparisons,
            long elapsedNs,
            String complexity,
            String notes,
            StringBuilder log) {
        int[] indices = foundIdx >= 0 ? new int[] {foundIdx} : new int[0];
        Course[] courses = foundIdx >= 0 ? new Course[] {arr[foundIdx]} : new Course[0];
        return new SearchResult(
                algo,
                query,
                foundIdx,
                indices,
                courses,
                comparisons,
                elapsedNs,
                complexity,
                notes,
                log == null ? "" : log.toString());
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim();
    }
}
