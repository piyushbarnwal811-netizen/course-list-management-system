package com.dsa.clms.ui;

import com.dsa.clms.algorithms.SearchResult;
import com.dsa.clms.algorithms.SortResult;
import com.dsa.clms.ds.CourseArray;
import com.dsa.clms.model.Course;

import java.util.Scanner;

/**
 * Console rendering, formatted ASCII tables, and validated input helpers.
 */
public class ConsoleUI {

    private final Scanner scanner;

    public ConsoleUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void printBanner(int count, int capacity, boolean traceMode) {
        System.out.println();
        System.out.println("====================================================================================");
        System.out.println("             COURSE LIST MANAGEMENT SYSTEM (DSA: Array, Searching, Sorting)         ");
        System.out.println("====================================================================================");
        System.out.printf("  Array Status: Size = %d / Capacity = %d (Load: %.1f%%) | Trace Mode: %s%n",
                count, capacity, capacity == 0 ? 0.0 : (count * 100.0) / capacity,
                traceMode ? "ON" : "OFF");
        System.out.println("------------------------------------------------------------------------------------");
    }

    public void printCourseTable(Course[] courses, String title) {
        System.out.println();
        System.out.println(">> " + title + " (Total: " + courses.length + ")");
        if (courses.length == 0) {
            System.out.println("   [No courses to display]");
            return;
        }
        String line = "+-----+------+--------+--------------------------------+------------------+--------------------+----+-----+---------+------+";
        System.out.println(line);
        System.out.printf("| %-3s | %-4s | %-6s | %-30s | %-16s | %-18s | %-2s | %-3s | %-7s | %-4s |%n",
                "Idx", "ID", "Code", "Course Title", "Department", "Instructor", "Cr", "Sem", "Enr/Cap", "Rate");
        System.out.println(line);

        for (int i = 0; i < courses.length; i++) {
            Course c = courses[i];
            System.out.printf("| %3d | %4d | %-6s | %-30s | %-16s | %-18s | %2d | %3d | %3d/%-3d | %4.1f |%n",
                    i,
                    c.getCourseId(),
                    truncate(c.getCourseCode(), 6),
                    truncate(c.getTitle(), 30),
                    truncate(c.getDepartment(), 16),
                    truncate(c.getInstructor(), 18),
                    c.getCredits(),
                    c.getSemester(),
                    c.getEnrolled(),
                    c.getCapacity(),
                    c.getRating());
        }
        System.out.println(line);
    }

    public void printArrayStats(CourseArray array) {
        System.out.println();
        System.out.println("=== Internal Array Memory & Instrumentation Stats ===");
        System.out.println("  Current Size (n)      : " + array.size());
        System.out.println("  Allocated Capacity    : " + array.capacity());
        System.out.printf("  Load Factor           : %.2f%%%n", array.getLoadFactorPercent());
        System.out.println("  Auto-Resize (2x)      : " + (array.isAutoResize() ? "Enabled" : "Disabled (Fixed Array)"));
        System.out.println("  Total Insertions      : " + array.getTotalInsertions());
        System.out.println("  Total Deletions       : " + array.getTotalDeletions());
        System.out.println("  Array Resize Count    : " + array.getResizeCount());
        System.out.println("  Shifts in Last Op     : " + array.getLastShiftCount());
        System.out.println("  Sorted by Code (ASC)  : " + array.isSortedBy(Course.SortKey.COURSE_CODE, Course.SortOrder.ASC));
        System.out.println("  Sorted by ID (ASC)    : " + array.isSortedBy(Course.SortKey.COURSE_ID, Course.SortOrder.ASC));
        System.out.println("  Sorted by Credits(ASC): " + array.isSortedBy(Course.SortKey.CREDITS, Course.SortOrder.ASC));
    }

    public void printSearchResult(SearchResult result, boolean showTrace) {
        System.out.println();
        System.out.println("=== Search Result ===");
        System.out.println("  Algorithm       : " + result.getAlgorithmName());
        System.out.println("  Query           : " + result.getQueryDescription());
        System.out.println("  Complexity      : " + result.getTimeComplexity());
        System.out.println("  Comparisons     : " + result.getComparisons());
        System.out.printf("  Execution Time  : %.3f us (%d ns)%n",
                result.getExecutionTimeUs(), result.getExecutionTimeNs());
        if (!result.getNotes().isEmpty()) {
            System.out.println("  Notes           : " + result.getNotes());
        }

        if (showTrace && !result.getTraceLog().isEmpty()) {
            System.out.println("  --- Step-by-Step Trace ---");
            System.out.print(result.getTraceLog());
        }

        Course[] matches = result.getMatchedCourses();
        if (matches.length == 0) {
            System.out.println("  Outcome         : NOT FOUND");
        } else if (matches.length == 1) {
            System.out.println("  Outcome         : FOUND at array index " + result.getFoundIndex());
            printCourseTable(matches, "Matched Course");
        } else {
            System.out.println("  Outcome         : FOUND " + matches.length + " matching courses");
            printCourseTable(matches, "Matched Courses");
        }
    }

