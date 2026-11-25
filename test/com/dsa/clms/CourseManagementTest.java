package com.dsa.clms;

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

import java.io.File;

/**
 * Self-contained unit & integration test runner for the Course List Management System.
 * Verifies custom Array ADT, all 9 Searching modes, all 7 Sorting algorithms,
 * stability guarantees, edge cases, and CSV persistence.
 */
public class CourseManagementTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("Running Course List Management System Test Suite...\n");

        testCourseArrayOperations();
        testCourseArrayFixedCapacityAndDuplicates();
        testAllSearchingAlgorithms();
        testAllSortingAlgorithmsAndStability();
        testSortingEdgeCases();
        testCsvRoundTrip();

        System.out.println("\n====================================================");
        System.out.printf("TEST SUMMARY: %d / %d checks passed.%n", testsPassed, testsRun);
        System.out.println("====================================================");
        if (testsPassed != testsRun) {
            throw new AssertionError("Some tests failed!");
        }
    }

    private static void testCourseArrayOperations() {
        CourseArray arr = new CourseArray(2, true);
        assertTrue(arr.isEmpty(), "New array should be empty");

        Course c1 = new Course(1, "CS101", "Intro CS", "CS", "Prof A", 4, 1, 60, 50, 4.5);
        Course c2 = new Course(2, "CS102", "DSA", "CS", "Prof B", 4, 2, 60, 55, 4.8);
        Course c3 = new Course(3, "CS103", "DBMS", "CS", "Prof C", 3, 3, 60, 40, 4.2);

        assertTrue(arr.insertAtEnd(c1), "Insert c1");
        assertTrue(arr.insertAtEnd(c2), "Insert c2");
        assertEquals(2, arr.capacity(), "Capacity should still be 2 before 3rd insert");
        assertTrue(arr.insertAtEnd(c3), "Insert c3 should trigger auto-resize");
        assertEquals(4, arr.capacity(), "Capacity should double to 4");
        assertEquals(1, arr.getResizeCount(), "Resize count should be 1");

        Course c0 = new Course(4, "CS100", "Basics", "CS", "Prof D", 2, 1, 50, 30, 4.0);
        arr.insertAtIndex(0, c0);
        assertEquals(3, arr.getLastShiftCount(), "Inserting at index 0 of size 3 shifts 3 elements");
        assertEquals("CS100", arr.get(0).getCourseCode(), "Index 0 should be CS100");
        assertEquals("CS103", arr.get(3).getCourseCode(), "Index 3 should be CS103");

        Course removed = arr.deleteAtIndex(1);
        assertEquals("CS101", removed.getCourseCode(), "Deleted element should be CS101");
        assertEquals(2, arr.getLastShiftCount(), "Deleting index 1 from size 4 shifts 2 elements left");
        assertEquals(3, arr.size(), "Size should be 3 after delete");

        arr.reverseInPlace();
        assertEquals("CS103", arr.get(0).getCourseCode(), "First after reverse should be CS103");
        assertEquals("CS100", arr.get(2).getCourseCode(), "Last after reverse should be CS100");
    }

    private static void testCourseArrayFixedCapacityAndDuplicates() {
        CourseArray fixed = new CourseArray(1, false);
        Course c1 = new Course(10, "MA101", "Calculus", "Math", "Prof X", 4, 1, 50, 40, 4.1);
        Course c2 = new Course(11, "MA102", "Algebra", "Math", "Prof Y", 4, 2, 50, 40, 4.2);

        assertTrue(fixed.insertAtEnd(c1), "First insert in fixed array succeeds");
        assertTrue(!fixed.insertAtEnd(c2), "Second insert in fixed array capacity=1 returns false");

        boolean duplicateRejected = false;
        try {
            Course dup = new Course(99, "MA101", "Dup", "Math", "Prof Z", 3, 1, 50, 10, 3.0);
            CourseArray grow = new CourseArray(4, true);
            grow.insertAtEnd(c1);
            grow.insertAtEnd(dup);
        } catch (IllegalArgumentException expected) {
            duplicateRejected = true;
        }
        assertTrue(duplicateRejected, "Duplicate courseCode must throw IllegalArgumentException");
    }

    private static void testAllSearchingAlgorithms() {
        Course[] sample = SampleCourseData.getSampleCourses();

        // Linear & Sentinel on unsorted array
        SearchResult lin = CourseSearcher.linearSearchByCode(sample, "CS102", true);
        assertTrue(lin.isFound(), "Linear search finds CS102");
        assertEquals("CS102", lin.getMatchedCourses()[0].getCourseCode(), "Linear search code match");

        SearchResult sent = CourseSearcher.sentinelLinearSearchByCode(sample, "AI405", true);
        assertTrue(sent.isFound(), "Sentinel search finds AI405");
        assertEquals("AI405", sent.getMatchedCourses()[0].getCourseCode(), "Sentinel search code match");

        SearchResult sentMiss = CourseSearcher.sentinelLinearSearchByCode(sample, "NO999", false);
        assertTrue(!sentMiss.isFound(), "Sentinel search returns not found for NO999");

        // Code-sorted searches
        Course[] sortedByCode = CourseSorter.mergeSort(
                sample, SortKey.COURSE_CODE, SortOrder.ASC, false).getSortedArray();

        String[] targets = {"AI302", "CS101", "CS415", "HU205", "MA302"};
        for (String target : targets) {
            SearchResult binIter = CourseSearcher.binarySearchByCodeIterative(sortedByCode, target, true);
            SearchResult binRec = CourseSearcher.binarySearchByCodeRecursive(sortedByCode, target, true);
            SearchResult jump = CourseSearcher.jumpSearchByCode(sortedByCode, target, true);
            SearchResult exp = CourseSearcher.exponentialSearchByCode(sortedByCode, target, true);

            assertTrue(binIter.isFound(), "Binary Iter finds " + target);
            assertEquals(binIter.getFoundIndex(), binRec.getFoundIndex(), "Binary Rec matches Iter index");
            assertEquals(binIter.getFoundIndex(), jump.getFoundIndex(), "Jump Search matches Binary index");
            assertEquals(binIter.getFoundIndex(), exp.getFoundIndex(), "Exponential Search matches Binary index");
        }

        SearchResult binMiss = CourseSearcher.binarySearchByCodeIterative(sortedByCode, "ZZ999", false);
        assertEquals(-1, binMiss.getFoundIndex(), "Binary search miss returns -1");

        // Interpolation search on ID-sorted array
        Course[] sortedById = CourseSorter.quickSort(
                sample, SortKey.COURSE_ID, SortOrder.ASC, false).getSortedArray();
        SearchResult interpHit = CourseSearcher.interpolationSearchById(sortedById, 309, true);
        assertTrue(interpHit.isFound(), "Interpolation search finds ID 309");
        assertEquals(309, interpHit.getMatchedCourses()[0].getCourseId(), "Interpolation ID match");

        SearchResult interpMiss = CourseSearcher.interpolationSearchById(sortedById, 9999, false);
        assertEquals(-1, interpMiss.getFoundIndex(), "Interpolation miss returns -1");

        // Keyword & Range searches
        SearchResult kw = CourseSearcher.keywordSearch(sample, "Sharma", false);
        assertEquals(2, kw.getMatchedCourses().length, "Keyword 'Sharma' matches 2 courses");

        Course[] sortedByCredits = CourseSorter.insertionSort(
                sample, SortKey.CREDITS, SortOrder.ASC, false).getSortedArray();
        SearchResult rangeSorted = CourseSearcher.rangeSearchByCredits(sortedByCredits, 2, 3, true, true);
        SearchResult rangeUnsorted = CourseSearcher.rangeSearchByCredits(sample, 2, 3, false, true);
        assertEquals(rangeUnsorted.getMatchedCourses().length, rangeSorted.getMatchedCourses().length,
                "Sorted binary bounds range search count matches unsorted linear range scan");
    }

    private static void testAllSortingAlgorithmsAndStability() {
        Course[] dataset = AlgorithmBenchmark.generateSyntheticDataset(60, 12345L);

        for (CourseSorter.Algorithm algo : CourseSorter.Algorithm.values()) {
            for (SortKey key : new SortKey[] {SortKey.COURSE_CODE, SortKey.CREDITS, SortKey.RATING}) {
                for (SortOrder order : SortOrder.values()) {
                    SortResult res = CourseSorter.sort(dataset, algo, key, order, false);
                    CourseArray check = new CourseArray(res.getSortedArray().length, false);
                    check.replaceContents(res.getSortedArray());
                    assertTrue(check.isSortedBy(key, order),
                            algo.getDisplayName() + " must sort by " + key + " " + order);
                }
            }
        }

        // Stability check for Bubble, Insertion, Merge Sort on equal credits
        Course a = new Course(1, "CS101", "First", "CS", "P1", 4, 1, 50, 10, 4.0);
        Course b = new Course(2, "CS102", "Second", "CS", "P2", 3, 1, 50, 10, 4.0);
        Course c = new Course(3, "CS103", "Third", "CS", "P3", 4, 1, 50, 10, 4.0);
        Course[] dupInput = {a, b, c};

        CourseSorter.Algorithm[] stableAlgos = {
                CourseSorter.Algorithm.BUBBLE,
                CourseSorter.Algorithm.INSERTION,
                CourseSorter.Algorithm.MERGE
        };
        for (CourseSorter.Algorithm algo : stableAlgos) {
            Course[] out = CourseSorter.sort(dupInput, algo, SortKey.CREDITS, SortOrder.ASC, false).getSortedArray();
            assertEquals("CS102", out[0].getCourseCode(), algo + " places 3-credit first");
            assertEquals("CS101", out[1].getCourseCode(), algo + " preserves CS101 before CS103");
            assertEquals("CS103", out[2].getCourseCode(), algo + " preserves CS103 after CS101");
        }
    }

    private static void testSortingEdgeCases() {
        Course[] empty = new Course[0];
        Course[] single = {new Course(1, "CS101", "Solo", "CS", "P", 3, 1, 50, 20, 4.5)};

        for (CourseSorter.Algorithm algo : CourseSorter.Algorithm.values()) {
            SortResult emptyRes = CourseSorter.sort(empty, algo, SortKey.COURSE_CODE, SortOrder.ASC, false);
            assertEquals(0, emptyRes.getSortedArray().length, algo + " handles empty array");

            SortResult singleRes = CourseSorter.sort(single, algo, SortKey.COURSE_CODE, SortOrder.ASC, false);
            assertEquals(1, singleRes.getSortedArray().length, algo + " handles single-element array");
        }
    }

    private static void testCsvRoundTrip() throws Exception {
        CourseArray original = new CourseArray(16, true);
        SampleCourseData.loadInto(original);
        original.insertAtEnd(new Course(
                999, "CS999", "Systems, Compilers & \"AI\"", "Computer Science", "Dr. Test", 4, 8, 50, 25, 4.9));

        String tempPath = "data/test_roundtrip_courses.csv";
        try {
            CsvStorage.save(original, tempPath);
            CourseArray loaded = new CourseArray(4, true);
            int count = CsvStorage.loadInto(loaded, tempPath);
            assertEquals(original.size(), count, "CSV loaded count matches saved count");
            assertEquals("Systems, Compilers & \"AI\"",
                    loaded.get(loaded.size() - 1).getTitle(),
                    "CSV preserves commas and quotes in title");
        } finally {
            File f = new File(tempPath);
            if (f.exists()) {
                f.delete();
            }
        }
    }

    private static void assertTrue(boolean condition, String message) {
        testsRun++;
        if (!condition) {
            System.err.println("  [FAIL] " + message);
            throw new AssertionError(message);
        }
        testsPassed++;
    }

    private static void assertEquals(int expected, int actual, String message) {
        testsRun++;
        if (expected != actual) {
            String err = message + " (expected=" + expected + ", actual=" + actual + ")";
            System.err.println("  [FAIL] " + err);
            throw new AssertionError(err);
        }
        testsPassed++;
    }

    private static void assertEquals(String expected, String actual, String message) {
        testsRun++;
        if (expected == null ? actual != null : !expected.equals(actual)) {
            String err = message + " (expected='" + expected + "', actual='" + actual + "')";
            System.err.println("  [FAIL] " + err);
            throw new AssertionError(err);
        }
        testsPassed++;
    }
}
