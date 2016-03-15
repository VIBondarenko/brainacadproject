package com.brainacad.ecs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Task extends ElementSystem implements Serializable {
    private static final long serialVersionUID = 100L;
    private static int count = 0;
    private Course course;

    public Task(String name, String description, Course course) {
        super(count, name, description);
        this.course = course;
        count++;
    }
    public Course getCourse() {
        return course;
    }
    public static void serializeStatic(ObjectOutputStream oos) throws IOException {
        oos.writeInt(count);
    }
    public static void deserializeStatic(ObjectInputStream ois) throws IOException {
        count = ois.readInt();
    }
    @Override
    public String toString() {
        String className = getClass().getSimpleName();
        String courseName = (course != null)? course.getName():" ";
        return super.toString() + className + " Course: " + courseName + "\n";
    }
}
