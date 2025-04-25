package com.brainacad.ecs;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


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
    public Trainer getTrainer() {
        return trainer;
    }
    public void setTrainer(Trainer trainer) {
            this.trainer = trainer;
    }
    public List<Student> getStudents() {
        return students;
    }
    public Map<Student, Map<Task, Integer>> getJournal() {
        return journal;
    }
    public Boolean addStudent(Student student) {
        if (getCountPlaces() != 0) {
            students.add(students.size(), student);
            return true;
        }
        return false;
    }
    public void addStudentToJournal(Student student, List<Task> tasks) {
        Iterator itr = tasks.iterator();
        Map<Task, Integer> tasksValue = new TreeMap<>(new TaskComparator());
        while (itr.hasNext()) {
            Task task = (Task) itr.next();
            if (task != null) {
                tasksValue.put(task, 0);
            }
        }
        journal.put(student, tasksValue);
    }
    public void addTaskToJournal(Student student, Task task) {
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
    public void deleteTrainer(){
        trainer = null;
    }
    public int getCountPlaces () {
        return CNT - students.size();
    }
    public String getStudentsList() {
        StringBuffer buf = new StringBuffer();
        Iterator itr = students.iterator();
        while (itr.hasNext()) {
            Student student = (Student)itr.next();
            buf.append("\tID: " + student.getId() + " Name: " + student.getName() + " " + student.getLastName() + "\n");
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
