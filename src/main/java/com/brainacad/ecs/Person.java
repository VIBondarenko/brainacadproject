package com.brainacad.ecs;

import java.io.*;

public abstract class Person implements Serializable {
    private static final long serialVersionUID = 100L;
    private int id;
    private String name;
    private String lastName;

    public Person(int count, String personName, String personLastName) {
        this.id = count;
        this.name = personName;
        this.lastName = personLastName;
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
    public String getLastName() {
        return lastName;
    }
    @Override
    public String toString() {
        String className = getClass().getSimpleName();
        return "\t" + className + " ID: " + id + "\n" +
                "\t" + className + " First Name: " + name + "\n" +
                "\t" + className + " Last Name: " + lastName + "\n";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        if (id != person.id) return false;
        if (!name.equals(person.name)) return false;
        return lastName.equals(person.lastName);

    }
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }
}
