package com.brainacad.ecs.entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.brainacad.ecs.entity.Course;

public class Trainer extends Person implements Serializable {
    private static final long serialVersionUID = 100L;
    private static int count = 0;
    private List<Course> courses = new ArrayList<>();

    public Trainer(String firstName, String lastName) {
        super(count, firstName, lastName);
        count++;
    }
    public void addCourse(Course course) {
        if (Utilities.searchById(courses, course.getId()) == null) {
            courses.add(course);
        } else {
            System.err.println("Course already in list");
        }
    }
    
    public boolean deleteCourse(Course course) {
        return courses.remove(course);
    }
    
    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }
    public void deleteTrainerFromCourses() {
        for (Course course : courses) {
            if (course != null) {
                course.deleteTrainer();
            }
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
        return super.toString() +
                "\tCourses: " + Utilities.listToString(courses) + "\n";
    }
}
