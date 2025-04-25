package com.brainacad.ecs;
/* Vitaliy Bondarenko
* https://vibondarenko.github.io/
**/
import com.brainacad.ecs.enums.ItemOfMenu;
import java.io.IOException;

import static com.brainacad.ecs.Utilities.pressEnter;

public class EducationControlSystem {
    private static EducationControlSystem instance = new EducationControlSystem();
    private Storage storage;
    private Menu menu = new Menu("\nEducation Control System Menu:\n");

    public static EducationControlSystem getInstance() {
        return instance;
    }
    private EducationControlSystem() {
    }
    private void show() {
        menu.addItem(new MenuItem(ItemOfMenu.CREATE_COURSE.toString()) {
            @Override
            public void run() {
                storage.createCourse();
                pressEnter();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.SHOW_COURSE.toString()) {
            @Override
            public void run() {
                storage.showCourse();
                pressEnter();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.SHOW_COURSES.toString()) {
            @Override
            public void run() {
                storage.showCourses();
                pressEnter();
            }
        });

        menu.addItem(new MenuItem(ItemOfMenu.CREATE_STUDENT.toString()) {
            @Override
            public void run() {
                storage.createStudent();
                pressEnter();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.REPLACE_STUDENT.toString()) {
            @Override
            public void run() {
                storage.replaceStudent();
                pressEnter();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.SHOW_STUDENT.toString()) {
            @Override
            public void run() {
                storage.showStudent();
                pressEnter();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.SHOW_STUDENTS.toString()) {
            @Override
            public void run() {
                storage.showStudents();
                pressEnter();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.DELETE_STUDENT.toString()) {
            @Override
            public void run() {
                storage.deleteStudent();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.CREATE_TRAINER.toString()) {
            @Override
            public void run() {
                storage.createTrainer();
                pressEnter();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.SHOW_TRAINER.toString()) {
            @Override
            public void run() {
                storage.showTrainer();
                pressEnter();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.SHOW_TRAINERS.toString()) {
            @Override
            public void run() {
                storage.showTrainers();
                pressEnter();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.DELETE_TRAINER.toString()) {
            @Override
            public void run() {
                storage.deleteTrainer();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.CREATE_TASK.toString()) {
            @Override
            public void run() {
                storage.createTasks();
                pressEnter();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.SHOW_JOURNAL.toString()) {
            @Override
            public void run() {
                storage.showJournal();
                pressEnter();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.SAVE_JOURNAL.toString()) {
            @Override
            public void run() {
                storage.saveJournal();
                pressEnter();
            }
        });
        menu.addItem(new MenuItem(ItemOfMenu.EXIT.toString()) {
            @Override
            public void run() {
                menu.setExit(true);
            }
        });
        menu.run();
    }
    private void run() {
        storage = Storage.getInstance();
        show();
        try {
            storage.write(storage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        //Point
        EducationControlSystem ecs = EducationControlSystem.getInstance();
        ecs.run();
    }
}