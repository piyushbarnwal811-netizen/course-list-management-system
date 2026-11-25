package com.dsa.clms.data;

import com.dsa.clms.ds.CourseArray;
import com.dsa.clms.model.Course;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Simple CSV persistence utility for saving and loading CourseArray contents.
 */
public final class CsvStorage {

    private static final String HEADER =
            "courseId,courseCode,title,department,instructor,credits,semester,capacity,enrolled,rating";

    private CsvStorage() {}

    public static void save(CourseArray courseArray, String filePath) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER);
            writer.newLine();
            for (int i = 0; i < courseArray.size(); i++) {
                writer.write(courseArray.get(i).toCsvRow());
                writer.newLine();
            }
        }
    }

    public static int loadInto(CourseArray courseArray, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        CourseArray loaded = new CourseArray(Math.max(16, courseArray.capacity()), courseArray.isAutoResize());
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    if (line.trim().toLowerCase().startsWith("courseid,")) {
                        continue;
                    }
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                Course c = Course.fromCsvRow(line);
                loaded.insertAtEnd(c);
                count++;
            }
        }

        courseArray.replaceContents(loaded.toDenseArray());
        return count;
    }
}
