package com.dsa.clms.algorithms;

import com.dsa.clms.model.Course;
import com.dsa.clms.model.Course.SortKey;
import com.dsa.clms.model.Course.SortOrder;

import java.util.Random;

/**
 * Benchmarks all searching and sorting algorithms side-by-side on identical array snapshots
 * or generated datasets of arbitrary size.
 */
public final class AlgorithmBenchmark {

    private AlgorithmBenchmark() {}

    public static SortResult[] benchmarkAllSorts(Course[] dataset, SortKey key, SortOrder order) {
        CourseSorter.Algorithm[] algos = CourseSorter.Algorithm.values();
        SortResult[] results = new SortResult[algos.length];

        for (int i = 0; i < algos.length; i++) {
            Course[] copy = deepCopy(dataset);
            results[i] = CourseSorter.sort(copy, algos[i], key, order, false);
        }
        return results;
    }

    /**
     * Benchmarks all course-code search algorithms on a code-sorted copy of the dataset.
     */
    public static SearchResult[] benchmarkAllCodeSearches(Course[] dataset, String targetCode) {
        Course[] sorted = CourseSorter.mergeSort(
                deepCopy(dataset), SortKey.COURSE_CODE, SortOrder.ASC, false).getSortedArray();

        SearchResult[] results = new SearchResult[6];
        results[0] = CourseSearcher.linearSearchByCode(sorted, targetCode, false);
        results[1] = CourseSearcher.sentinelLinearSearchByCode(sorted, targetCode, false);
        results[2] = CourseSearcher.binarySearchByCodeIterative(sorted, targetCode, false);
        results[3] = CourseSearcher.binarySearchByCodeRecursive(sorted, targetCode, false);
        results[4] = CourseSearcher.jumpSearchByCode(sorted, targetCode, false);
        results[5] = CourseSearcher.exponentialSearchByCode(sorted, targetCode, false);
        return results;
    }

    /**
     * Generates a synthetic dataset of n courses in randomized order for stress-testing algorithms.
     */
    public static Course[] generateSyntheticDataset(int n, long seed) {
        if (n < 0) {
            throw new IllegalArgumentException("Dataset size cannot be negative.");
        }
        String[] depts = {"CS", "IT", "EC", "ME", "MA", "AI", "DS", "PH"};
        String[] deptFull = {
                "Computer Science", "Information Tech", "Electronics", "Mechanical",
                "Mathematics", "Artificial Intel", "Data Science", "Physics"
        };
        String[] topics = {
                "Algorithms", "Systems", "Networks", "Databases", "Compilers",
                "Linear Algebra", "Optimization", "Architecture", "Security", "Graphics"
        };
        String[] instructors = {
                "Dr. A. Sharma", "Prof. R. Verma", "Dr. S. Iyer", "Prof. K. Nair",
                "Dr. M. Gupta", "Prof. N. Kulkarni", "Dr. P. Rao", "Prof. V. Joshi"
        };

        Course[] arr = new Course[n];
        Random rng = new Random(seed);

        for (int i = 0; i < n; i++) {
            int deptIdx = i % depts.length;
            int id = 1000 + i * 3 + rng.nextInt(3);
            String code = String.format("%s%04d", depts[deptIdx], 100 + i);
            String title = topics[rng.nextInt(topics.length)] + " " + (1 + (i % 5));
            String dept = deptFull[deptIdx];
            String instr = instructors[rng.nextInt(instructors.length)];
            int credits = 1 + rng.nextInt(5);
            int sem = 1 + rng.nextInt(8);
            int capacity = 40 + rng.nextInt(81);
            int enrolled = rng.nextInt(capacity + 1);
            double rating = 3.0 + Math.round(rng.nextDouble() * 20.0) / 10.0;

            arr[i] = new Course(id, code, title, dept, instr, credits, sem, capacity, enrolled, rating);
        }

        // Fisher-Yates shuffle on the raw array
        for (int i = n - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            Course tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
        return arr;
    }

    private static Course[] deepCopy(Course[] src) {
        Course[] copy = new Course[src.length];
        for (int i = 0; i < src.length; i++) {
            copy[i] = new Course(src[i]);
        }
        return copy;
    }
}
