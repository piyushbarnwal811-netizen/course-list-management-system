package com.dsa.clms.algorithms;

import com.dsa.clms.model.Course;

/**
 * Encapsulates metrics and output of a sorting algorithm execution on Course[].
 */
public class SortResult {

    private final String algorithmName;
    private final Course.SortKey sortKey;
    private final Course.SortOrder sortOrder;
    private final int elementCount;
    private final long comparisons;
    private final long swapsOrWrites;
    private final long passes;
    private final long executionTimeNs;
    private final boolean stable;
    private final String timeComplexity;
    private final String spaceComplexity;
    private final Course[] sortedArray;
    private final String traceLog;

    public SortResult(
            String algorithmName,
            Course.SortKey sortKey,
            Course.SortOrder sortOrder,
            int elementCount,
            long comparisons,
            long swapsOrWrites,
            long passes,
            long executionTimeNs,
            boolean stable,
            String timeComplexity,
            String spaceComplexity,
            Course[] sortedArray,
            String traceLog) {
        this.algorithmName = algorithmName;
        this.sortKey = sortKey;
        this.sortOrder = sortOrder;
        this.elementCount = elementCount;
        this.comparisons = comparisons;
        this.swapsOrWrites = swapsOrWrites;
        this.passes = passes;
        this.executionTimeNs = executionTimeNs;
        this.stable = stable;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
        this.sortedArray = sortedArray;
        this.traceLog = traceLog == null ? "" : traceLog;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public Course.SortKey getSortKey() {
        return sortKey;
    }

    public Course.SortOrder getSortOrder() {
        return sortOrder;
    }

    public int getElementCount() {
        return elementCount;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwapsOrWrites() {
        return swapsOrWrites;
    }

    public long getPasses() {
        return passes;
    }

    public long getExecutionTimeNs() {
        return executionTimeNs;
    }

    public double getExecutionTimeMs() {
        return executionTimeNs / 1_000_000.0;
    }

    public double getExecutionTimeUs() {
        return executionTimeNs / 1_000.0;
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

    public Course[] getSortedArray() {
        return sortedArray;
    }

    public String getTraceLog() {
        return traceLog;
    }
}
