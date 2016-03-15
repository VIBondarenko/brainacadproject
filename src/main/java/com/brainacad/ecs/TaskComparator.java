package com.brainacad.ecs;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by vibondarenko on 07.03.2016.
 */
public class TaskComparator implements Comparator, Serializable {
    @Override
    public int compare(Object o1, Object o2) {
        return ((Task)o1).getName().compareTo(((Task)o2).getName());
    }
}
