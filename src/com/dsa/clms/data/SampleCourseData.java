package com.dsa.clms.data;

import com.dsa.clms.ds.CourseArray;
import com.dsa.clms.model.Course;

/**
 * Provides a realistic, intentionally unsorted sample catalog of university courses
 * so searching and sorting behaviors can be demonstrated immediately.
 */
public final class SampleCourseData {

    private SampleCourseData() {}

    public static Course[] getSampleCourses() {
        return new Course[] {
                new Course(108, "CS301", "Operating Systems", "Computer Science", "Dr. A. Sharma", 4, 5, 90, 84, 4.7),
                new Course(102, "CS102", "Data Structures & Algorithms", "Computer Science", "Prof. R. Verma", 4, 2, 120, 118, 4.9),
                new Course(215, "MA201", "Discrete Mathematics", "Mathematics", "Dr. S. Iyer", 4, 3, 100, 76, 4.3),
                new Course(304, "AI401", "Machine Learning Foundations", "Artificial Intel", "Dr. M. Gupta", 4, 7, 80, 79, 4.8),
                new Course(119, "EC204", "Digital Logic Design", "Electronics", "Prof. K. Nair", 3, 4, 75, 62, 4.1),
                new Course(105, "CS201", "Object Oriented Programming", "Computer Science", "Prof. N. Kulkarni", 4, 3, 110, 104, 4.6),
                new Course(410, "DS305", "Big Data Analytics", "Data Science", "Dr. P. Rao", 3, 6, 70, 68, 4.5),
                new Course(112, "CS304", "Database Management Systems", "Computer Science", "Dr. A. Sharma", 4, 5, 100, 95, 4.8),
                new Course(201, "MA101", "Calculus & Linear Algebra", "Mathematics", "Prof. V. Joshi", 4, 1, 130, 121, 4.2),
                new Course(125, "CS402", "Compiler Design", "Computer Science", "Dr. T. Banerjee", 4, 7, 60, 48, 4.4),
                new Course(309, "AI405", "Deep Learning & Neural Nets", "Artificial Intel", "Dr. M. Gupta", 4, 8, 75, 74, 4.9),
                new Course(115, "CS306", "Computer Networks", "Computer Science", "Prof. R. Verma", 4, 5, 95, 89, 4.5),
                new Course(502, "HU101", "Technical Communication", "Humanities", "Dr. L. Menon", 2, 1, 120, 98, 3.9),
                new Course(131, "CS410", "Cloud & Distributed Systems", "Computer Science", "Prof. N. Kulkarni", 3, 8, 85, 81, 4.7),
                new Course(222, "MA302", "Probability & Statistics", "Mathematics", "Dr. S. Iyer", 3, 4, 90, 82, 4.4),
                new Course(318, "EC301", "Microprocessors & Interfacing", "Electronics", "Prof. K. Nair", 4, 5, 70, 55, 4.0),
                new Course(402, "DS202", "Statistical Computing", "Data Science", "Dr. P. Rao", 3, 4, 65, 59, 4.3),
                new Course(101, "CS101", "Intro to Programming", "Computer Science", "Prof. R. Verma", 4, 1, 140, 136, 4.6),
                new Course(140, "CS415", "Cybersecurity & Cryptography", "Computer Science", "Dr. T. Banerjee", 3, 7, 80, 77, 4.8),
                new Course(508, "HU205", "Engineering Economics", "Humanities", "Dr. L. Menon", 2, 3, 100, 71, 3.8),
                new Course(110, "CS205", "Computer Organization", "Computer Science", "Prof. V. Joshi", 3, 3, 90, 74, 4.2),
                new Course(325, "AI302", "Natural Language Processing", "Artificial Intel", "Dr. M. Gupta", 3, 6, 70, 66, 4.7),
                new Course(418, "DS401", "Data Visualization", "Data Science", "Dr. P. Rao", 2, 7, 60, 53, 4.5),
                new Course(121, "CS310", "Software Engineering", "Computer Science", "Prof. N. Kulkarni", 3, 6, 100, 91, 4.4)
        };
    }

    public static void loadInto(CourseArray courseArray) {
        courseArray.clear();
        Course[] samples = getSampleCourses();
        for (int i = 0; i < samples.length; i++) {
            courseArray.insertAtEnd(new Course(samples[i]));
        }
    }
}
