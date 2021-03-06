package com.brainacad.ecs;

import java.io.*;

public class ElementSystem implements Serializable{
    private static final long serialVersionUID = 100L;
    private int id;
    private String name;
    private String description;

    public ElementSystem(int count, String name, String description) {
        this.id = count;
        this.name = name;
        this.description = description;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        String className = getClass().getSimpleName();
        return "\t" + className + " ID: " + id + "\n" +
               "\t" + className + " Name: " + name + "\n" +
               "\t" + className + " Description: " + description + "\n";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementSystem)) return false;

        ElementSystem that = (ElementSystem) o;

        if (id != that.id) return false;
        if (!name.equals(that.name)) return false;
        return description.equals(that.description);

    }
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }
}
