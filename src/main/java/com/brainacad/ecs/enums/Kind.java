package com.brainacad.ecs.enums;

public enum Kind {
    STUDENT{
        @Override
        public String toString() {
            return "Student";
        }
    },
    TRAINER {
        @Override
        public String toString() {
            return "Trainer";
        }
    }
}