    public void printSortResult(SortResult result, boolean showTrace) {
        System.out.println();
        System.out.println("=== Sort Execution Report ===");
        System.out.println("  Algorithm       : " + result.getAlgorithmName());
        System.out.println("  Sort Key/Order  : " + result.getSortKey().getDisplayName()
                + " (" + result.getSortOrder().getDisplayName() + ")");
        System.out.println("  Elements (n)    : " + result.getElementCount());
        System.out.println("  Comparisons     : " + result.getComparisons());
        System.out.println("  Swaps / Writes  : " + result.getSwapsOrWrites());
        System.out.println("  Passes / Steps  : " + result.getPasses());
        System.out.printf("  Execution Time  : %.3f ms (%.2f us)%n",
                result.getExecutionTimeMs(), result.getExecutionTimeUs());
        System.out.println("  Stable          : " + (result.isStable() ? "Yes" : "No"));
        System.out.println("  Time Complexity : " + result.getTimeComplexity());
        System.out.println("  Space Complexity: " + result.getSpaceComplexity());

        if (showTrace && !result.getTraceLog().isEmpty()) {
            System.out.println("  --- Execution Trace (first passes) ---");
            System.out.print(result.getTraceLog());
        }
    }

    public void printSortBenchmarkTable(SortResult[] results) {
        System.out.println();
        System.out.println("=== Sorting Algorithms Benchmark Comparison ===");
        if (results.length > 0) {
            System.out.println("  Dataset Size (n): " + results[0].getElementCount()
                    + " | Key: " + results[0].getSortKey().getDisplayName()
                    + " (" + results[0].getSortOrder().getDisplayName() + ")");
        }
        String line = "+---------------------------+-------------+--------------+--------+------------+--------+------------------------+----------+";
        System.out.println(line);
        System.out.printf("| %-25s | %11s | %12s | %6s | %10s | %-6s | %-22s | %-8s |%n",
                "Algorithm", "Comparisons", "Swaps/Writes", "Passes", "Time (us)", "Stable", "Time Complexity", "Space");
        System.out.println(line);
        for (SortResult r : results) {
            System.out.printf("| %-25s | %11d | %12d | %6d | %10.2f | %-6s | %-22s | %-8s |%n",
                    truncate(r.getAlgorithmName(), 25),
                    r.getComparisons(),
                    r.getSwapsOrWrites(),
                    r.getPasses(),
                    r.getExecutionTimeUs(),
                    r.isStable() ? "Yes" : "No",
                    truncate(r.getTimeComplexity(), 22),
                    truncate(r.getSpaceComplexity(), 8));
        }
        System.out.println(line);
    }

    public void printSearchBenchmarkTable(SearchResult[] results, int n) {
        System.out.println();
        System.out.println("=== Searching Algorithms Benchmark Comparison (on Code-Sorted Array, n=" + n + ") ===");
        String line = "+---------------------------+-------------+------------+--------+----------------------------------+";
        System.out.println(line);
        System.out.printf("| %-25s | %11s | %10s | %-6s | %-32s |%n",
                "Algorithm", "Comparisons", "Time (us)", "Index", "Time Complexity");
        System.out.println(line);
        for (SearchResult r : results) {
            System.out.printf("| %-25s | %11d | %10.2f | %6d | %-32s |%n",
                    truncate(r.getAlgorithmName(), 25),
                    r.getComparisons(),
                    r.getExecutionTimeUs(),
                    r.getFoundIndex(),
                    truncate(r.getTimeComplexity(), 32));
        }
        System.out.println(line);
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val < min || val > max) {
                    System.out.printf("  [!] Please enter an integer between %d and %d.%n", min, max);
                } else {
                    return val;
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number. Please try again.");
            }
        }
    }

    public double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val < min || val > max) {
                    System.out.printf("  [!] Please enter a number between %.2f and %.2f.%n", min, max);
                } else {
                    return val;
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid decimal number. Please try again.");
            }
        }
    }

    public String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("  [!] Input cannot be empty.");
        }
    }

    public String readOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            }
            if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("  [!] Please answer y or n.");
        }
    }

    public void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private String truncate(String s, int maxLen) {
        if (s == null) {
            return "";
        }
        if (s.length() <= maxLen) {
            return s;
        }
        return maxLen <= 3 ? s.substring(0, maxLen) : s.substring(0, maxLen - 3) + "...";
    }
}
