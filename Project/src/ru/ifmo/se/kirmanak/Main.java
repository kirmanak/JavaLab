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
import java.util.concurrent.Future;

/**
 * EntryPoint, создающий графический интерфейс и обрабатывающий действия пользователя.
 * Работает с файлом, записанным в переменную окружения под названием jsonFile.
 */

public class Main extends Application {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    /** Сама коллекция  */
    static Vector<Humans> collection = new Vector<>();
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
        try {
            launch();
        } catch (Exception e) {
            System.err.println("Я сломался");
            e.printStackTrace(System.err);
        }
    }

    /**
     * Метод, осуществляющий чтение данных из файла в отдельном потоке
     */
    private static void load() {
        final Runnable load = () -> System.err.println(Commands.load.doIt());
        Future<?> result = executor.submit(load);
        while (!result.isDone()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        updateList();
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
        final VBox rootNode = new VBox();
        final HBox sliderHBox = new HBox();
        pane = new GridPane();
        slider = new Slider(1, collection.size(), 5);
        addVBox = new VBox();
        name = new TextField();
        character = new TextField();
        location = new TextField();
        relations = new ChoiceBox<>();
        picker = new DatePicker(LocalDate.now());
        final Runnable save = () -> System.err.println(Commands.save.doIt());

        //для remover'а и генератора
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        removeButton = new Button("Удалить");
        generateButton = new Button("Сгенерировать");
        sliderHBox.getChildren().addAll(slider, removeButton, generateButton);

        final Menu fileMenu = new Menu("Файл");
        final MenuItem saveOption = new MenuItem("Сохранить");
        final MenuItem loadOption = new MenuItem("Загрузить");
        fileMenu.getItems().addAll(loadOption, saveOption);
        final Menu optionsMenu = new Menu("Операции");
        final MenuItem removeLastOption = new MenuItem("Удалить последнего");
        final MenuItem addOption = new MenuItem("Добавить нового");
        optionsMenu.getItems().addAll(addOption, removeLastOption);
        final MenuBar menuBar = new MenuBar();
        final Menu helpMenu = new Menu("Справка");
        final MenuItem helpOption = new MenuItem("Получить справку");
        helpMenu.getItems().add(helpOption);
        final String help = "В левой части программы Вы можете видеть Вашу коллекцию" +
                " элементов класса ru.ifmo.se.kirmanak.Humans. В правой части вы можете описать новый" +
                " элемент и затем добавить его при помощи подменю \"" + addOption.getText()
                + "\" в меню \"" + optionsMenu.getText() + "\". \n" +
                "Кроме того, новый элемент вы можете добавить при помощи слайдера. Слайдером" +
                " указываете сколько новых элементов необходимо сгенерировать, затем жмёте \""
                + generateButton.getText() + "\". Наверняка, вас повеселит результат.\n" +
                "С помощью слайдера можно удалить какой-то конкретный элемент в коллекции," +
                " указав слайдером его номер и нажав \"" + removeButton.getText() + "\".\n" +
                "\nАвтор - Камакин Кирилл, P3102." +
                "\nСПб, 2017.";
        menuBar.getMenus().addAll(fileMenu, optionsMenu, helpMenu);

        //для добавления нового человека
        name.setPromptText("Имя");
        character.setPromptText("Характер");
        location.setPromptText("Местонахождение");
        relations.getItems().addAll(Relative.values());
        relations.setValue(Relative.sibling);
        final HBox box = new HBox(relations, picker);

        addVBox.getChildren().addAll(new Label("Информация о новом элементе:"),
                name, character, location, box);
        pane.add(addVBox, 1, 0);
        rootNode.getChildren().addAll(menuBar, pane, sliderHBox);

        picker.setOnAction((event) -> {
            if (picker.getValue().toEpochDay()<LocalDate.now().toEpochDay())
                picker.setValue(LocalDate.now());
        });
        saveOption.setOnAction((event) -> executor.submit(save));
        loadOption.setOnAction(event -> load());
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

        load();
        rootNode.autosize();
        primaryStage.setScene(new Scene(rootNode));
        primaryStage.setTitle("Лабораторная №6");
        primaryStage.show();
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        updateList();
        slider.requestFocus();
        primaryStage.setOnCloseRequest(event -> {
            executor.submit(save);
            executor.shutdown();
        });
    }
}