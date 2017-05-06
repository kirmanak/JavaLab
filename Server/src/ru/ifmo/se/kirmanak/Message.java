package ru.ifmo.se.kirmanak;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    //Статусы:
    //false - запрос коллекции
    //true - приложена коллекция
    private final boolean STATUS;
    private final ArrayList<Humans> list;

    public Message(boolean STATUS, ArrayList<Humans> list) {
        this.STATUS = STATUS;
        this.list = list;
    }

    public boolean getSTATUS() {
        return STATUS;
    }

    public ArrayList<Humans> getList() {
        return list;
    }
}
