package com.brainacad.ecs.entity;

import java.io.*;

public class ElementSystem implements Serializable{
    private static final long serialVersionUID = 100L;
    private int id;
    private String name;
    private String description;

    public ElementSystem(int count, String elementName, String elementDescription) {
        this.id = count;
        this.name = elementName;
        this.description = elementDescription;
    }
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
