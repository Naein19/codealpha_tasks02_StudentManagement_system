/**
 * A data model representing a single grade.
 * It holds not just the score, but the status of the grade
 * (e.g., Graded, Absent, Malpractice).
 */
public class Grade {
    private double score;
    private String status; // "Graded", "Absent", "Malpractice", "Not Graded"

    public Grade(double score, String status) {
        this.score = score;
        this.status = status;
    }

    // Getters
    public double getScore() {
        return score;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        switch (status) {
            case "Absent":
            case "Malpractice":
                return status;
            case "Not Graded":
                return "N/A";
            default:
                return String.valueOf(score);
        }
    }
}