package com.dsa.clms.ui;

import com.dsa.clms.algorithms.AlgorithmBenchmark;
import com.dsa.clms.algorithms.CourseSearcher;
import com.dsa.clms.algorithms.CourseSorter;
import com.dsa.clms.algorithms.SearchResult;
import com.dsa.clms.algorithms.SortResult;
import com.dsa.clms.data.CsvStorage;
import com.dsa.clms.data.SampleCourseData;
import com.dsa.clms.ds.CourseArray;
import com.dsa.clms.model.Course;
import com.dsa.clms.model.Course.SortKey;
import com.dsa.clms.model.Course.SortOrder;

import java.io.IOException;
import java.util.Scanner;

/**
 * Interactive CLI Menu Controller orchestrating Array ADT operations,
 * Searching, Sorting, Benchmarking, Analytics, and CSV persistence.
 */
public class MenuController {

    private static final String DEFAULT_CSV_PATH = "data/courses.csv";

    private final CourseArray courseArray;
    private final ConsoleUI ui;
    private boolean traceMode = false;

    public MenuController() {
        this.courseArray = new CourseArray(16, true);
        SampleCourseData.loadInto(this.courseArray);
        this.ui = new ConsoleUI(new Scanner(System.in));
    }

    public void run() {
        boolean running = true;
        while (running) {
            ui.printBanner(courseArray.size(), courseArray.capacity(), traceMode);
            System.out.println("  1. Array Operations       (View, Insert, Update, Delete, Reverse, Resize)");
            System.out.println("  2. Searching Operations   (Linear, Sentinel, Binary, Jump, Exponential, Interpolation, Range)");
            System.out.println("  3. Sorting Operations     (Bubble, Selection, Insertion, Shell, Merge, Quick, Heap)");
            System.out.println("  4. Benchmark & DSA Lab    (Compare All Sorts & Searches, Stress Test N Courses)");
            System.out.println("  5. Analytics & Reports    (Department Summary, Top Rated, Seat Occupancy)");
            System.out.println("  6. Dataset & CSV Storage  (Sample Data, Random Generator, Save/Load CSV, Clear)");
            System.out.println("  7. Toggle Step Trace Mode (Currently: " + (traceMode ? "ON" : "OFF") + ")");
            System.out.println("  0. Exit");
            System.out.println("------------------------------------------------------------------------------------");

            int choice = ui.readInt("Select an option [0-7]: ", 0, 7);
            switch (choice) {
                case 1:
                    handleArrayMenu();
                    break;
                case 2:
                    handleSearchMenu();
                    break;
                case 3:
                    handleSortMenu();
                    break;
                case 4:
                    handleBenchmarkMenu();
                    break;
                case 5:
                    handleAnalyticsMenu();
                    break;
                case 6:
                    handleDatasetMenu();
                    break;
                case 7:
                    traceMode = !traceMode;
                    System.out.println("  [OK] Step-by-Step Trace Mode is now " + (traceMode ? "ON" : "OFF") + ".");
                    break;
                case 0:
                    running = false;
                    System.out.println("\nExiting Course List Management System. Goodbye!");
                    break;
                default:
                    break;
            }
        }
    }

    private void handleArrayMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Array Operations (Custom CourseArray ADT) ---");
            System.out.println("  1. Display All Courses (Table)");
            System.out.println("  2. Insert Course at End         [O(1) amortized]");
            System.out.println("  3. Insert Course at Index       [O(n) with right-shift]");
            System.out.println("  4. Update Course at Index       [O(1) direct access]");
            System.out.println("  5. Delete Course by Index       [O(n) with left-shift]");
            System.out.println("  6. Delete Course by Code        [O(n) search + left-shift]");
            System.out.println("  7. Reverse Array In-Place       [O(n), O(1) space]");
            System.out.println("  8. Resize Array Capacity        [Manual array copy]");
            System.out.println("  9. Toggle Auto-Resize           (Currently: " + (courseArray.isAutoResize() ? "ON" : "OFF") + ")");
            System.out.println(" 10. View Internal Array Stats");
            System.out.println("  0. Back to Main Menu");

