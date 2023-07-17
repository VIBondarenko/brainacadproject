# BrainAcadProject
## Learning Project for Brain Academy ([mainacademy.ua](https://mainacademy.ua/))

## Learning Management System (LMS)

### EXERCISE
Implement the "Learning Management System" project according to the requirements described above. When implementing, choose and use the most appropriate collections and data structures. Provide for the possibility of building the project using the Maven builder and then launching the application from the resulting jar artefact. Where there is a need to apply the studied design patterns. Write 7 unit tests for an arbitrary part of the functionality.

***Student***

Student studying in a course and is part of a specific group. Student Responsibility - it is the fulfilment of tasks received within the course. A student can take several courses at the same time. Each student must have a unique identifier, first name, last name, list of courses he is currently studying, tasks list received within each course.

***Trainer***

The trainer teaches one or more courses. Every trainer must have:
unique identifier, first name, last name, list of courses he reads.

***Course***

Each course must contain:
unique identificator, Name, short description, list of students enrolled in this course, a trainer who teaches the course,
start date, end date, list of days of the week on which classes are held, Gradebook.
The course has a limitation, more than 12 students cannot be enrolled in one course. The student must be able to move from one course to another.

***Gradebook***

The Gradebook stores the number of points received by each student for each task within the course.

### INTERFACE
LMS The system should allow using the command line interface (CLI) to manage courses, namely:
1. Create course
2. Displaying detailed information about the course by its ID
3. Listing the titles of all courses
4. Create a student within a specific course(s)
5. Transferring a student from one course to another
6. Displaying information about a student by his ID
7. Create a trainer within a specific course(s)
8. Displaying information about a trainer by his ID
9. Create tasks within a specific course
10. Displaying the names and surnames of all students by course ID
11. Outputting a Gradebook for a specific course
12. Save Gradebook to file
13. Exit
