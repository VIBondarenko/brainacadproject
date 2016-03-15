package com.brainacad.ecs.enums;

public enum DayOfWeek {
    MON(1) {
        @Override
        public String toString() {
            return "Monday";
        }
    },
    TUE(2) {
        @Override
        public String toString() {
            return "Tuesday";
        }
    },
    WED(3) {
        @Override
        public String toString() {
            return "wednesday";
        }
    },
    THU(4) {
        @Override
        public String toString() {
            return "Thursday";
        }
    },
    FRI(5) {
        @Override
        public String toString() {
            return "Friday";
        }
    },
    SAT(6) {
        @Override
        public String toString() {
            return "Saturday";
        }
    },
    SUN(7) {
        @Override
        public String toString() {
            return "Sunday";
        }
    };
    private int dayCode;

    private DayOfWeek(int dayCode) {
        this.dayCode = dayCode;
    }

    public int getDayCode() {
        return dayCode;
    }
}