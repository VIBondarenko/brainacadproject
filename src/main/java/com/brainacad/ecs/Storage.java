package com.brainacad.ecs;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.brainacad.ecs.Utilities.*;

public class Storage implements Serializable {
    private static final long serialVersionUID = 100L;
    private static Storage instance;
    private List<Course> courses = new ArrayList<Course>();
    private List<Student> students = new ArrayList<Student>();
    private List<Trainer> trainers = new ArrayList<Trainer>();
    private List<Task> tasks = new ArrayList<Task>();

    public static Storage getInstance() {
        if (instance == null) {
            String fileName = Storage.class.getSimpleName();
            try {
                instance = (Storage) read(fileName);
            } catch (IOException e) {
                System.err.println("File '" + fileName + ".ser" + "' not found.");
                instance = new Storage();
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found in file '" + fileName + ".ser'");
                instance = new Storage();
            }
        }
        return instance;
    }
    private Storage() {

    }
    private int getCountFreeCourses() {
        Iterator itr = courses.iterator();
        int count = 0;
        while (itr.hasNext()) {
            if (((Course) itr.next()).getCountPlaces() != 0) {
                count++;
            }
        }
        return count;
    }
    private static Object read(String string) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = null;
        ois = new ObjectInputStream(new FileInputStream(string + ".ser"));
        Course.deserializeStatic(ois);
        Student.deserializeStatic(ois);
        Trainer.deserializeStatic(ois);
        Task.deserializeStatic(ois);
        return ois.readObject();
    }
    public void write(Object object) throws IOException {
        String fileName = object.getClass().getSimpleName();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName + ".ser"))) {
            Course.serializeStatic(oos);
            Student.serializeStatic(oos);
            Trainer.serializeStatic(oos);
            Task.serializeStatic(oos);
            oos.writeObject(object);
        }
    }
    public void createCourse() {
        String strName = null;
        Date date1 = null;
        Date date2 = null;
        Course course;

        while (strName == null) {
            System.out.print("Name: ");
            course = searchByName(courses, strName = readStringValue());
            if (course != null) {
                System.out.println("Sorry, same name");
                strName = null;
            }
        }

        System.out.print("Description: ");
        String strDesc = readStringValue();

        DateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        while (date1 == null) {
            System.out.print("Begin Date (dd.mm.yyyy): ");
            try {
                date1 = formatDate.parse(readStringValue());
            } catch (ParseException e) {
                System.out.println("Sorry, wrong value. Please try again");
            }
        }

        while (date2 == null) {
            System.out.print("End Date (dd.mm.yyyy): ");
            try {
                date2 = formatDate.parse(readStringValue());
                if (date2.compareTo(date1) <= 0) {
                    date2 = null;
                    System.out.println("Sorry, date2 < date1. Please try again");
                }
            } catch (ParseException e) {
                System.out.println("Sorry, wrong value. Please try again");
            }
        }

        System.out.print("Days: ");
        String days = readStringValue();

        course = new Course(strName, strDesc, date1, date2, days);
        courses.add(courses.size(), course);

        System.out.println("\nNew course has been successfully created:");
        System.out.print(course);
        try {
            write(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showCourse() {
        if (courses.size() == 0) {
            System.out.println("There aren't any courses. Please create course.");
            return;
        }
        int id = Integer.MIN_VALUE;
        showCourses();
        while(id == Integer.MIN_VALUE) {
            System.out.print("Enter Course ID: ");
            id = readIntValue();
        }
        Course course = searchById(courses, id);
        if (course != null) {
            System.out.print(course.toString());
        } else {
            System.out.println("Course with ID '" + id + "' not found");
        }
    }
    public void showCourses() {
        if (courses.size() == 0) {
            System.out.println("There aren't any courses. Please create course.");
            return;
        }
        System.out.println("There are next courses:");
        Iterator itr = courses.iterator();
        while (itr.hasNext()) {
            Course item = (Course) itr.next();
            System.out.println("\tID: " + item.getId() + "  Name: " + item.getName());
        }
    }
/*    public void deleteCourse() {

    }*/
    public void createStudent() {
        if (courses.size() == 0) {
            System.out.println("There aren't any courses. Please create course.");
            return;
        }
        int countFreeCourses;
        if ((countFreeCourses = getCountFreeCourses()) == 0) {
            System.out.println("There aren't places on courses");
            return;
        }

        String firstName = null;
        String lastName = null;
        int countCourses = Integer.MIN_VALUE;
        int idCourse = Integer.MIN_VALUE;

        while (firstName == null) {
            System.out.print("Name: ");
            firstName = checkName(readStringValue());
            if (firstName == null) {
                System.err.println("Not valid value");
            }
        }

        while (lastName == null) {
            System.out.print("Last Name: ");
            lastName = checkName(readStringValue());
            if (lastName == null) {
                System.err.println("Not valid value");
            }
        }

        Student student = new Student(firstName, lastName);
        students.add(students.size(), student);

        while (countCourses == Integer.MIN_VALUE) {
            showCourses();
            System.out.print("Enter Courses count for adding: ");
            countCourses = readIntValue();
            if (countCourses == 0) {
                System.out.println("You entered '" + countCourses + "'");
                countCourses = Integer.MIN_VALUE;
            }
            if (idCourse != Integer.MIN_VALUE) {
                if ((countCourses > courses.size()) || (countCourses == 0)) {
                    countCourses = Integer.MIN_VALUE;
                    System.err.println("There are " + courses.size() + " courses in list, but you entered: " + countCourses);
                }
                if (countFreeCourses < countCourses) {
                    countCourses = Integer.MIN_VALUE;
                    System.err.println("Count free courses is " + countFreeCourses + ", but you entered: " + countCourses);
                }
            }
        }

        while (idCourse == Integer.MIN_VALUE) {
            for (int i = 0; i < countCourses; i++) {
                System.out.print("Enter Course ID: ");
                idCourse = readIntValue();
                if (idCourse != Integer.MIN_VALUE) {
                    Course course = searchById(courses, idCourse);
                    if (course != null) {
                        if (course.addStudent(student)) {
                            student.addCourse(course);
                            student.addTasks(tasks, idCourse);
                        } else {
                            System.out.println("There aren't place on course with ID: " + idCourse);
                            idCourse =Integer.MIN_VALUE;
                        }
                    } else {
                        System.err.println("Sorry, Course with ID = " + idCourse + "not found");
                        idCourse =Integer.MIN_VALUE;
                    }
                }
            }
        }

        System.out.println("\nNew student has been successfully created:");
        System.out.print(student);

        try {
            write(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void replaceStudent() {
        if (courses.size() == 0) {
            System.out.println("There aren't any courses. Please create course.");
            return;
        }
        if (students.size() == 0) {
            System.out.println("There aren't any students. Please create course.");
            return;
        }
        if (getCountFreeCourses() == 0) {
            System.out.println("There aren't any free places on courses.");
            return;
        }

        int studentId, courseFromId, courseToId;
        studentId = courseFromId = courseToId = Integer.MIN_VALUE;
        Course courseFrom = null;
        Course courseTo = null;
        Student student = null;

        while (studentId == Integer.MIN_VALUE) {
            System.out.print("Enter Student ID: ");
            student = searchById(students, studentId = readIntValue());
            if (student == null) {
                System.out.println("Student with ID '" + studentId + "' not found");
                studentId = Integer.MIN_VALUE;
            }
        }

        while (courseFromId == Integer.MIN_VALUE) {
            System.out.print("Enter Course ID (From): ");
            courseFrom = searchById(student.getCourses(), courseFromId = readIntValue());
            if (courseFrom == null) {
                System.out.println("Student with ID '" + studentId + "' not leaning on Course with ID: " + courseFromId);
                courseFromId = Integer.MIN_VALUE;
            }
        }

        while (courseToId == Integer.MIN_VALUE) {
            System.out.print("Enter Course ID (To): ");
            courseTo = searchById(courses, courseToId = readIntValue());
            if (courseTo == null) {
                System.out.println("Course with ID '" + courseToId + "' not found");
                courseToId = Integer.MIN_VALUE;
            } else {
                if (courseFromId == courseToId) {
                    System.out.println("Not valid value. Course ID From = Course ID To");
                    courseToId = Integer.MIN_VALUE;
                }
                if (courseTo.getCountPlaces() == 0) {
                    System.out.println("Course haven't free places");
                    courseToId = Integer.MIN_VALUE;
                }
            }
        }

        courseFrom.deleteStudent(student);
        student.deleteTasks(tasks, courseFrom.getId());
        student.deleteCourse(courseFrom);

        courseTo.addStudent(student);
        student.addCourse(courseTo);
        student.addTasks(tasks, courseTo.getId());
        courseTo.addStudentToJournal(student, student.getTasks(courseTo.getId()));

        System.out.println("Student '(" + student.getId() + ") "
                + student.getName() + " " + student.getLastName()
                + "' replaced from course '(" + courseFrom.getId() + ") " + courseFrom.getName()
                + "' to course '" + courseTo.getId() + " " + courseTo.getName() + "'");
        try {
            write(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showStudent() {
        if (students.size() == 0) {
            System.out.println("There aren't any students. Please create student.");
            return;
        }
        int id = Integer.MIN_VALUE;
        while(id == Integer.MIN_VALUE) {
            System.out.print("Enter Student ID: ");
            id = readIntValue();
        }
        Student student = searchById(students, id);
        if (student != null) {
            System.out.println(student.toString());
        } else {
            System.out.println("Student with ID '" + id + "' not found");
        }
    }
    public void showStudents() {
        if (courses.size() == 0) {
            System.out.println("There aren't any courses. Please create course.");
            return;
        }
        int id = Integer.MIN_VALUE;
        showCourses();
        while(id == Integer.MIN_VALUE) {
            System.out.print("Enter Course ID: ");
            id = readIntValue();
        }

        Course course = searchById(courses, id);
        if (course != null) {
            System.out.print("Students of course '" + course.getName() + "':\n" + course.getStudentsList());
        } else {
            System.out.println("Course with ID '" + id + "' not found");
        }
    }
    public void deleteStudent() {
        int id = Integer.MIN_VALUE;
        while(id == Integer.MIN_VALUE) {
            System.out.print("Enter Student ID: ");
            id = readIntValue();
        }
        Student student = searchById(students, id);
        if (student != null) {
            student.deleteStudentFromCourses();
            students.remove(student);
            System.out.println("Student with ID '" + id + "' deleted");
        } else {
            System.out.println("Student with ID '" + id + "' not found");
        }
    }
    public void createTrainer() {
        if (courses.size() == 0) {
            System.out.println("There aren't any courses. Please create course.");
            return;
        }

        int count = 0;
        Iterator itr = courses.iterator();

        while (itr.hasNext()) {
            Course item = (Course) itr.next();
            if (item.getTrainer() == null) {
                count ++;
            }
        }

        if (count == 0) {
            System.out.println("There aren't any courses free for trainer");
            return;
        }

        String firstName = null;
        String lastName = null;
        int countCourses = Integer.MIN_VALUE;
        int idCourse = Integer.MIN_VALUE;

        while (firstName == null) {
            System.out.print("Name: ");
            firstName = checkName(readStringValue());
            if (firstName == null) {
                System.err.println("Not valid value");
            }
        }

        while (lastName == null) {
            System.out.print("Last Name: ");
            lastName = checkName(readStringValue());
            if (lastName == null) {
                System.err.println("Not valid value");
            }
        }

        Trainer trainer = new Trainer(firstName, lastName);
        trainers.add(trainers.size(), trainer);

        while (countCourses == Integer.MIN_VALUE) {
            showCourses();
            System.out.print("Enter Courses count for adding: ");
            countCourses = readIntValue();
            if (countCourses == 0) {
                countCourses = Integer.MIN_VALUE;
            }
            if (countCourses != Integer.MIN_VALUE) {
                if (countCourses > courses.size()) {
                    countCourses = Integer.MIN_VALUE;
                    System.err.println("Sorry, entered count > than courses count!");
                }
                if (countCourses > count) {
                    countCourses = Integer.MIN_VALUE;
                    System.err.println("Sorry, entered count > than courses free for trainer!");
                }
            }
        }

        while (idCourse == Integer.MIN_VALUE) {
            for (int i = 0; i < countCourses; i++) {
                System.out.print("Enter Course ID: ");
                idCourse = readIntValue();
                if (idCourse != Integer.MIN_VALUE) {
                    Course course = searchById(courses, idCourse);
                    if (course != null) {
                        if (course.getTrainer() == null) {
                            course.setTrainer(trainer);
                            trainer.addCourse(course);
                        } else {
                            System.out.println("Sorry, Course have trainer");
                            idCourse = Integer.MIN_VALUE;
                        }
                    } else {
                        System.err.println("Sorry, Course with ID = " + idCourse + " not found");
                        idCourse = Integer.MIN_VALUE;
                    }
                }
            }
        }

        System.out.println("\nNew trainer has been successfully created:");
        System.out.print(trainer);

        try {
            write(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showTrainer() {
        if (trainers.size() == 0) {
            System.out.println("There aren't any trainers. Please create trainers.");
            return;
        }
        int id = Integer.MIN_VALUE;

        showTrainers();

        while(id == Integer.MIN_VALUE) {
            System.out.print("Enter Trainer ID: ");
            id = readIntValue();
        }

        Trainer trainer = searchById(trainers, id);

        if (trainer != null) {
            System.out.print(trainer.toString());
        } else {
            System.out.println("Trainer with ID '" + id + "' not found");
        }
    }
    public void showTrainers() {
        if (trainers.size() == 0) {
            System.out.println("There aren't any trainers. Please create trainer.");
            return;
        }
        System.out.println("There are next trainers:");
        Iterator itr = trainers.iterator();
        while (itr.hasNext()) {
            Trainer item = (Trainer) itr.next();
            System.out.println("\tID: " + item.getId() + "  Name: " + item.getName() + " " + item.getLastName());
        }
    }
    public void deleteTrainer() {
        showTrainers();
        int id = Integer.MIN_VALUE;
        while(id == Integer.MIN_VALUE) {
            System.out.print("Enter Trainer ID: ");
            id = readIntValue();
        }
        Trainer trainer = searchById(trainers, id);
        if (trainer != null) {
            trainer.deleteTrainerFromCourses();
            trainers.remove(trainer);
            System.out.println("Trainer with ID '" + id + "' deleted");
        } else {
            System.out.println("Trainer with ID '" + id + "' not found");
        }
    }
    public void createTasks() {
        if (courses.size() == 0) {
            System.out.println("There aren't any courses. Please create course.");
            return;
        }

        int courseId = Integer.MIN_VALUE;
        int countTask = Integer.MIN_VALUE;
        String name, description;
        Course course = null;

        showCourses();

        while(courseId == Integer.MIN_VALUE) {
            System.out.print("Enter Course ID: ");
            courseId = readIntValue();
            if (courseId != Integer.MIN_VALUE) {
                course = searchById(courses, courseId);
                if (course == null) {
                    System.out.println("Sorry, Course with ID = " + courseId + " not found");
                    courseId = Integer.MIN_VALUE;
                }
            }
        }

        while (countTask == Integer.MIN_VALUE) {
            System.out.print("Enter count of task: ");
            countTask = readIntValue();
        }

        for (int i = 0; i < countTask; i++) {
            System.out.print("Name: ");
            name = readStringValue();
            System.out.print("Description: ");
            description = readStringValue();
            Task task = new Task(name, description, course);
            tasks.add(tasks.size(), task);
            List<Student> students = course.getStudents();
            if (!students.isEmpty()) {
                Iterator itr = students.iterator();
                while (itr.hasNext()) {
                    Student student = (Student) itr.next();
                    student.addTask(task);
                    course.addTaskToJournal(student, task);
                }
            }
        }


        try {
            write(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
/*
    public void deleteTask() {

    }
*/
    public void showJournal() {
        if (courses.size() == 0) {
            System.out.println("There aren't any courses. Please create course.");
            return;
        }
        showCourses();
        int courseId = Integer.MIN_VALUE;
        while(courseId == Integer.MIN_VALUE) {
            System.out.print("Enter Course ID: ");
            courseId = readIntValue();
            if (courseId != Integer.MIN_VALUE) {
                Course course = searchById(courses, courseId);
                if (course != null) {
                    System.out.println("Course: " + course.getName());
                    course.printJournal();
                }
            }
        }
    }
    public void saveJournal() {
        if (courses.size() == 0) {
            System.out.println("There aren't any courses. Please create course.");
            return;
        }
        showCourses();
        int courseId = Integer.MIN_VALUE;
        Course course = null;

        while(courseId == Integer.MIN_VALUE) {
            System.out.print("Enter Course ID: ");
            courseId = readIntValue();
            if (courseId != Integer.MIN_VALUE) {
                course = searchById(courses, courseId);
                if (course == null) {
                    courseId = Integer.MIN_VALUE;
                }
            }
        }

        System.out.print("Enter File Name (without ext): ");
        course.saveJournal(readStringValue() + "_" + course.getName() + ".txt");
        System.out.println("Saved");

    }
}
