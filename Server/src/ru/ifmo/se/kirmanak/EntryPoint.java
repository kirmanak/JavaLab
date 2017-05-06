package ru.ifmo.se.kirmanak;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.postgresql.ds.PGConnectionPoolDataSource;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

class EntryPoint {
    private static final String dbUser = "postgres";
    private static final String dbPassword = "8XpGBTfg";
    private static final String dbHost = "localhost";
    private static final String dbName = "humans";
    private static final String dbTableName = "human";
    private static final ForkJoinPool pool = new ForkJoinPool();
    private static final int port = 6666;
    /**
     * Сама коллекция
     */
    private static final ObservableList<Humans> collection
            = FXCollections.synchronizedObservableList(new Collection());
    private static DatagramChannel serverChannel;
    private static ServerThread serverThread;

    public static ServerThread getServerThread() {
        return serverThread;
    }

    public static DatagramChannel getServerChannel() {
        return serverChannel;
    }

    public static ForkJoinPool getPool() {
        return pool;
    }

    public static String getDbTableName() {
        return dbTableName;
    }

    static ObservableList<Humans> getCollection() {
        return collection;
    }

    public static Connection getConnection () throws SQLException {
        PGConnectionPoolDataSource dataSource = new PGConnectionPoolDataSource();
        dataSource.setUser(dbUser);
        dataSource.setServerName(dbHost);
        dataSource.setDatabaseName(dbName);
        dataSource.setPassword(dbPassword);
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }

    public static void main(String[] args) throws IOException {
        serverChannel = DatagramChannel.open();
        serverThread = new ServerThread(port);
        Thread serverDaemon = new Thread(serverThread);
        serverDaemon.setDaemon(true);
        serverDaemon.start();
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
                    Message message = new Message(true, new ArrayList<>(EntryPoint.getCollection()));
                    System.out.println("Сформировал новое сообщение после добавления...");
                    getServerThread().sendMessageToAll(message);
                }
                if (c.wasRemoved()) {
                    List<TreeItem<String>> list = new ArrayList<>();
                    for (int i = 1, collectionSize = collection.size(); i <= collectionSize; i++) {
                        Humans humans = collection.get(i - 1);
                        TreeItem<String> item = humans.toTreeItem(i);
                        list.add(item);
                    }
                    Interface.getView().getRoot().getChildren().setAll(list);
                    Message message = new Message(true, new ArrayList<>(EntryPoint.getCollection()));
                    System.out.println("Сформировал новое сообщение после удаления...");
                    getServerThread().sendMessageToAll(message);
                }
            }
            Platform.runLater(Interface::updateSlider);
        });
        Interface.draw(args);
    }
}
