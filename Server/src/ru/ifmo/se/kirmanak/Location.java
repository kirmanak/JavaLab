package ru.ifmo.se.kirmanak;

import java.io.Serializable;

/**
 * Местонахождение человека
 */
class Location implements Serializable {
    /** Имя локации */
    private final String name;

    public Location(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }

    public String toString() {
        return this.getName();
    }

    public int hashCode () {
        return this.getName().length() + this.getName().codePointAt(0);
    }
}
