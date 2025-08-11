package com.brainacad.ecs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Student extends Person implements Serializable {
    private static int count = 0;
    private List<Course> courses = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();

    public Student(String firstName, String lastName) {
        super(count, firstName, lastName);
        count++;
    }
    public void addCourse(Course course) {
        if (course == null) {
            System.err.println("Warning: Cannot add null course to student");
            return;
        }
        if (Utilities.searchById(courses, course.getId()) == null) {
            courses.add(courses.size(), course);
        } else {
            System.err.println("Course already in list");
        }
    }
    public boolean addTask(Task task) {
        if (task == null) {
            System.err.println("Warning: Cannot add null task to student");
            return false;
        }
        if (Utilities.searchById(tasks, task.getId()) == null ) {
            tasks.add(tasks.size(), task);
        } else {
            return false;
        }
        return true;
    }
    public void addTasks(List<Task> tasks, int courseId) {
        if (tasks == null) {
            System.err.println("Warning: Cannot add null tasks list to student");
            return;
        }
        
        Iterator<Task> itr = tasks.iterator();
        while (itr.hasNext()) {
            Task task = itr.next();
            if (task != null && task.getCourse() != null) {
                if (task.getCourse().getId() == courseId) {
                    addTask(task);
                }
            }
        }
    }
    public boolean deleteCourse(Course course) {
        return courses.remove(course);
    }
    public boolean deleteTask(Task task) {
        return tasks.remove(task);
    }
    public boolean deleteTasks(List<Task> tasks, int courseId) {
        Iterator itr = tasks.iterator();
        while (itr.hasNext()) {
            Task task = (Task) itr.next();
            if (task != null) {
                if (task.getCourse().getId() == courseId) {
                    deleteTask(task);
                }
            }
        }
        return true;
    }
    public void deleteStudentFromCourses() {
        Iterator itr = courses.iterator();
        while(itr.hasNext()) {
            Course course = (Course)itr.next();
            course.deleteStudent(this);
        }
    }
    public List<Course> getCourses() {
        return courses;
    }
    public List<Task> getTasks(int courseId) {
        List<Task> courseTasks = new ArrayList<>();
        Iterator itr = tasks.iterator();
        while (itr.hasNext()) {
            Task task = (Task) itr.next();
            if (task.getCourse().getId() == courseId) {
                courseTasks.add(courseTasks.size(), task);
            }
        }
        return courseTasks;
    }
    public static void serializeStatic(ObjectOutputStream oos) throws IOException {
        oos.writeInt(count);
    }
    public static void deserializeStatic(ObjectInputStream ois) throws IOException {
        count = ois.readInt();
    }
    @Override
    public String toString() {
        return super.toString() +
                "\tCourses: " + Utilities.listToString(courses) + "\n" +
                "\tTasks: " + Utilities.listToString(tasks) + "\n";
    }
}