            int ch = ui.readInt("Choose [0-10]: ", 0, 10);
            try {
                switch (ch) {
                    case 1:
                        ui.printCourseTable(courseArray.toDenseArray(), "Current Course Array");
                        break;
                    case 2: {
                        Course c = promptNewCourse();
                        boolean added = courseArray.insertAtEnd(c);
                        if (added) {
                            System.out.println("  [OK] Added at index " + (courseArray.size() - 1)
                                    + " (shifts=" + courseArray.getLastShiftCount() + ").");
                        } else {
                            System.out.println("  [!] Array is FULL and auto-resize is OFF.");
                        }
                        break;
                    }
                    case 3: {
                        int idx = ui.readInt("Enter insertion index [0-" + courseArray.size() + "]: ",
                                0, courseArray.size());
                        Course c = promptNewCourse();
                        boolean added = courseArray.insertAtIndex(idx, c);
                        if (added) {
                            System.out.println("  [OK] Inserted at index " + idx
                                    + " (elements shifted right: " + courseArray.getLastShiftCount() + ").");
                        } else {
                            System.out.println("  [!] Array is FULL and auto-resize is OFF.");
                        }
                        break;
                    }
                    case 4: {
                        if (courseArray.isEmpty()) {
                            System.out.println("  [!] Array is empty.");
                            break;
                        }
                        int idx = ui.readInt("Enter index to update [0-" + (courseArray.size() - 1) + "]: ",
                                0, courseArray.size() - 1);
                        Course existing = courseArray.get(idx);
                        System.out.println("  Editing: " + existing);
                        Course updated = promptEditCourse(existing);
                        courseArray.set(idx, updated);
                        System.out.println("  [OK] Updated course at index " + idx + ".");
                        break;
                    }
                    case 5: {
                        if (courseArray.isEmpty()) {
                            System.out.println("  [!] Array is empty.");
                            break;
                        }
                        int idx = ui.readInt("Enter index to delete [0-" + (courseArray.size() - 1) + "]: ",
                                0, courseArray.size() - 1);
                        Course removed = courseArray.deleteAtIndex(idx);
                        System.out.println("  [OK] Deleted: " + removed.getCourseCode()
                                + " (elements shifted left: " + courseArray.getLastShiftCount() + ").");
                        break;
                    }
                    case 6: {
                        if (courseArray.isEmpty()) {
                            System.out.println("  [!] Array is empty.");
                            break;
                        }
                        String code = ui.readNonEmptyString("Enter Course Code to delete: ");
                        Course removed = courseArray.deleteByCourseCode(code);
                        if (removed != null) {
                            System.out.println("  [OK] Deleted " + removed.getCourseCode() + " - " + removed.getTitle()
                                    + " (elements shifted left: " + courseArray.getLastShiftCount() + ").");
                        } else {
                            System.out.println("  [!] Course code '" + code + "' not found.");
                        }
                        break;
                    }
                    case 7:
                        courseArray.reverseInPlace();
                        System.out.println("  [OK] Array reversed in-place.");
                        break;
                    case 8: {
                        int minCap = Math.max(1, courseArray.size());
                        int newCap = ui.readInt("Enter new capacity (>= " + minCap + "): ", minCap, 1_000_000);
                        courseArray.resize(newCap);
                        System.out.println("  [OK] Array resized to capacity " + courseArray.capacity() + ".");
                        break;
                    }
                    case 9:
                        courseArray.setAutoResize(!courseArray.isAutoResize());
                        System.out.println("  [OK] Auto-Resize is now " + (courseArray.isAutoResize() ? "ON" : "OFF"));
                        break;
                    case 10:
                        ui.printArrayStats(courseArray);
                        break;
                    case 0:
                        back = true;
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                System.out.println("  [Error] " + ex.getMessage());
            }
        }
    }

    private void handleSearchMenu() {
        boolean back = false;
        while (!back) {
            if (courseArray.isEmpty()) {
                System.out.println("\n  [!] Course array is empty. Load or insert courses first.");
                return;
            }
            System.out.println("\n--- Searching Algorithms ---");
            System.out.println("  1. Linear Search by Course Code          [O(n), unsorted OK]");
            System.out.println("  2. Sentinel Linear Search by Code        [O(n), unsorted OK]");
            System.out.println("  3. Binary Search (Iterative) by Code     [O(log n), requires Code-sorted]");
            System.out.println("  4. Binary Search (Recursive) by Code     [O(log n), requires Code-sorted]");
            System.out.println("  5. Jump Search by Course Code            [O(sqrt(n)), requires Code-sorted]");
            System.out.println("  6. Exponential Search by Course Code     [O(log i), requires Code-sorted]");
            System.out.println("  7. Interpolation Search by Course ID     [O(log log n) avg, requires ID-sorted]");
            System.out.println("  8. Keyword Search (Title/Code/Dept/Prof) [O(n), multi-match]");
            System.out.println("  9. Credit Range Search [min..max]        [O(log n + k) if sorted, else O(n)]");
            System.out.println("  0. Back to Main Menu");

            int ch = ui.readInt("Choose [0-9]: ", 0, 9);
            if (ch == 0) {
                back = true;
                continue;
            }

            Course[] workingArr = courseArray.toDenseArray();
            switch (ch) {
                case 1: {
                    String code = ui.readNonEmptyString("Enter Course Code (e.g. CS301): ");
                    SearchResult res = CourseSearcher.linearSearchByCode(workingArr, code, traceMode);
                    ui.printSearchResult(res, traceMode);
                    break;
                }
                case 2: {
                    String code = ui.readNonEmptyString("Enter Course Code (e.g. CS301): ");
                    SearchResult res = CourseSearcher.sentinelLinearSearchByCode(workingArr, code, traceMode);
                    ui.printSearchResult(res, traceMode);
                    break;
                }
                case 3:
                case 4:
                case 5:
                case 6: {
                    Course[] arrForSearch = ensureSortedForSearch(SortKey.COURSE_CODE);
                    if (arrForSearch == null) {
                        break;
                    }
                    String code = ui.readNonEmptyString("Enter Course Code (e.g. CS301): ");
                    SearchResult res;
                    if (ch == 3) {
                        res = CourseSearcher.binarySearchByCodeIterative(arrForSearch, code, traceMode);
                    } else if (ch == 4) {
                        res = CourseSearcher.binarySearchByCodeRecursive(arrForSearch, code, traceMode);
                    } else if (ch == 5) {
                        res = CourseSearcher.jumpSearchByCode(arrForSearch, code, traceMode);
                    } else {
                        res = CourseSearcher.exponentialSearchByCode(arrForSearch, code, traceMode);
                    }
                    ui.printSearchResult(res, traceMode);
                    break;
                }
                case 7: {
                    Course[] arrForSearch = ensureSortedForSearch(SortKey.COURSE_ID);
                    if (arrForSearch == null) {
                        break;
                    }
                    int id = ui.readInt("Enter numeric Course ID (e.g. 102): ", 1, 1_000_000);
                    SearchResult res = CourseSearcher.interpolationSearchById(arrForSearch, id, traceMode);
                    ui.printSearchResult(res, traceMode);
                    break;
                }
                case 8: {
                    String kw = ui.readNonEmptyString("Enter keyword (e.g. Data, Sharma, AI): ");
                    SearchResult res = CourseSearcher.keywordSearch(workingArr, kw, traceMode);
                    ui.printSearchResult(res, traceMode);
                    break;
                }
                case 9: {
                    int minC = ui.readInt("Minimum credits [1-10]: ", 1, 10);
                    int maxC = ui.readInt("Maximum credits [" + minC + "-10]: ", minC, 10);
                    boolean sortedByCredits = courseArray.isSortedBy(SortKey.CREDITS, SortOrder.ASC);
                    SearchResult res = CourseSearcher.rangeSearchByCredits(
                            workingArr, minC, maxC, sortedByCredits, traceMode);
                    ui.printSearchResult(res, traceMode);
                    break;
                }
                default:
                    break;
            }
        }
    }

    private Course[] ensureSortedForSearch(SortKey requiredKey) {
        if (courseArray.isSortedBy(requiredKey, SortOrder.ASC)) {
            return courseArray.toDenseArray();
        }
        System.out.println("  [!] Array is NOT currently sorted ascending by " + requiredKey.getDisplayName() + ".");
        System.out.println("      1. Sort main array in-place now (using Merge Sort)");
        System.out.println("      2. Run search on a temporary sorted copy (keeps main array order)");
        System.out.println("      0. Cancel");
        int opt = ui.readInt("Choose [0-2]: ", 0, 2);
        if (opt == 0) {
            return null;
        }
        SortResult sorted = CourseSorter.mergeSort(
                courseArray.toClonedArray(), requiredKey, SortOrder.ASC, false);
        if (opt == 1) {
            courseArray.replaceContents(sorted.getSortedArray());
            System.out.println("  [OK] Main array sorted by " + requiredKey.getDisplayName() + " (ASC).");
            return courseArray.toDenseArray();
        } else {
            System.out.println("  [Info] Searching on a temporary sorted copy.");
            return sorted.getSortedArray();
        }
    }

    private void handleSortMenu() {
        if (courseArray.isEmpty()) {
            System.out.println("\n  [!] Course array is empty.");
            return;
        }
        System.out.println("\n--- Sorting Algorithms ---");
        CourseSorter.Algorithm[] algos = CourseSorter.Algorithm.values();
        for (int i = 0; i < algos.length; i++) {
            System.out.printf("  %d. %-26s [%s, Space: %s, Stable: %s]%n",
                    i + 1,
                    algos[i].getDisplayName(),
                    algos[i].getTimeComplexity(),
                    algos[i].getSpaceComplexity(),
                    algos[i].isStable() ? "Yes" : "No");
        }
        System.out.println("  0. Back");

        int algoIdx = ui.readInt("Select algorithm [0-" + algos.length + "]: ", 0, algos.length);
        if (algoIdx == 0) {
            return;
        }
        CourseSorter.Algorithm chosenAlgo = algos[algoIdx - 1];

        SortKey key = promptSortKey();
        SortOrder order = promptSortOrder();

        SortResult result = CourseSorter.sort(courseArray.toClonedArray(), chosenAlgo, key, order, traceMode);
        ui.printSortResult(result, traceMode);

        if (ui.readYesNo("Apply this sorted order to the main CourseArray?")) {
            courseArray.replaceContents(result.getSortedArray());
            System.out.println("  [OK] Main CourseArray updated with sorted order.");
            ui.printCourseTable(courseArray.toDenseArray(), "Sorted Course Array");
        }
    }

    private void handleBenchmarkMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Algorithm Benchmark & Complexity Lab ---");
            System.out.println("  1. Benchmark All 7 Sorting Algorithms on Current Array (n=" + courseArray.size() + ")");
            System.out.println("  2. Stress-Test All 7 Sorting Algorithms on Synthetic N Courses (e.g., 500, 2000, 5000)");
            System.out.println("  3. Benchmark All 6 Code-Search Algorithms (Linear vs Binary vs Jump vs Exponential)");
            System.out.println("  4. Print DSA Time & Space Complexity Cheat Sheet");
            System.out.println("  0. Back to Main Menu");

            int ch = ui.readInt("Choose [0-4]: ", 0, 4);
            switch (ch) {
                case 1: {
                    if (courseArray.isEmpty()) {
                        System.out.println("  [!] Current array is empty.");
                        break;
                    }
                    SortKey key = promptSortKey();
                    SortOrder order = promptSortOrder();
                    SortResult[] results = AlgorithmBenchmark.benchmarkAllSorts(
                            courseArray.toDenseArray(), key, order);
                    ui.printSortBenchmarkTable(results);
                    break;
                }
                case 2: {
                    int n = ui.readInt("Enter synthetic dataset size N [10 - 15000]: ", 10, 15000);
                    SortKey key = promptSortKey();
                    SortOrder order = promptSortOrder();
                    System.out.println("  Generating " + n + " randomized courses and running 7 sorts...");
                    Course[] synthetic = AlgorithmBenchmark.generateSyntheticDataset(n, System.currentTimeMillis());
                    SortResult[] results = AlgorithmBenchmark.benchmarkAllSorts(synthetic, key, order);
                    ui.printSortBenchmarkTable(results);
                    break;
                }
                case 3: {
                    System.out.println("  1. Use current array (n=" + courseArray.size() + ")");
                    System.out.println("  2. Use large synthetic array (n=10000) to highlight O(n) vs O(log n)");
                    int srcChoice = ui.readInt("Choose [1-2]: ", 1, 2);
                    Course[] dataset;
                    String targetCode;
                    if (srcChoice == 1) {
                        if (courseArray.isEmpty()) {
                            System.out.println("  [!] Current array is empty.");
                            break;
                        }
                        dataset = courseArray.toDenseArray();
                        targetCode = ui.readNonEmptyString("Enter target Course Code (e.g. CS415 or ZZZ999): ");
                    } else {
                        dataset = AlgorithmBenchmark.generateSyntheticDataset(10000, 42L);
                        targetCode = ui.readOptionalString(
                                "Enter target code (leave blank to search worst-case missing code 'ZZ9999'): ");
                        if (targetCode.isEmpty()) {
                            targetCode = "ZZ9999";
                        }
                    }
                    SearchResult[] results = AlgorithmBenchmark.benchmarkAllCodeSearches(dataset, targetCode);
                    ui.printSearchBenchmarkTable(results, dataset.length);
                    break;
                }
                case 4:
                    printComplexityCheatSheet();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    break;
            }
        }
    }

    private void handleAnalyticsMenu() {
        if (courseArray.isEmpty()) {
            System.out.println("\n  [!] Course array is empty.");
            return;
        }
        Course[] arr = courseArray.toDenseArray();
        int n = arr.length;

        int totalCredits = 0;
        int totalEnrolled = 0;
        int totalCapacity = 0;
        double totalRating = 0.0;
        Course highestRated = arr[0];
        Course mostPopular = arr[0];
        Course leastOccupied = arr[0];

        // Track unique departments using raw arrays (max n unique departments)
        String[] deptNames = new String[n];
        int[] deptCounts = new int[n];
        int[] deptEnrolled = new int[n];
        double[] deptRatingSum = new double[n];
        int uniqueDepts = 0;

        for (int i = 0; i < n; i++) {
            Course c = arr[i];
            totalCredits += c.getCredits();
            totalEnrolled += c.getEnrolled();
            totalCapacity += c.getCapacity();
            totalRating += c.getRating();

            if (c.getRating() > highestRated.getRating()) {
                highestRated = c;
            }
            if (c.getEnrolled() > mostPopular.getEnrolled()) {
                mostPopular = c;
            }
            if (c.getOccupancyPercent() < leastOccupied.getOccupancyPercent()) {
                leastOccupied = c;
            }

            int deptIdx = -1;
            for (int d = 0; d < uniqueDepts; d++) {
                if (deptNames[d].equalsIgnoreCase(c.getDepartment())) {
                    deptIdx = d;
                    break;
                }
            }
            if (deptIdx == -1) {
                deptNames[uniqueDepts] = c.getDepartment();
                deptCounts[uniqueDepts] = 1;
                deptEnrolled[uniqueDepts] = c.getEnrolled();
                deptRatingSum[uniqueDepts] = c.getRating();
                uniqueDepts++;
            } else {
                deptCounts[deptIdx]++;
                deptEnrolled[deptIdx] += c.getEnrolled();
                deptRatingSum[deptIdx] += c.getRating();
            }
        }

        System.out.println("\n=== Course Catalog Analytics (Computed via Pure Array Traversal) ===");
        System.out.println("  Total Courses          : " + n);
        System.out.printf("  Average Credits        : %.2f%n", (totalCredits * 1.0) / n);
        System.out.printf("  Average Rating         : %.2f / 5.0%n", totalRating / n);
        System.out.printf("  Total Enrollment       : %d / %d seats (%.1f%% occupied)%n",
                totalEnrolled, totalCapacity, totalCapacity == 0 ? 0.0 : (totalEnrolled * 100.0) / totalCapacity);
        System.out.println("  Highest Rated Course   : " + highestRated.getCourseCode() + " - "
                + highestRated.getTitle() + " (" + highestRated.getRating() + ")");
        System.out.println("  Most Enrolled Course   : " + mostPopular.getCourseCode() + " - "
                + mostPopular.getTitle() + " (" + mostPopular.getEnrolled() + " students)");
        System.out.printf("  Lowest Occupancy       : %s - %s (%.1f%%)%n",
                leastOccupied.getCourseCode(), leastOccupied.getTitle(), leastOccupied.getOccupancyPercent());

        String line = "+----------------------+---------+----------------+------------+";
        System.out.println("\n  Department Breakdown:");
        System.out.println("  " + line);
        System.out.printf("  | %-20s | %7s | %14s | %10s |%n",
                "Department", "Courses", "Total Enrolled", "Avg Rating");
        System.out.println("  " + line);
        for (int d = 0; d < uniqueDepts; d++) {
            System.out.printf("  | %-20s | %7d | %14d | %10.2f |%n",
                    deptNames[d].length() > 20 ? deptNames[d].substring(0, 17) + "..." : deptNames[d],
                    deptCounts[d],
                    deptEnrolled[d],
                    deptRatingSum[d] / deptCounts[d]);
        }
        System.out.println("  " + line);
    }

    private void handleDatasetMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Dataset & CSV Persistence ---");
            System.out.println("  1. Reload Default 24 Sample Courses");
            System.out.println("  2. Generate N Random Courses into Array");
            System.out.println("  3. Save Current Array to CSV (" + DEFAULT_CSV_PATH + ")");
            System.out.println("  4. Load Array from CSV (" + DEFAULT_CSV_PATH + ")");
            System.out.println("  5. Clear All Courses");
            System.out.println("  0. Back to Main Menu");

            int ch = ui.readInt("Choose [0-5]: ", 0, 5);
            try {
                switch (ch) {
                    case 1:
                        SampleCourseData.loadInto(courseArray);
                        System.out.println("  [OK] Loaded " + courseArray.size() + " sample courses.");
                        break;
                    case 2: {
                        int n = ui.readInt("How many random courses to generate [1 - 5000]? ", 1, 5000);
                        Course[] generated = AlgorithmBenchmark.generateSyntheticDataset(n, System.currentTimeMillis());
                        courseArray.replaceContents(generated);
                        System.out.println("  [OK] Generated " + courseArray.size() + " courses.");
                        break;
                    }
                    case 3: {
                        String path = ui.readOptionalString("CSV path [Enter for '" + DEFAULT_CSV_PATH + "']: ");
                        if (path.isEmpty()) {
                            path = DEFAULT_CSV_PATH;
                        }
                        CsvStorage.save(courseArray, path);
                        System.out.println("  [OK] Saved " + courseArray.size() + " courses to " + path);
                        break;
                    }
                    case 4: {
                        String path = ui.readOptionalString("CSV path [Enter for '" + DEFAULT_CSV_PATH + "']: ");
                        if (path.isEmpty()) {
                            path = DEFAULT_CSV_PATH;
                        }
                        int count = CsvStorage.loadInto(courseArray, path);
                        System.out.println("  [OK] Loaded " + count + " courses from " + path);
                        break;
                    }
                    case 5:
                        if (ui.readYesNo("Are you sure you want to clear all courses?")) {
                            courseArray.clear();
                            System.out.println("  [OK] Course array cleared.");
                        }
                        break;
                    case 0:
                        back = true;
                        break;
                    default:
                        break;
                }
            } catch (IOException | RuntimeException ex) {
                System.out.println("  [Error] " + ex.getMessage());
            }
        }
    }

    private Course promptNewCourse() {
        int id = ui.readInt("  Course ID (numeric, e.g. 601): ", 1, 999999);
        String code = ui.readNonEmptyString("  Course Code (e.g. CS450): ");
        String title = ui.readNonEmptyString("  Course Title: ");
        String dept = ui.readNonEmptyString("  Department: ");
        String instr = ui.readNonEmptyString("  Instructor: ");
        int credits = ui.readInt("  Credits [1-6]: ", 1, 6);
        int sem = ui.readInt("  Semester [1-8]: ", 1, 8);
        int cap = ui.readInt("  Capacity [1-500]: ", 1, 500);
        int enr = ui.readInt("  Enrolled [0-" + cap + "]: ", 0, cap);
        double rating = ui.readDouble("  Rating [0.0-5.0]: ", 0.0, 5.0);
        return new Course(id, code, title, dept, instr, credits, sem, cap, enr, rating);
    }

    private Course promptEditCourse(Course existing) {
        System.out.println("  (Press Enter to keep existing text values)");
        String title = ui.readOptionalString("  Title [" + existing.getTitle() + "]: ");
        if (title.isEmpty()) title = existing.getTitle();

        String dept = ui.readOptionalString("  Department [" + existing.getDepartment() + "]: ");
        if (dept.isEmpty()) dept = existing.getDepartment();

        String instr = ui.readOptionalString("  Instructor [" + existing.getInstructor() + "]: ");
        if (instr.isEmpty()) instr = existing.getInstructor();

        int credits = ui.readInt("  Credits [1-6] (was " + existing.getCredits() + "): ", 1, 6);
        int sem = ui.readInt("  Semester [1-8] (was " + existing.getSemester() + "): ", 1, 8);
        int cap = ui.readInt("  Capacity [1-500] (was " + existing.getCapacity() + "): ", 1, 500);
        int enr = ui.readInt("  Enrolled [0-" + cap + "] (was " + existing.getEnrolled() + "): ", 0, cap);
        double rating = ui.readDouble("  Rating [0.0-5.0] (was " + existing.getRating() + "): ", 0.0, 5.0);

        return new Course(
                existing.getCourseId(),
                existing.getCourseCode(),
                title,
                dept,
                instr,
                credits,
                sem,
                cap,
                enr,
                rating);
    }

    private SortKey promptSortKey() {
        SortKey[] keys = SortKey.values();
        System.out.println("  Sort By Field:");
        for (int i = 0; i < keys.length; i++) {
            System.out.printf("    %d. %s%n", i + 1, keys[i].getDisplayName());
        }
        int idx = ui.readInt("  Choose field [1-" + keys.length + "]: ", 1, keys.length);
        return keys[idx - 1];
    }

    private SortOrder promptSortOrder() {
        System.out.println("  Sort Order: 1. Ascending  |  2. Descending");
        int idx = ui.readInt("  Choose order [1-2]: ", 1, 2);
        return idx == 1 ? SortOrder.ASC : SortOrder.DESC;
    }

    private void printComplexityCheatSheet() {
        System.out.println("\n=== DSA Time & Space Complexity Reference ===");
        System.out.println("  ARRAY OPERATIONS:");
        System.out.println("    Access by index         : O(1) time | O(1) space");
        System.out.println("    Insert at end           : O(1) amortized (O(n) on resize)");
        System.out.println("    Insert/Delete at index  : O(n) due to contiguous element shifting");
        System.out.println("  SEARCHING:");
        System.out.println("    Linear / Sentinel       : O(n) time | O(1) space (unsorted OK)");
        System.out.println("    Jump Search             : O(sqrt(n)) time | O(1) space (sorted)");
        System.out.println("    Binary Search (Iter/Rec): O(log n) time | O(1) iter / O(log n) rec (sorted)");
        System.out.println("    Exponential Search      : O(log i) time | O(1) space (sorted)");
        System.out.println("    Interpolation Search    : O(log log n) avg / O(n) worst (sorted uniform IDs)");
        System.out.println("  SORTING:");
        System.out.println("    Bubble / Insertion      : O(n) best, O(n^2) avg/worst | O(1) space | Stable");
        System.out.println("    Selection Sort          : O(n^2) all cases | O(1) space | Unstable (min writes)");
        System.out.println("    Shell Sort (Knuth)      : ~O(n^(3/2)) | O(1) space | Unstable");
        System.out.println("    Merge Sort              : O(n log n) all cases | O(n) space | Stable");
        System.out.println("    Quick Sort (Med-of-3)   : O(n log n) avg, O(n^2) worst | O(log n) stack | Unstable");
        System.out.println("    Heap Sort               : O(n log n) all cases | O(1) space | Unstable");
    }
}
