package com.brainacad.ecs;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by vibondarenko on 07.03.2016.
 */
public class StudentComparator implements Comparator, Serializable{
    @Override
    public int compare(Object o1, Object o2) {
        return ((Student)o1).getName().compareTo(((Student)o2).getName());
    }
}
