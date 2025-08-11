package com.brainacad.ecs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public void deleteTrainerFromCourses() {
        Iterator<Course> itr = courses.iterator();
        while(itr.hasNext()) {
            Course course = itr.next();
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
