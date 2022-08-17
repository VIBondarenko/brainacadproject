# BrainAcadProject
## Leaning Project for Brain Academy ([mainacademy.ua](https://mainacademy.ua/))

## Leaning Management System (LMS)

### STUDENT
Student studying in a course and is part of a specific group. 
Student Responsibility - it is the fulfillment of tasks received within the course. 
A student can take several courses at the same time.
У каждого студента должны быть: 
уникальный идентификатор, 
имя, 
фамилия, 
список курсов, на которых он сейчас учится, 
список задач, полученных в рамках каждого курса.

Тренер
Тренер читает один и более курсов. Каждый тренер должен иметь: 
уникальный идентификатор, 
имя, 
фамилию, 
список курсов, которые он читает.

COURSE
Каждый курс должен содержать: 
уникальный идентификатор, 
название, 
краткое описание, 
список студентов, записанных на данный курс, 
тренера, который читает курс, 
дату начала, 
дату окончания, 
список дней недели, в которые проходят занятия, 
журнал успеваемости. 
Курс имеет ограничение, на один курс не может быть записано более чем 12 студентов. Студент должен иметь возможность перейти с одного курса на другой.

Журнал успеваемости
Журнал успеваемости хранит количество полученных баллов каждым студентом по каждой задаче в рамках курса. 

LMS
Система должна позволять при помощи интерфейса командой строки (command line interface, CLI) выполнять управление курсами, а именно:
Создание курса
Вывод подробной информации о курсе по его идентификатору
Вывод списка названий всех курсов
Создание студента в рамках определенного курса(ов)
Перевод студента из одного курса на другой
Вывод информации о студенте по его идентификатору
Создание тренера в рамках определенного курса(ов)
Вывод информации о тренере по его идентификатору
Создание задач в рамках определенного курса
Вывод имен и фамилий всех студентов по идентификатору курса
Вывод журнала успеваемости определенного курса
Сохранение журнала успеваемости в файл
Выход из программы


ЗАДАНИЕ
Реализовать проект "Cистема управления обучением" согласно описанным выше требованиям. При реализации выбирать и использовать наиболее подходящие коллекции и структуры данных. Предусмотреть возможность сборки проекта при помощи сборщика Maven и последующего запуска приложения из полученного jar-артефакта. Где есть необходимость применить изученные паттерны проектирования. На произвольную часть функционала написать 7 unit-тестов.

СОЗДАНИЕ КУРСА
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
