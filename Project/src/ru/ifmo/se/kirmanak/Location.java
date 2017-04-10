package ru.ifmo.se.kirmanak;

/**
 * Местонахождение человека
 */
class Location {
    /** Имя локации */
    private final String name;
    private int hash = 0;

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
        if (this.hash == 0) {
            this.hash = this.getName().length();
        }
        return this.hash;
    }
}
