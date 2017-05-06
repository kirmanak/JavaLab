package ru.ifmo.se.kirmanak;

import javafx.scene.control.TreeItem;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/** Отношения людей с Малышом */
class Humans implements Serializable {
    private static int nextId = 1;
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
    private final int id;

    public Humans(String name, String character, Relative relative, LocalDate time, Location location, int id) {
        this.name = name;
        this.character = character;
        this.relative = relative;
        this.time = time;
        this.location = location;
        this.id = id;
        if (nextId <= id) nextId=id+1;
    }

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
        this.id = nextId;
        nextId++;
    }

    public String getName() {
        return this.name;
    }

    public Relative getRelative() {
        return this.relative;
    }

    public LocalDate getTime() {
        return this.time;
    }

    public String getCharacter() {
        return this.character;
    }

    public Location getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return this.getRelative().toString() + " " + this.getName() + " с " + this.getCharacter()
                + "\nхарактером, который находится\n" + this.getLocation().toString() + " до "
                + this.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    TreeItem<String> toTreeItem(int j) {
        TreeItem<String> item =
                new TreeItem<>(j + ". " + this.getName());
        item.getChildren().add(new TreeItem<>(this.toString()));
        return item;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(this.getClass())) {
            Humans humans = (Humans) obj;
            return this.id == humans.id && this.name.equals(humans.name) && this.character.equals(humans.character)
                    && this.relative.equals(humans.relative) && this.location.equals(humans.location) && this.time.equals(humans.time);
        } else return false;
    }

    @Override
    public int hashCode () {
        return this.id;
    }

}
