package com.brainacad.ecs;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuItem {
    private String name;
    //private List<Integer> args = new ArrayList<>();

    public MenuItem(String itemName) {
        this.name = itemName;
    }
    public String getName() {
        return name;
    }
/*
    public void setArgs(List<Integer> args) {
        this.args = args;
    }
*/
    public abstract void run();
}
