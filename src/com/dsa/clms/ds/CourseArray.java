package com.dsa.clms.ds;

import com.dsa.clms.model.Course;

/**
 * Custom array-backed Abstract Data Type (ADT) for storing and managing Course objects.
 * Implemented directly on top of a raw Java array (Course[]) without using java.util.ArrayList
 * or Collections, explicitly tracking element shifts, array resizes, and capacity utilization.
 */
public class CourseArray {

    public static final int DEFAULT_CAPACITY = 16;

    private Course[] elements;
    private int size;
    private int capacity;
    private boolean autoResize;

    // DSA instrumentation counters
    private int lastShiftCount;
    private int resizeCount;
    private int totalInsertions;
    private int totalDeletions;

    public CourseArray() {
        this(DEFAULT_CAPACITY, true);
    }

    public CourseArray(int initialCapacity, boolean autoResize) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity must be at least 1.");
        }
        this.capacity = initialCapacity;
        this.elements = new Course[this.capacity];
        this.size = 0;
        this.autoResize = autoResize;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public boolean isAutoResize() {
        return autoResize;
    }

    public void setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
    }

    public int getLastShiftCount() {
        return lastShiftCount;
    }

    public int getResizeCount() {
        return resizeCount;
    }

    public int getTotalInsertions() {
        return totalInsertions;
    }

    public int getTotalDeletions() {
        return totalDeletions;
    }

    public double getLoadFactorPercent() {
        return capacity == 0 ? 0.0 : (size * 100.0) / capacity;
    }

    public Course get(int index) {
        validateExistingIndex(index);
        return elements[index];
    }

    public void set(int index, Course updatedCourse) {
        validateExistingIndex(index);
        if (updatedCourse == null) {
            throw new IllegalArgumentException("Course cannot be null.");
        }
        elements[index] = updatedCourse;
    }

    /**
     * Inserts a course at the end of the array.
     * Time Complexity: O(1) amortized (O(n) if dynamic resize occurs).
     */
    public boolean insertAtEnd(Course course) {
        return insertAtIndex(size, course);
    }

    /**
     * Inserts a course at a specific 0-based index by shifting elements right.
     * Time Complexity: O(n) worst case when inserting at index 0.
     */
    public boolean insertAtIndex(int index, Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null.");
        }
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    "Invalid insertion index " + index + ". Valid range: [0, " + size + "]");
        }
        if (containsCourseCode(course.getCourseCode())) {
            throw new IllegalArgumentException(
                    "Duplicate course code: " + course.getCourseCode());
        }
        if (containsCourseId(course.getCourseId())) {
            throw new IllegalArgumentException(
                    "Duplicate course ID: " + course.getCourseId());
        }

        if (size == capacity) {
            if (!autoResize) {
                return false;
            }
            resize(capacity * 2);
        }

        int shifts = 0;
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
            shifts++;
        }
        elements[index] = course;
        size++;
        lastShiftCount = shifts;
        totalInsertions++;
        return true;
    }

    /**
     * Deletes and returns the course at the specified index by shifting trailing elements left.
     * Time Complexity: O(n) worst case when deleting index 0.
     */
    public Course deleteAtIndex(int index) {
        validateExistingIndex(index);
        Course removed = elements[index];

        int shifts = 0;
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
            shifts++;
        }
        elements[size - 1] = null;
        size--;
        lastShiftCount = shifts;
        totalDeletions++;
        return removed;
    }

    /**
     * Deletes the first course matching the given course code.
     */
    public Course deleteByCourseCode(String courseCode) {
        int idx = indexOfCourseCode(courseCode);
        if (idx == -1) {
            return null;
        }
        return deleteAtIndex(idx);
    }

    /**
     * Resizes the underlying array manually to the requested capacity.
     */
    public void resize(int newCapacity) {
        if (newCapacity < size) {
            throw new IllegalArgumentException(
                    "New capacity (" + newCapacity + ") cannot be smaller than current size (" + size + ").");
        }
        if (newCapacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1.");
        }
        Course[] newArray = new Course[newCapacity];
        for (int i = 0; i < size; i++) {
            newArray[i] = elements[i];
        }
        this.elements = newArray;
        this.capacity = newCapacity;
        this.resizeCount++;
    }

    /**
     * Reverses the current elements in-place using two pointers.
     * Time Complexity: O(n), Auxiliary Space: O(1).
     */
    public void reverseInPlace() {
        int left = 0;
        int right = size - 1;
        while (left < right) {
            Course temp = elements[left];
            elements[left] = elements[right];
            elements[right] = temp;
            left++;
            right--;
        }
    }

    /**
     * Checks if the array is currently sorted according to the specified key and order.
     */
    public boolean isSortedBy(Course.SortKey key, Course.SortOrder order) {
        for (int i = 0; i < size - 1; i++) {
            if (elements[i].compareTo(elements[i + 1], key, order) > 0) {
                return false;
            }
        }
        return true;
    }

    public int indexOfCourseCode(String courseCode) {
        if (courseCode == null) {
            return -1;
        }
        String target = courseCode.trim();
        for (int i = 0; i < size; i++) {
            if (elements[i].getCourseCode().equalsIgnoreCase(target)) {
                return i;
            }
        }
        return -1;
    }

    public boolean containsCourseCode(String courseCode) {
        return indexOfCourseCode(courseCode) != -1;
    }

    public boolean containsCourseId(int courseId) {
        for (int i = 0; i < size; i++) {
            if (elements[i].getCourseId() == courseId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a shallow-cloned dense array of length == size, containing deep-copied Course objects.
     */
    public Course[] toClonedArray() {
        Course[] copy = new Course[size];
        for (int i = 0; i < size; i++) {
            copy[i] = new Course(elements[i]);
        }
        return copy;
    }

    /**
     * Returns a direct dense reference array of length == size for read-only traversal/analytics.
     */
    public Course[] toDenseArray() {
        Course[] copy = new Course[size];
        for (int i = 0; i < size; i++) {
            copy[i] = elements[i];
        }
        return copy;
    }

    /**
     * Replaces internal array contents with a validated array (e.g., after sorting).
     */
    public void replaceContents(Course[] newCourses) {
        if (newCourses == null) {
            throw new IllegalArgumentException("Input array cannot be null.");
        }
        if (newCourses.length > capacity) {
            resize(Math.max(capacity * 2, newCourses.length));
        }
        for (int i = 0; i < newCourses.length; i++) {
            elements[i] = newCourses[i];
        }
        for (int i = newCourses.length; i < size; i++) {
            elements[i] = null;
        }
        this.size = newCourses.length;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        this.size = 0;
        this.lastShiftCount = 0;
    }

    private void validateExistingIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " out of bounds for array size " + size);
        }
    }
}
