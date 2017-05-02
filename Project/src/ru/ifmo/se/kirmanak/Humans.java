package ru.ifmo.se.kirmanak;

import javafx.scene.control.TreeItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/** Отношения людей с Малышом */
class Humans implements Comparable<Humans> {
    /** Имя */
    private final String name;
    /** Характер */
    private final String character;
    /** Его отношения с малышом */
    private final Relative relative;
    /** До какой даты человек будет находится в этом месте */
    private final LocalDate time;
    /** Место, в котором находится человек */
    private final Location location;

    /**
     * @param name      Имя человека
     * @param character Характер человека
     * @param location  Местонахождение человека
     * @param time      Время, до которого он будет там находиться
     * @param relative  Отношения с Малышом
     */

    public Humans(String name, String character, Location location, LocalDate time, Relative relative) {
        this.name = name;
        this.location = location;
        this.relative = relative;
        this.character = character;
        this.time = time;
    }

    String getName() {
        return this.name;
    }

    Relative getRelative() {
        return this.relative;
    }

    LocalDate getTime() {
        return this.time;
    }

    String getCharacter() {
        return this.character;
    }

    Location getLocation() {
        return this.location;
    }

    public String toString() {
        return this.getRelative().toString() + " " + this.getName() + " с " + this.getCharacter()
                + " характером, который находится " + this.getLocation().toString() + " до "
                + this.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    TreeItem<String> toTreeItem(int j) {
        TreeItem<String> item =
                new TreeItem<>(j + ". " + this.getName());
        item.getChildren().add(new TreeItem<>(this.toString()));
        return item;
    }

    public int hashCode () {
        return this.getName().length() + this.getRelative().toString().length();
    }

    @Override
    public int compareTo(Humans adult) {
        return this.toString().compareTo(adult.toString());
    }
}
