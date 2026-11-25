import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class piyush {
    private static final Scanner input = new Scanner(System.in);
    private static final ArrayList<Course> courses = new ArrayList<>();

    public static void main(String[] args) {
        courses.add(new Course("CS101", "Java Programming", "A. Sharma", 4));
        courses.add(new Course("CS202", "Data Structures", "R. Verma", 3));
        courses.add(new Course("IT105", "Web Development", "N. Singh", 3));

        while (true) {
            System.out.println("\n=== Course Management System ===");
            System.out.println("1. View courses");
            System.out.println("2. Add course");
            System.out.println("3. Search course");
            System.out.println("4. Update course");
            System.out.println("5. Delete course");
            System.out.println("6. Sort courses by title");
            System.out.println("0. Exit");
            int choice = readInt("Choose: ");

            switch (choice) {
                case 1:
                    viewCourses();
                    break;
                case 2:
                    addCourse();
                    break;
                case 3:
                    searchCourse();
                    break;
                case 4:
                    updateCourse();
                    break;
                case 5:
                    deleteCourse();
                    break;
                case 6:
                    courses.sort(Comparator.comparing(course -> course.title.toLowerCase()));
                    System.out.println("Courses sorted by title.");
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Please choose a listed option.");
            }
        }
    }

    private static void viewCourses() {
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }
        System.out.printf("%-10s %-28s %-20s %s%n", "Code", "Title", "Instructor", "Credits");
        System.out.println("---------------------------------------------------------------------");
        for (Course course : courses) {
            System.out.printf("%-10s %-28s %-20s %d%n",
                    course.code, course.title, course.instructor, course.credits);
        }
    }

    private static void addCourse() {
        String code = readText("Course code: ").toUpperCase();
        if (findCourse(code) != null) {
            System.out.println("That course code already exists.");
            return;
        }
        String title = readText("Course title: ");
        String instructor = readText("Instructor: ");
        int credits = readInt("Credits: ");
        if (credits < 1 || credits > 10) {
            System.out.println("Credits must be between 1 and 10.");
            return;
        }
        courses.add(new Course(code, title, instructor, credits));
        System.out.println("Course added.");
    }

    private static void searchCourse() {
        String query = readText("Enter course code or title: ").toLowerCase();
        boolean found = false;
        for (Course course : courses) {
            if (course.code.toLowerCase().contains(query)
                    || course.title.toLowerCase().contains(query)) {
                printCourse(course);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No matching course found.");
        }
    }

    private static void updateCourse() {
        Course course = findCourse(readText("Course code to update: ").toUpperCase());
        if (course == null) {
            System.out.println("Course not found.");
            return;
        }
        String title = readText("New title (Enter to keep current): ");
        String instructor = readText("New instructor (Enter to keep current): ");
        String creditsText = readText("New credits (Enter to keep current): ");
        if (!title.isEmpty()) course.title = title;
        if (!instructor.isEmpty()) course.instructor = instructor;
        if (!creditsText.isEmpty()) {
            try {
                int credits = Integer.parseInt(creditsText);
                if (credits < 1 || credits > 10) {
                    System.out.println("Credits must be between 1 and 10.");
                    return;
                }
                course.credits = credits;
            } catch (NumberFormatException e) {
                System.out.println("Credits must be a whole number.");
                return;
            }
        }
        System.out.println("Course updated.");
    }

    private static void deleteCourse() {
        String code = readText("Course code to delete: ").toUpperCase();
        Course course = findCourse(code);
        if (course == null) {
            System.out.println("Course not found.");
            return;
        }
        courses.remove(course);
        System.out.println("Course deleted.");
    }

    private static Course findCourse(String code) {
        for (Course course : courses) {
            if (course.code.equalsIgnoreCase(code)) return course;
        }
        return null;
    }

    private static void printCourse(Course course) {
        System.out.println(course.code + " | " + course.title + " | "
                + course.instructor + " | " + course.credits + " credits");
    }

    private static String readText(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            String value = readText(prompt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Enter a whole number.");
            }
        }
    }

    private static class Course {
        private final String code;
        private String title;
        private String instructor;
        private int credits;

        private Course(String code, String title, String instructor, int credits) {
            this.code = code;
            this.title = title;
            this.instructor = instructor;
            this.credits = credits;
        }
    }
}
