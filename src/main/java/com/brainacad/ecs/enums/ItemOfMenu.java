package com.brainacad.ecs.enums;

import java.io.Serializable;

public enum ItemOfMenu implements Serializable {
    CREATE_COURSE(1) {
        @Override
        public String toString() {
            return "create course";
        }
    },
    SHOW_COURSE(2) {
        @Override
        public String toString() {
            return "show course";
        }
    },
    SHOW_COURSES(3) {
        @Override
        public String toString() {
            return "show courses";
        }
    },

    DELETE_COURSE(4) {
        @Override
        public String toString() {
            return "delete course";
        }
    },
    CREATE_STUDENT(5){
        @Override
        public String toString() {
            return "create student";
        }
    },
    REPLACE_STUDENT(6) {
        @Override
        public String toString() {
            return "replace student";
        }
    },
    SHOW_STUDENT(7) {
        @Override
        public String toString() {
            return "show student";
        }
    },
    SHOW_STUDENTS(8) {
        @Override
        public String toString() {
            return "show students";
        }
    },

    DELETE_STUDENT(9) {
        @Override
        public String toString() {
            return "delete student";
        }
    },

    CREATE_TRAINER(10) {
        @Override
        public String toString() {
            return "create trainer";
        }
    },
    SHOW_TRAINER(11) {
        @Override
        public String toString() {
            return "show trainer";
        }
    },
    SHOW_TRAINERS(12) {
        @Override
        public String toString() {
            return "show trainers";
        }
    },
    DELETE_TRAINER(13) {
        @Override
        public String toString() {
            return "delete trainer";
        }
    },
    CREATE_TASK(14) {
        @Override
        public String toString() {
            return "create task";
        }
    },
    DELETE_TASK(15) {
        @Override
        public String toString() {
            return "delete task";
        }
    },
    SHOW_JOURNAL(16) {
        @Override
        public String toString() {
            return "show journal";
        }
    },
    SAVE_JOURNAL(17) {
        @Override
        public String toString() {
            return "save journal";
        }
    },
    EXIT(18) {
        @Override
        public String toString() {
            return "exit";
        }
    };
    private int menuCode;

    private ItemOfMenu(int menuCode) {
        this.menuCode = menuCode;
    }

    public int getMenuCode() {
        return menuCode;
    }
}
