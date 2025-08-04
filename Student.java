import java.util.HashMap;
import java.util.Map;

/**
 * Updated Data Model for a single student.
 * Now uses the Grade class to store detailed grade information.
 */
public class Student {
    private String studentId;
    private String name;
    private String studentClass;
    private Map<String, Grade> subjectGrades;

    public Student(String studentId, String name, String studentClass) {
        this.studentId = studentId;
        this.name = name;
        this.studentClass = studentClass;
        this.subjectGrades = new HashMap<>();
        // Initialize subjects with a placeholder Grade object
        for (String subject : App.SUBJECTS) {
            this.subjectGrades.put(subject, new Grade(-1, "Not Graded"));
        }
    }

    // Getters
    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public Map<String, Grade> getSubjectGrades() {
        return subjectGrades;
    }

    /**
     * Adds or updates a grade for a specific subject.
     * 
     * @param subject The subject name.
     * @param grade   The Grade object containing score and status.
     */
    public void addSubjectGrade(String subject, Grade grade) {
        this.subjectGrades.put(subject, grade);
    }

    /**
     * Calculates the total marks for the student. Treats non-graded scores as 0.
     * 
     * @return The sum of all subject scores.
     */
    public double getTotalMarks() {
        return subjectGrades.values().stream()
                .mapToDouble(Grade::getScore)
                .filter(score -> score >= 0) // Only sum valid scores (0 or higher)
                .sum();
    }

    /**
     * Calculates the sum of Maths and Science marks for tie-breaking.
     * 
     * @return The sum of scores for Maths and Science.
     */
    public double getMathsAndScienceTotal() {
        double maths = subjectGrades.get("Maths").getScore();
        double science = subjectGrades.get("Science").getScore();
        return (maths > 0 ? maths : 0) + (science > 0 ? science : 0);
    }

    /**
     * Determines the student's final remark. Malpractice overrides all else.
     * 
     * @return "Malpractice", "Pass", or "Fail".
     */
    public String getRemark() {
        boolean hasMalpractice = subjectGrades.values().stream()
                .anyMatch(grade -> grade.getStatus().equals("Malpractice"));

        if (hasMalpractice) {
            return "Malpractice";
        }

        return getTotalMarks() > 100 ? "Pass" : "Fail";
    }
}