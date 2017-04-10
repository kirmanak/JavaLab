package ru.ifmo.se.kirmanak;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * EntryPoint, создающий графический интерфейс и обрабатывающий действия пользователя.
 * Работает с файлом, записанным в переменную окружения под названием jsonFile.
 */

public class Main extends Application {
    /** Сама коллекция  */
    static volatile Vector<Humans> collection = new Vector<>();
    private static final Runnable load = () -> System.err.println(Commands.load.doIt());
    private static final Runnable save = () -> System.err.println(Commands.save.doIt());
    static TextField name;
    static TextField character;
    static TextField location;
    static ChoiceBox<Relative> relations;
    static DatePicker picker;
    private static GridPane pane;
    private static Slider slider;
    private static VBox addVBox;
    private static Button removeButton;
    private static Button generateButton;

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(save));
        IO(load);
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("Я сломался");
            e.printStackTrace(System.err);
        }
    }

    /** Метод, осуществляющий ввод/вывод данных из/в файл(а) в отдельном потоке */
    private static void IO (Runnable runnable) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(runnable);
        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (!executor.isTerminated()) {
                System.err.println("Слишком долго обрабатываются данные.");
                executor.shutdownNow();
            }
        }
    }

    /** Метод для обновления отображаемой коллекции в случае её изменения*/
    private static void updateList () {
        TreeItem<String> tree = new TreeItem<>("Коллекция: ");
        for (Humans humans : Main.collection) {
            TreeItem<String> item =
                    new TreeItem<>(humans.getName());
            item.getChildren().add(new TreeItem<>(humans.toString()));
            tree.getChildren().add(item);
        }
        tree.setExpanded(true);
        TreeView<String> view = new TreeView<>(tree);
        view.setMinWidth(700);
        view.setMaxHeight(addVBox.getHeight());
        view.setMinHeight(addVBox.getHeight());
        pane.add(view, 0, 0);
        slider.setMinWidth(pane.getWidth()
                - removeButton.getWidth()
                - generateButton.getWidth());
        slider.setMax(collection.size());
        if (slider.getMax() == 0) {
            slider.setMin(1);
            slider.setMax(5);
        }
    }

    private static Dialog dialogWindow(String header, String content) {
        Dialog dialog = new Dialog();
        dialog.setTitle(header);
        dialog.setContentText(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.FINISH);
        //следующая строка честно похищена со StackOverflow для разбиения справки на строки
        dialog.getDialogPane().getChildren().stream()
                .filter(node -> node instanceof Label)
                .forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
        return dialog;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox rootNode = new VBox();
        HBox sliderHBox = new HBox();
        pane = new GridPane();
        slider = new Slider(1, collection.size(), 5);
        addVBox = new VBox();
        name = new TextField();
        character = new TextField();
        location = new TextField();
        relations = new ChoiceBox<>();
        picker = new DatePicker(LocalDate.now());

        //для remover'а и генератора
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        removeButton = new Button("Удалить");
        generateButton = new Button("Сгенерировать");
        sliderHBox.getChildren().addAll(slider, removeButton, generateButton);

        Menu fileMenu = new Menu("Файл");
        MenuItem saveOption = new MenuItem("Сохранить");
        MenuItem loadOption = new MenuItem("Загрузить");
        fileMenu.getItems().addAll(loadOption, saveOption);
        Menu optionsMenu = new Menu("Операции");
        MenuItem removeLastOption = new MenuItem("Удалить последнего");
        MenuItem addOption = new MenuItem("Добавить нового");
        optionsMenu.getItems().addAll(addOption, removeLastOption);
        MenuBar menuBar = new MenuBar();
        Menu helpMenu = new Menu("Справка");
        MenuItem helpOption = new MenuItem("Получить справку");
        helpMenu.getItems().add(helpOption);
        String help = "В левой части программы Вы можете видеть Вашу коллекцию" +
                " элементов класса ru.ifmo.se.kirmanak.Humans. В правой части вы можете описать новый" +
                " элемент и затем добавить его при помощи подменю \"" + addOption.getText()
                + "\" в меню \"" + optionsMenu.getText() + "\". \n" +
                " Кроме того, новый элемент вы можете добавить при помощи слайдера. Слайдером" +
                " указываете сколько новых элементов необходимо сгенерировать, затем жмёте \""
                + generateButton.getText() + "\". Наверняка, вас повеселит результат.\n" +
                " С помощью слайдера можно удалить какой-то конкретный элемент в коллекции," +
                " указав слайдером его номер и нажав \"" + removeButton.getText() + "\".\n" +
                "\n" +
                "Автор - Камакин Кирилл, P3102." +
                "\n СПб, 2к17.";
        menuBar.getMenus().addAll(fileMenu, optionsMenu, helpMenu);

        //для добавления нового человека
        name.setPromptText("Имя");
        character.setPromptText("Характер");
        location.setPromptText("Местонахождение");
        relations.getItems().addAll(Relative.values());
        relations.setValue(Relative.sibling);
        HBox box = new HBox(relations, picker);

        addVBox.getChildren().addAll(new Label("Информация о новом элементе:"),
                name, character, location, box);
        pane.add(addVBox, 1, 0);
        rootNode.getChildren().addAll(menuBar, pane, sliderHBox);

        picker.setOnAction((event) -> {
            if (picker.getValue().toEpochDay()<LocalDate.now().toEpochDay())
                picker.setValue(LocalDate.now());
        });
        saveOption.setOnAction((event) -> IO(save));
        loadOption.setOnAction((event) -> {
            IO(load);
            updateList();
        });
        removeButton.setOnAction((event) -> {
            System.err.println(Commands.remove.doIt((int) slider.getValue()));
            updateList();
        });
        generateButton.setOnAction((event) -> {
            System.err.println(Commands.generate.doIt((int) slider.getValue()));
            updateList();
        });
        removeLastOption.setOnAction((event) -> {
            System.err.println(Commands.remove.doIt(Main.collection.size()));
            updateList();
        });
        addOption.setOnAction((event) -> {
            if (name.getText().isEmpty() ||
                    character.getText().isEmpty() || location.getText().isEmpty()) {
                System.err.println("Пустые поля для ввода");
                String errText = "Вы должны заполнить поля " + name.getPromptText() +
                        ", " + character.getPromptText() + ", " + location.getPromptText();
                dialogWindow("Ошибка", errText).showAndWait();
            } else {
                System.err.println(Commands.add.doIt());
                updateList();
            }
        });
        helpOption.setOnAction((event) -> dialogWindow("Справка", help).showAndWait());

        primaryStage.setScene(new Scene(rootNode));
        primaryStage.setTitle("Лабораторная №6");
        primaryStage.show();
        updateList();
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        updateList();
        slider.requestFocus();
    }
}