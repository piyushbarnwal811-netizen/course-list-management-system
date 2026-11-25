package com.dsa.clms.model;

/**
 * Represents a university course entity in the Course List Management System.
 * Supports multi-field comparisons for sorting and searching algorithms.
 */
public class Course {

    public enum SortKey {
        COURSE_ID("Course ID"),
        COURSE_CODE("Course Code"),
        TITLE("Course Title"),
        DEPARTMENT("Department"),
        CREDITS("Credits"),
        ENROLLED("Enrolled Students"),
        RATING("Rating");

        private final String displayName;

        SortKey(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum SortOrder {
        ASC("Ascending"),
        DESC("Descending");

        private final String displayName;

        SortOrder(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private int courseId;
    private String courseCode;
    private String title;
    private String department;
    private String instructor;
    private int credits;
    private int semester;
    private int capacity;
    private int enrolled;
    private double rating;

    public Course(
            int courseId,
            String courseCode,
            String title,
            String department,
            String instructor,
            int credits,
            int semester,
            int capacity,
            int enrolled,
            double rating) {
        this.courseId = courseId;
        this.courseCode = courseCode == null ? "" : courseCode.trim().toUpperCase();
        this.title = title == null ? "" : title.trim();
        this.department = department == null ? "" : department.trim();
        this.instructor = instructor == null ? "" : instructor.trim();
        this.credits = credits;
        this.semester = semester;
        this.capacity = capacity;
        this.enrolled = enrolled;
        this.rating = rating;
    }

    /** Copy constructor to prevent shared-state mutation during algorithm benchmarks. */
    public Course(Course other) {
        this(
                other.courseId,
                other.courseCode,
                other.title,
                other.department,
                other.instructor,
                other.credits,
                other.semester,
                other.capacity,
                other.enrolled,
                other.rating);
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode == null ? "" : courseCode.trim().toUpperCase();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title.trim();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department == null ? "" : department.trim();
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor == null ? "" : instructor.trim();
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(int enrolled) {
        this.enrolled = enrolled;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getOccupancyPercent() {
        if (capacity <= 0) {
            return 0.0;
        }
        return (enrolled * 100.0) / capacity;
    }

    /**
     * Compares this course with another based on the given key and order.
     */
    public int compareTo(Course other, SortKey key, SortOrder order) {
        int cmp;
        switch (key) {
            case COURSE_ID:
                cmp = Integer.compare(this.courseId, other.courseId);
                break;
            case COURSE_CODE:
                cmp = this.courseCode.compareToIgnoreCase(other.courseCode);
                break;
            case TITLE:
                cmp = this.title.compareToIgnoreCase(other.title);
                break;
            case DEPARTMENT:
                cmp = this.department.compareToIgnoreCase(other.department);
                break;
            case CREDITS:
                cmp = Integer.compare(this.credits, other.credits);
                break;
            case ENROLLED:
                cmp = Integer.compare(this.enrolled, other.enrolled);
                break;
            case RATING:
                cmp = Double.compare(this.rating, other.rating);
                break;
            default:
                cmp = this.courseCode.compareToIgnoreCase(other.courseCode);
        }
        return order == SortOrder.ASC ? cmp : -cmp;
    }

    public String toCsvRow() {
        return String.join(
                ",",
                String.valueOf(courseId),
                escapeCsv(courseCode),
                escapeCsv(title),
                escapeCsv(department),
                escapeCsv(instructor),
                String.valueOf(credits),
                String.valueOf(semester),
                String.valueOf(capacity),
                String.valueOf(enrolled),
                String.format(java.util.Locale.US, "%.2f", rating));
    }

    public static Course fromCsvRow(String csvLine) {
        String[] parts = splitCsvLine(csvLine);
        if (parts.length < 10) {
            throw new IllegalArgumentException("Invalid CSV row: expected 10 columns, got " + parts.length);
        }
        return new Course(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                parts[4].trim(),
                Integer.parseInt(parts[5].trim()),
                Integer.parseInt(parts[6].trim()),
                Integer.parseInt(parts[7].trim()),
                Integer.parseInt(parts[8].trim()),
                Double.parseDouble(parts[9].trim()));
    }

    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static String[] splitCsvLine(String line) {
        String[] result = new String[10];
        int fieldIdx = 0;
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes && fieldIdx < 9) {
                result[fieldIdx++] = current.toString();
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        if (fieldIdx < 10) {
            result[fieldIdx++] = current.toString();
        }
        String[] trimmed = new String[fieldIdx];
        for (int i = 0; i < fieldIdx; i++) {
            trimmed[i] = result[i];
        }
        return trimmed;
    }

    @Override
    public String toString() {
        return String.format(
                "[#%d | %s] %s (%s) - %s | Cr:%d Sem:%d | %d/%d | Rating: %.1f",
                courseId, courseCode, title, department, instructor, credits, semester, enrolled, capacity, rating);
    }
}
