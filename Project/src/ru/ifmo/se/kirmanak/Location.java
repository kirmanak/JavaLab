package ru.ifmo.se.kirmanak;

/**
 * Местонахождение человека
 */
class Location {
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
