import java.util.*;
import java.util.stream.Collectors;

/**
 * Main application class.
 * Handles all user interaction, console display, and input parsing.
 */
public class App {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String[] SUBJECTS = { "Maths", "English", "Science", "Social" };

    private final GradeTracker gradeTracker;
    private final Scanner scanner;

    public App() {
        this.gradeTracker = new GradeTracker();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        new App().run();
    }

    public void run() {
        while (true) {
            displayMenu();
            System.out.print(ANSI_BOLD + "Choose an option: " + ANSI_RESET);
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addStudent();
                    break;
                case "2":
                    addSubjectGrades();
                    break;
                case "3":
                    viewStudentDetails();
                    break;
                case "4":
                    displaySummaryReport();
                    break;
                case "5":
                    manageClasses();
                    break;
                case "6":
                    gradeTracker.saveStudents();
                    System.out.println(ANSI_YELLOW + "\nData saved. Exiting application. Goodbye!" + ANSI_RESET);
                    return;
                default:
                    System.out.println(ANSI_RED + "Invalid option. Please try again." + ANSI_RESET);
                    pressEnterToContinue();
            }
        }
    }

    private void displayMenu() {
        clearConsole();
        System.out.println(ANSI_CYAN + "========================================");
        System.out.println("    " + ANSI_BOLD + "Student Grade Tracker System" + ANSI_RESET + ANSI_CYAN);
        System.out.println("========================================" + ANSI_RESET);
        System.out.println(" [1] Add New Student");
        System.out.println(" [2] Enter Student Grades");
        System.out.println(" [3] View Individual Student Details");
        System.out.println(" [4] Display Class Summary Report");
        System.out.println(" [5] Manage Classes");
        System.out.println(" [6] Save and Exit");
        System.out.println("----------------------------------------");
    }

    private void manageClasses() {
        while (true) {
            clearConsole();
            System.out.println(ANSI_YELLOW + "--- Class Management ---" + ANSI_RESET);
            System.out.println(" [1] Add New Class");
            System.out.println(" [2] View All Classes");
            System.out.println(" [3] Back to Main Menu");
            System.out.println("------------------------");
            System.out.print(ANSI_BOLD + "Choose an option: " + ANSI_RESET);
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter new class name: ");
                    String className = scanner.nextLine();
                    if (!className.trim().isEmpty()) {
                        gradeTracker.addClass(className);
                        System.out.println(ANSI_GREEN + "✔ Class '" + className + "' added." + ANSI_RESET);
                    } else {
                        System.out.println(ANSI_RED + "Class name cannot be empty." + ANSI_RESET);
                    }
                    pressEnterToContinue();
                    break;
                case "2":
                    System.out.println(ANSI_YELLOW + "\n--- Available Classes ---" + ANSI_RESET);
                    gradeTracker.getClassNames().forEach(System.out::println);
                    pressEnterToContinue();
                    break;
                case "3":
                    return;
                default:
                    System.out.println(ANSI_RED + "Invalid option." + ANSI_RESET);
                    pressEnterToContinue();
            }
        }
    }

    private void addStudent() {
        System.out.println(ANSI_YELLOW + "\n--- Available Classes ---" + ANSI_RESET);
        List<String> classList = new ArrayList<>(gradeTracker.getClassNames());
        for (int i = 0; i < classList.size(); i++) {
            System.out.printf(" [%d] %s%n", i + 1, classList.get(i));
        }
        System.out.println("-------------------------");
        System.out.print("Choose a class for the new student (enter number): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice > 0 && choice <= classList.size()) {
                String chosenClass = classList.get(choice - 1);
                System.out.print("Enter student's full name: ");
                String name = scanner.nextLine();
                if (!name.trim().isEmpty()) {
                    Student newStudent = gradeTracker.addStudent(name, chosenClass);
                    System.out.println(ANSI_GREEN + "\n✔ Success! Student '" + newStudent.getName() + "' added to "
                            + chosenClass + " with ID: " + newStudent.getStudentId() + ANSI_RESET);
                } else {
                    System.out.println(ANSI_RED + "Student name cannot be empty." + ANSI_RESET);
                }
            } else {
                System.out.println(ANSI_RED + "Invalid class choice." + ANSI_RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "Invalid input. Please enter a number." + ANSI_RESET);
        }
        pressEnterToContinue();
    }

    private void addSubjectGrades() {
        System.out.print("Enter Student ID to add grades (e.g., S1): ");
        String id = scanner.nextLine().toUpperCase();
        Optional<Student> studentOpt = gradeTracker.findStudentById(id);

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            System.out.println("Entering grades for " + ANSI_BOLD + student.getName() + ANSI_RESET);
            for (String subject : SUBJECTS) {
                while (true) {
                    System.out.print("  - Enter grade for " + subject + " (0-100): ");
                    try {
                        double score = Double.parseDouble(scanner.nextLine());
                        if (score >= 0 && score <= 100) {
                            Grade grade;
                            if (score == 0) {
                                grade = handleZeroScore();
                            } else {
                                grade = new Grade(score, "Graded");
                            }
                            student.addSubjectGrade(subject, grade);
                            break;
                        } else {
                            System.out.println(ANSI_RED + "Invalid grade. Must be between 0 and 100." + ANSI_RESET);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(ANSI_RED + "Invalid input. Please enter a number." + ANSI_RESET);
                    }
                }
            }
            System.out.println(
                    ANSI_GREEN + "\n✔ All grades for " + student.getName() + " have been updated." + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "Student with ID '" + id + "' not found." + ANSI_RESET);
        }
        pressEnterToContinue();
    }

    private Grade handleZeroScore() {
        while (true) {
            System.out.println(ANSI_YELLOW + "  Reason for score of 0:" + ANSI_RESET);
            System.out.println("    [1] Genuinely scored zero");
            System.out.println("    [2] Absent for exam");
            System.out.println("    [3] Malpractice reported");
            System.out.print(ANSI_BOLD + "  Please select an option: " + ANSI_RESET);
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    return new Grade(0, "Graded");
                case "2":
                    return new Grade(0, "Absent");
                case "3":
                    return new Grade(0, "Malpractice");
                default:
                    System.out.println(ANSI_RED + "  Invalid choice. Please enter 1, 2, or 3." + ANSI_RESET);
            }
        }
    }

    private void viewStudentDetails() {
        System.out.print("Enter Student ID (e.g., S1): ");
        String id = scanner.nextLine().toUpperCase();
        Optional<Student> studentOpt = gradeTracker.findStudentById(id);

        if (studentOpt.isPresent()) {
            Student s = studentOpt.get();
            System.out.println("\n" + ANSI_YELLOW + "--- Student Details ---" + ANSI_RESET);
            System.out.println(ANSI_BOLD + "ID:        " + ANSI_RESET + s.getStudentId());
            System.out.println(ANSI_BOLD + "Name:      " + ANSI_RESET + s.getName());
            System.out.println(ANSI_BOLD + "Class:     " + ANSI_RESET + s.getStudentClass());
            System.out.println(ANSI_BOLD + "Grades:" + ANSI_RESET);
            for (String subject : SUBJECTS) {
                System.out.printf("  - %-8s: %s%n", subject, s.getSubjectGrades().get(subject).toString());
            }
            System.out.println(ANSI_BOLD + "Total Marks: " + ANSI_RESET + s.getTotalMarks());
            System.out.println(ANSI_BOLD + "Remark:      " + ANSI_RESET + s.getRemark());
            System.out.println(ANSI_YELLOW + "-----------------------" + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "Student with ID '" + id + "' not found." + ANSI_RESET);
        }
        pressEnterToContinue();
    }

    private void displaySummaryReport() {
        System.out.println(ANSI_YELLOW + "\n--- Select Class for Report ---" + ANSI_RESET);
        List<String> classList = new ArrayList<>(gradeTracker.getClassNames());
        for (int i = 0; i < classList.size(); i++) {
            System.out.printf(" [%d] %s%n", i + 1, classList.get(i));
        }
        System.out.println(" [" + (classList.size() + 1) + "] Back to Main Menu");
        System.out.println("-------------------------------");
        System.out.print("Choose a class to generate report (enter number): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice > 0 && choice <= classList.size()) {
                String chosenClass = classList.get(choice - 1);
                List<Student> studentsInClass = gradeTracker.getStudentsByClass(chosenClass);

                studentsInClass.sort(Comparator.comparing(Student::getTotalMarks)
                        .thenComparing(Student::getMathsAndScienceTotal).reversed());

                printReportTable(studentsInClass, chosenClass);

            } else if (choice == classList.size() + 1) {
                return;
            } else {
                System.out.println(ANSI_RED + "Invalid class choice." + ANSI_RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "Invalid input. Please enter a number." + ANSI_RESET);
        }
        pressEnterToContinue();
    }

    private void printReportTable(List<Student> students, String className) {
        clearConsole();
        String headerFormat = "%-5s | %-8s | %-25s | %-11s | %-11s | %-11s | %-11s | %-10s | %-11s%n";
        String line = "---------------------------------------------------------------------------------------------------------------------------------";

        System.out.println(ANSI_CYAN + line.replace("-", "="));
        System.out.println("                                " + ANSI_BOLD + "Class Summary Report for: " + className
                + ANSI_RESET + ANSI_CYAN);
        System.out.println(line.replace("-", "=") + ANSI_RESET);
        System.out.printf(ANSI_BOLD + headerFormat + ANSI_RESET, "Rank", "ID", "Name", "Maths", "English", "Science",
                "Social", "Total", "Remark");
        System.out.println(line);

        if (students.isEmpty()) {
            System.out.println("No students found in this class.");
        } else {
            int rank = 1;
            for (Student s : students) {
                System.out.printf(headerFormat,
                        rank++, s.getStudentId(), s.getName(),
                        s.getSubjectGrades().get("Maths").toString(), s.getSubjectGrades().get("English").toString(),
                        s.getSubjectGrades().get("Science").toString(), s.getSubjectGrades().get("Social").toString(),
                        s.getTotalMarks(), s.getRemark());
            }
            printStatisticsFooter(students);
        }
        System.out.println(line);
    }

    private void printStatisticsFooter(List<Student> students) {
        String line = "---------------------------------------------------------------------------------------------------------------------------------";
        System.out.println(line);

        // --- SECTION 1: Subject Performance (Highest, Lowest, Average) ---
        System.out.println(ANSI_YELLOW + ANSI_BOLD + "Subject Performance Summary:" + ANSI_RESET);
        System.out.printf(ANSI_BOLD + "%-10s | %-30s | %-30s | %-10s%n" + ANSI_RESET, "Subject", "Highest Scorer",
                "Lowest Scorer", "Average");

        for (String subject : SUBJECTS) {
            List<Student> gradedStudents = students.stream()
                    .filter(s -> s.getSubjectGrades().get(subject).getStatus().equals("Graded"))
                    .collect(Collectors.toList());

            String highestStr = "N/A";
            String lowestStr = "N/A";
            String avgStr = "N/A";

            if (!gradedStudents.isEmpty()) {
                double avg = gradedStudents.stream()
                        .mapToDouble(s -> s.getSubjectGrades().get(subject).getScore())
                        .average().orElse(0.0);
                avgStr = String.format("%.2f", avg);

                Student highestScorer = gradedStudents.stream()
                        .max(Comparator.comparing(s -> s.getSubjectGrades().get(subject).getScore())).get();
                highestStr = String.format("%.1f (%s)", highestScorer.getSubjectGrades().get(subject).getScore(),
                        highestScorer.getName());

                Student lowestScorer = gradedStudents.stream()
                        .min(Comparator.comparing(s -> s.getSubjectGrades().get(subject).getScore())).get();
                lowestStr = String.format("%.1f (%s)", lowestScorer.getSubjectGrades().get(subject).getScore(),
                        lowestScorer.getName());
            }
            System.out.printf("%-10s | %-30s | %-30s | %-10s%n", subject, highestStr, lowestStr, avgStr);
        }
        System.out.println(); // Add a blank line for spacing

        // --- SECTION 2: Subject Distribution (Counts) ---
        System.out.println(ANSI_YELLOW + ANSI_BOLD + "Subject Distribution Summary:" + ANSI_RESET);
        System.out.printf(ANSI_BOLD + "%-10s | %-15s | %-15s | %-15s | %-15s%n" + ANSI_RESET, "Subject",
                "Passed (>=25)", "Failed (<25)", "Absent", "Malpractice");
        for (String subject : SUBJECTS) {
            long passed = students.stream().map(s -> s.getSubjectGrades().get(subject))
                    .filter(g -> g.getStatus().equals("Graded") && g.getScore() >= 25).count();
            long failed = students.stream().map(s -> s.getSubjectGrades().get(subject))
                    .filter(g -> g.getStatus().equals("Graded") && g.getScore() < 25).count();
            long absent = students.stream().map(s -> s.getSubjectGrades().get(subject))
                    .filter(g -> g.getStatus().equals("Absent")).count();
            long malpractice = students.stream().map(s -> s.getSubjectGrades().get(subject))
                    .filter(g -> g.getStatus().equals("Malpractice")).count();
            System.out.printf("%-10s | %-15d | %-15d | %-15d | %-15d%n", subject, passed, failed, absent, malpractice);
        }
        System.out.println(); // Add a blank line for spacing

        // --- SECTION 3: Overall Class Summary ---
        System.out.println(ANSI_YELLOW + ANSI_BOLD + "Overall Class Summary:" + ANSI_RESET);
        long totalStudents = students.size();
        long passedCount = students.stream().filter(s -> s.getRemark().equals("Pass")).count();
        long failedCount = students.stream().filter(s -> s.getRemark().equals("Fail")).count();
        long malpracticeCount = students.stream().filter(s -> s.getRemark().equals("Malpractice")).count();
        System.out.printf("Total Students: " + ANSI_BOLD + "%-5d" + ANSI_RESET + " | Passed: " + ANSI_GREEN + "%-5d"
                + ANSI_RESET + " | Failed: " + ANSI_RED + "%-5d" + ANSI_RESET + " | Malpractice: " + ANSI_RED + "%-5d%n"
                + ANSI_RESET, totalStudents, passedCount, failedCount, malpracticeCount);
    }

    private void pressEnterToContinue() {
        System.out.print("\n" + ANSI_YELLOW + "Press Enter to return to the menu..." + ANSI_RESET);
        scanner.nextLine();
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}