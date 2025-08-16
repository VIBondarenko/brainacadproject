package com.brainacad.ecs.entity;

import java.io.*;
import java.util.Objects;

public abstract class Person {
    private int id;
    private String name;
    private String lastName;
    private int age;

    public Person(int count, String personName, String personLastName) {
        this.id = count;
        this.name = personName;
        this.lastName = personLastName;
        this.age = 0; // Default age
    }
    
    public Person(int count, String personName, String personLastName, int age) {
        this.id = count;
        this.name = personName;
        this.lastName = personLastName;
        this.age = age;
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
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
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
        if (age != person.age) return false;
        if (!Objects.equals(name, person.name)) return false;
        return Objects.equals(lastName, person.lastName);

    }
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(lastName);
        result = 31 * result + age;
        return result;
    }
}
