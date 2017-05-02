package ru.ifmo.se.kirmanak;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

class EntryPoint {
    /**
     * Сама коллекция
     */
    private static final ObservableList<Humans> collection
            = FXCollections.synchronizedObservableList(new Collection());

    static ObservableList<Humans> getCollection() {
        return collection;
    }

    public static void main(String[] args) {
        collection.addListener((ListChangeListener<Humans>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    List<? extends Humans> addedSubList = c.getAddedSubList();
                    int previousSize = EntryPoint.getCollection().size() - addedSubList.size();
                    for (int i = 1, addedSubListSize = addedSubList.size(); i <= addedSubListSize; i++) {
                        Humans humans = addedSubList.get(i - 1);
                        int j = i + previousSize;
                        TreeItem<String> item = humans.toTreeItem(j);
                        Interface.getView().getRoot().getChildren().add(item);
                    }
                }
                if (c.wasRemoved()) {
                    ArrayList<TreeItem<String>> set = new ArrayList<>();
                    for (int i = 1, collectionSize = collection.size(); i <= collectionSize; i++) {
                        Humans humans = collection.get(i - 1);
                        TreeItem<String> item = humans.toTreeItem(i);
                        set.add(item);
                    }
                    Interface.getView().getRoot().getChildren().setAll(set);
                }
            }
            Platform.runLater(Interface::updateList);
        });
        Interface.draw(args);
    }
}
