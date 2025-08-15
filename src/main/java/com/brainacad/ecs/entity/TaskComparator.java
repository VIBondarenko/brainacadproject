package com.brainacad.ecs.entity;

import java.io.Serializable;
import java.util.Comparator;

public class TaskComparator implements Comparator<Task>, Serializable {
    @Override
    public int compare(Task o1, Task o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;
        
        String name1 = o1.getName();
        String name2 = o2.getName();
        
        if (name1 == null && name2 == null) return 0;
        if (name1 == null) return -1;
        if (name2 == null) return 1;
        
        return name1.compareTo(name2);
    }
}
