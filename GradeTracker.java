import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Handles all business logic for managing students, classes, and grades.
 */
public class GradeTracker {
    private static final String DATA_FILE = "students.json";
    private Map<String, Student> students;
    private Set<String> classNames;
    private AtomicInteger nextId;
    private Gson gson;

    public GradeTracker() {
        this.gson = new GsonBuilder().create();
        this.students = new HashMap<>();
        this.classNames = new HashSet<>();
        loadData();
        if (classNames.isEmpty()) {
            initializeDefaultClasses();
        }
        this.nextId = new AtomicInteger(calculateNextId());
    }

    private void initializeDefaultClasses() {
        classNames.add("First Year");
        classNames.add("Second Year");
        classNames.add("Third Year");
        classNames.add("Fourth Year");
    }

    public void addClass(String className) {
        classNames.add(className);
    }

    public Set<String> getClassNames() {
        return classNames;
    }

    public Student addStudent(String name, String className) {
        String id = "S" + nextId.getAndIncrement();
        Student student = new Student(id, name, className);
        students.put(id, student);
        return student;
    }

    public Optional<Student> findStudentById(String studentId) {
        return Optional.ofNullable(students.get(studentId.toUpperCase()));
    }

    public List<Student> getStudentsByClass(String className) {
        return students.values().stream()
                .filter(student -> student.getStudentClass().equalsIgnoreCase(className))
                .collect(Collectors.toList());
    }

    public void saveStudents() {
        // Create a wrapper object to save both students and class names
        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put("students", students);
        dataToSave.put("classNames", classNames);

        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            gson.toJson(dataToSave, writer);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadData() {
        if (Files.exists(Paths.get(DATA_FILE))) {
            try (FileReader reader = new FileReader(DATA_FILE)) {
                Type type = new TypeToken<HashMap<String, Object>>() {
                }.getType();
                Map<String, Object> loadedData = gson.fromJson(reader, type);

                // Use Gson's internal converter to get the correct types
                String studentsJson = gson.toJson(loadedData.get("students"));
                String classesJson = gson.toJson(loadedData.get("classNames"));

                Type studentMapType = new TypeToken<HashMap<String, Student>>() {
                }.getType();
                Type classSetType = new TypeToken<HashSet<String>>() {
                }.getType();

                this.students = gson.fromJson(studentsJson, studentMapType);
                this.classNames = gson.fromJson(classesJson, classSetType);

                if (this.students == null)
                    this.students = new HashMap<>();
                if (this.classNames == null)
                    this.classNames = new HashSet<>();

            } catch (Exception e) {
                System.err.println("Error loading data, starting fresh: " + e.getMessage());
            }
        }
    }

    private int calculateNextId() {
        if (students.isEmpty())
            return 1;
        return students.keySet().stream()
                .mapToInt(id -> Integer.parseInt(id.substring(1)))
                .max()
                .orElse(0) + 1;
    }
}