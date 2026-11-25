package com.dsa.clms.algorithms;

import com.dsa.clms.model.Course;

/**
 * Encapsulates the outcome, performance metrics, and optional execution trace
 * of a searching algorithm run on the Course array.
 */
public class SearchResult {

    private final String algorithmName;
    private final String queryDescription;
    private final int foundIndex;
    private final int[] matchedIndices;
    private final Course[] matchedCourses;
    private final long comparisons;
    private final long executionTimeNs;
    private final String timeComplexity;
    private final String notes;
    private final String traceLog;

    public SearchResult(
            String algorithmName,
            String queryDescription,
            int foundIndex,
            int[] matchedIndices,
            Course[] matchedCourses,
            long comparisons,
            long executionTimeNs,
            String timeComplexity,
            String notes,
            String traceLog) {
        this.algorithmName = algorithmName;
        this.queryDescription = queryDescription;
        this.foundIndex = foundIndex;
        this.matchedIndices = matchedIndices == null ? new int[0] : matchedIndices;
        this.matchedCourses = matchedCourses == null ? new Course[0] : matchedCourses;
        this.comparisons = comparisons;
        this.executionTimeNs = executionTimeNs;
        this.timeComplexity = timeComplexity;
        this.notes = notes == null ? "" : notes;
        this.traceLog = traceLog == null ? "" : traceLog;
    }

    public boolean isFound() {
        return foundIndex >= 0 || matchedCourses.length > 0;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public String getQueryDescription() {
        return queryDescription;
    }

    public int getFoundIndex() {
        return foundIndex;
    }

    public int[] getMatchedIndices() {
        return matchedIndices;
    }

    public Course[] getMatchedCourses() {
        return matchedCourses;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getExecutionTimeNs() {
        return executionTimeNs;
    }

    public double getExecutionTimeUs() {
        return executionTimeNs / 1_000.0;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public String getNotes() {
        return notes;
    }

    public String getTraceLog() {
        return traceLog;
    }
}
