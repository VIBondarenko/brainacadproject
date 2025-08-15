package com.brainacad.ecs.entity;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.brainacad.ecs.ElementSystem;
import com.brainacad.ecs.Student;
import com.brainacad.ecs.StudentComparator;
import com.brainacad.ecs.Task;
import com.brainacad.ecs.TaskComparator;
import com.brainacad.ecs.Trainer;


public class Course extends ElementSystem implements Serializable {
    private static final long serialVersionUID = 100L;
    private static int count = 0;
    final static int CNT = 12;
    private Date beginDate;
    private Date endDate;
    private String days;
    private Trainer trainer;

    private List<Student> students = new ArrayList<>();
    private Map<Student, Map<Task, Integer>> journal = new TreeMap<>(new StudentComparator());
    public Course(String name, String description, Date beginDate, Date endDate, String days) {
        super(count, name, description);
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.days = days;
        count++;
    }
    
    // Additional constructor for simple course creation
    public Course() {
        super(count, "", "");
        this.beginDate = new Date();
        this.endDate = new Date();
        this.days = "";
        count++;
    }
    public Trainer getTrainer() {
        return trainer;
    }
    public void setTrainer(Trainer trainer) {
            this.trainer = trainer;
    }
    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }
    public Map<Student, Map<Task, Integer>> getJournal() {
        Map<Student, Map<Task, Integer>> journalCopy = new HashMap<>();
        for (Map.Entry<Student, Map<Task, Integer>> entry : journal.entrySet()) {
            journalCopy.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return journalCopy;
    }
    public Boolean addStudent(Student student) {
        if (student == null) {
            System.err.println("Warning: Cannot add null student to course");
            return false;
        }
        if (getCountPlaces() != 0) {
            students.add(students.size(), student);
            return true;
        }
        return false;
    }
    public void addStudentToJournal(Student student, List<Task> tasks) {
        if (student == null) {
            System.err.println("Warning: Cannot add null student to journal");
            return;
        }
        if (tasks == null) {
            System.err.println("Warning: Cannot add null tasks list to journal");
            return;
        }
        
        Map<Task, Integer> tasksValue = new TreeMap<>(new TaskComparator());
        for (Task task : tasks) {
            if (task != null) {
                tasksValue.put(task, 0);
            }
        }
        journal.put(student, tasksValue);
    }
    public void addTaskToJournal(Student student, Task task) {
        if (student == null || task == null) {
            System.err.println("Warning: Cannot add null student or task to journal");
            return;
        }
        
        if (journal.containsKey(student)) {
            journal.get(student).put(task, 0);
        } else {
            Map<Task, Integer> taskValue = new TreeMap<>(new TaskComparator());
            taskValue.put(task, 0);
            journal.put(student, taskValue);
        }
    }
    public void deleteStudent(Student student){
        students.remove(student);
        deleteStudentFromJournal(student);
    }
    public void deleteStudentFromJournal(Student student) {
        journal.remove(student);
    }
    
    public Date getBeginDate() {
        return beginDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public String getDays() {
        return days;
    }
    
    /**
     * Calculate duration in days between begin and end dates
     * @return duration in days as integer
     */
    public int getDuration() {
        if (beginDate != null && endDate != null) {
            long diffInMillies = Math.abs(endDate.getTime() - beginDate.getTime());
            return (int) (diffInMillies / (24 * 60 * 60 * 1000)) + 1; // +1 to include both start and end days
        }
        return 0; // Default duration if dates are null
    }

    public void setDuration(int duration) {
        if (duration > 0) {
            this.days = String.valueOf(duration);
        } else {
            System.err.println("Invalid duration. Duration must be positive.");
        }
    }
    
    public void deleteTrainer(){
        trainer = null;
    }
    public int getCountPlaces () {
        return CNT - students.size();
    }
    
    public void setCountPlaces(int countPlaces) {
        // Note: This is a conceptual method since count places is calculated
        // In a real implementation, you might want to store max capacity separately
        // For now, this method exists to satisfy the interface but doesn't change behavior
    }
    public String getStudentsList() {
        StringBuilder buf = new StringBuilder();
        for (Student student : students) {
            buf.append("\tID: ").append(student.getId()).append(" Name: ").append(student.getName()).append(" ").append(student.getLastName()).append("\n");
        }
        return buf.toString();
    }
    public void printJournal() {
        for (Map.Entry<Student, Map<Task, Integer>> entryStudent : journal.entrySet()) {
            Student student = (Student) entryStudent.getKey();
            System.out.println("\tStudent: (" + student.getId() + ") " + student.getName() + " " + student.getLastName() + ";");
            for (Map.Entry<Task, Integer> entryTask : entryStudent.getValue().entrySet()) {
                Task task = (Task) entryTask.getKey();
                System.out.println("\t\tTask: " + task.getName() + "; Rating: " + (Integer)entryTask.getValue() + ";");
            }
        }
    }
    public void saveJournal(String name) {
        try (FileWriter output = new FileWriter(name)) {
            for (Map.Entry<Student, Map<Task, Integer>> entryStudent : journal.entrySet()) {
                Student student = (Student) entryStudent.getKey();
                output.write("Student: (" + student.getId() + ") " + student.getName() + " " + student.getLastName() + ";\n");
                for (Map.Entry<Task, Integer> entryTask : entryStudent.getValue().entrySet()) {
                    Task task = (Task) entryTask.getKey();
                    output.write("\t" + task.getName() + "; Rating: " + (Integer)entryTask.getValue() + ";\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void serializeStatic(ObjectOutputStream oos) throws IOException {
        oos.writeInt(count);
    }
    public static void deserializeStatic(ObjectInputStream ois) throws IOException {
        count = ois.readInt();
    }
    @Override
    public String toString() {
        DateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String str = (trainer != null) ? trainer.getName() + " " + trainer.getLastName() : " ";
        return super.toString() +
                "\tStart Date: " + formatDate.format(beginDate) + "\n" +
                "\tEnd Date: " + formatDate.format(endDate) + "\n" +
                "\tDays: " + days + "\n" +
                "\tTrainer: " + str + "\n";
    }
}
