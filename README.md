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
1. Создание курса
2. Вывод подробной информации о курсе по его идентификатору
3. Вывод списка названий всех курсов
4. Создание студента в рамках определенного курса(ов)
5. Перевод студента из одного курса на другой
6. Вывод информации о студенте по его идентификатору
7. Создание тренера в рамках определенного курса(ов)
8. Вывод информации о тренере по его идентификатору
9. Создание задач в рамках определенного курса
10. Вывод имен и фамилий всех студентов по идентификатору курса
11. Вывод журнала успеваемости определенного курса
12. Сохранение журнала успеваемости в файл
13. Выход из программы

### СОЗДАНИЕ КУРСА
Создает новый курс. Идентификатор курса должен формироваться автоматически. При попытке создать курс с названием, которое уже есть, выводить сообщение: "Course name should be unique. Please, enter another name".
Пример команды:
Please, enter the command:
create course

Course name:		Java for Beginners
Course description:	Course for people that want learn Java programming language
Start date:		01.01.2015
End date: 		01.04.2015
Days:			Tue, Wed, Sat

New course has been successfully created:
Course ID: 1
Course name: Java for Beginners
Course description: Course for people that want learn Java programming language
Start date: 01.01.2015
End date: 01.04.2015
Days: Tue, Wed, Sat

ВЫВОД ПОДРОБНОЙ ИНФОРМАЦИИ О КУРСЕ ПО ЕГО ИДЕНТИФИКАТОРУ
Выводит описание курса по его идентификатору. Если курса с указанным идентификатором нет, то необходимо вывести сообщение: "Course with id xx doesn’t exist".
Пример команды:
Please, enter the command:
show course 1

Course ID: 1
Course name: Java for Beginners
Course description: Course for people that want learn Java programming language
Start date: 01.01.2015
End date: 01.04.2015
Days: Tue, Wed, Sat

ВЫВОД СПИСКА НАЗВАНИЙ ВСЕХ КУРСОВ
Выводит список всех существующих курсов.
Пример команды:
Please, enter the command:
show list courses

1. Programming in Java
2. Programming FrontEnd
3. Software Testing

CREATE STUDENT IN COURSE(-S)

ПЕРЕВОД СТУДЕНТА ИЗ ОДНОГО КУРСА НА ДРУГОЙ

ВЫВОД ИНФОРМАЦИИ О СТУДЕНТЕ ПО ЕГО ИДЕНТИФИКАТОРУ

СОЗДАНИЕ ТРЕНЕРА В РАМКАХ ОПРЕДЕЛЕННОГО КУРСА(ОВ)

ВЫВОД ИНФОРМАЦИИ О ТРЕНЕРЕ ПО ЕГО ИДЕНТИФИКАТОРУ

СОЗДАНИЕ ЗАДАЧ В РАМКАХ ОПРЕДЕЛЕННОГО КУРСА

ВЫВОД ИМЕН И ФАМИЛИЙ ВСЕХ СТУДЕНТОВ ПО ИДЕНТИФИКАТОРУ КУРСА

ВЫВОД ЖУРНАЛА УСПЕВАЕМОСТИ ОПРЕДЕЛЕННОГО КУРСА

СОХРАНЕНИЕ ЖУРНАЛА УСПЕВАЕМОСТИ В ФАЙЛ

CLOSE APPLICATION
