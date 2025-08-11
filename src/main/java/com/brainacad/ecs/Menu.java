package com.brainacad.ecs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.brainacad.ecs.Utilities.*;

public class Menu {
    private static final String FORMAT_MENU = "%s. %s\n";
    private String name;
    private boolean isExit = false;
    private List<MenuItem> items = new ArrayList<>();

    public Menu(String menuName) {
        this.name = menuName;
    }
    public void setExit(boolean exit) {
        isExit = exit;
    }
    private int parseCommand(String command) {
        String strCmdItem = null;

        final String space = " ";
        String[] cmd = command.split("[ ]");
        Integer valueInt;
        switch (cmd.length) {
            case 1:
                strCmdItem = cmd[cmd.length - 1];
                break;
            case 2:
                strCmdItem = cmd[0] + space + cmd[1];
                break;
            case 3:
                valueInt = isDigit(cmd[2]);
                if (valueInt != null) {
                    strCmdItem = cmd[0] + space + cmd[1]; //cmd[2]
                    for (int i = 2; i < cmd.length; i--) {

                    }
                } else {
                    strCmdItem = cmd[0] + space + cmd[1] + space + cmd[2];
                }
                break;
            case 4:
                valueInt = isDigit(cmd[2]);
                if (valueInt != null) {
                    strCmdItem = cmd[0] + space + cmd[1]; //cmd[2], cmd[3]
                    for (int i = 2; i < cmd.length; i--) {

                    }
                } else {
                    strCmdItem = cmd[0] + space + cmd[1] + space + cmd[2]; //cmd[3]
                    for (int i = 3; i < cmd.length; i--) {

                    }
                }
                break;
            case 5:
                valueInt = isDigit(cmd[2]);
                if (valueInt != null) {
                    strCmdItem = cmd[0] + space + cmd[1]; //cmd[2], cmd[3], cmd[4]
                    for (int i = 2; i < cmd.length; i--) {

                    }
                } else {
                    strCmdItem = cmd[0] + space + cmd[1] + space + cmd[2]; //cmd[3], cmd[4]
                    for (int i = 3; i < cmd.length; i--) {

                    }
                }
        }

        for (MenuItem elem : items) {
            if (elem != null && strCmdItem != null && strCmdItem.equals(elem.getName())) {
                int ind = items.indexOf(elem);
                return ind;
            }
        }
        return items.size();
    }
    public Menu addItem(MenuItem item) {
        items.add(items.size(), item);
        return this;
    }
    private void showMenu() {
        System.out.println(name);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < items.size(); i++) {
            buffer.append(String.format(FORMAT_MENU, (i + 1), items.get(i).getName()));
        }
        System.out.print(buffer.toString());
    }
    public void run() {
        while (!isExit) {
            showMenu();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("\nEnter command: ");
            String line;
            try {
                line = reader.readLine();
                if (line == null) {
                    line = "";
                }
            } catch (IOException e) {
                System.err.println("IO Exception");
                pressEnter();
                line = "";
            }
            int choice = parseCommand(line.trim());
            if (choice < items.size()) {
                MenuItem item = items.get(choice);
                item.run();
            } else {
                System.err.println("Bad command");
                pressEnter();
            }
        }
    }
}
