package ru.ifmo.se.kirmanak;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.concurrent.ForkJoinPool;

/**
 * EntryPoint, создающий графический интерфейс и обрабатывающий действия пользователя.
 * Работает с файлом, записанным в переменную окружения под названием jsonFile.
 */

public class Main extends Application {
    static TextField name;
    static TextField character;
    static TextField location;
    static ChoiceBox<Relative> relations;
    static DatePicker picker;
    private static GridPane pane;
    private static Slider slider;
    private static Button removeButton;
    private static Button generateButton;

    public static void main(String[] args) {
        Commands.collection.addListener((ListChangeListener<Humans>) c ->
                Platform.runLater(Main::updateList)
        );
        try {
            launch();
        } catch (Exception e) {
            System.err.println("Я сломался");
            e.printStackTrace(System.err);
        }
    }

    /** Метод для обновления отображаемой коллекции в случае её изменения*/
    private static void updateList () {
        TreeItem<String> tree = new TreeItem<>("Коллекция: ");
        for (Humans humans : Commands.collection) {
            TreeItem<String> item =
                    new TreeItem<>(humans.getName());
            item.getChildren().add(new TreeItem<>(humans.toString()));
            tree.getChildren().add(item);
        }
        tree.setExpanded(true);
        TreeView<String> view = new TreeView<>(tree);
        view.setPrefWidth(700);
        view.setPrefHeight(pane.getHeight());
        pane.add(view, 0, 0);
        slider.setMinWidth(pane.getWidth()
                - removeButton.getWidth()
                - generateButton.getWidth());
        slider.setMax(Commands.collection.size());
        if (slider.getMax() < 3) {
            slider.setMin(1);
            slider.setMax(5);
        }
    }

    /**
     * Метод, конструирующий диалоговые окна, готовые к отображению
     */
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
        pane.setPrefHeight(300);
        slider = new Slider(1, Commands.collection.size(), 5);
        VBox addVBox = new VBox();
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

        //для меню
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
                " элементов класса Humans. В правой части вы можете описать новый" +
                " элемент и затем добавить его при помощи подменю \"" + addOption.getText()
                + "\" в меню \"" + optionsMenu.getText() + "\".\n" +
                "Кроме того, новый элемент вы можете добавить при помощи слайдера. Слайдером" +
                " указываете сколько новых элементов необходимо сгенерировать, затем жмёте \""
                + generateButton.getText() + "\". Наверняка, вас повеселит результат.\n" +
                "С помощью слайдера можно удалить какой-то конкретный элемент в коллекции," +
                " указав слайдером его номер и нажав \"" + removeButton.getText() + "\".\n" +
                "Есть так же команды \"" + loadOption.getText() + "\" и \"" + saveOption.getText()
                + "\" в подменю \"" + fileMenu.getText() + "\", причём первая выполняется " +
                "при каждой загрузке, а вторая при каждом выходе из программы.\n" +
                "\nАвтор - Камакин Кирилл, гр. P3102.\nСПб, 2017.";
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
        addVBox.setPrefHeight(pane.getHeight());
        //элемент (0;0) добавляется в updateList()
        pane.add(addVBox, 1, 0);

        rootNode.getChildren().addAll(menuBar, pane, sliderHBox);

        //описание setOnAction-ов
        picker.setOnAction((event) -> {
            //нельзя установить дату грядущего возвращения ранее,
            //чем сегодня
            if (picker.getValue() == null || picker.getValue().toEpochDay() < LocalDate.now().toEpochDay())
                picker.setValue(LocalDate.now());
        });
        final ForkJoinPool pool = new ForkJoinPool();
        Runtime.getRuntime().addShutdownHook(new Thread(pool::shutdownNow));
        saveOption.setOnAction((event) -> {
            Runnable runnable = () -> System.err.println(Commands.save.doIt());
            pool.submit(runnable).join();
        });
        loadOption.setOnAction((event) -> {
            Runnable runnable = () -> System.err.println(Commands.load.doIt());
            pool.submit(runnable);
        });
        removeButton.setOnAction((event) ->
                System.err.println(Commands.remove.doIt((int) slider.getValue())));
        generateButton.setOnAction((event) ->
                System.err.println(Commands.generate.doIt((int) slider.getValue()))
        );
        removeLastOption.setOnAction((event) ->
                System.err.println(Commands.remove.doIt(Commands.collection.size()))
        );
        addOption.setOnAction((event) -> {
            if (name.getText().isEmpty() ||
                    character.getText().isEmpty() ||
                    location.getText().isEmpty()) {
                System.err.println("Пустые поля для ввода");
                String errText = "Вы должны заполнить поля " + name.getPromptText() +
                        ", " + character.getPromptText() + ", " + location.getPromptText();
                dialogWindow("Ошибка", errText).showAndWait();
            } else {
                System.err.println(Commands.add.doIt());
            }
        });
        helpOption.setOnAction((event) -> dialogWindow("Справка", help).showAndWait());

        //отрисовываем
        loadOption.fire();
        updateList();
        rootNode.autosize();
        primaryStage.setScene(new Scene(rootNode));
        primaryStage.setTitle("Лабораторная №6");
        primaryStage.show();
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        slider.requestFocus();
        primaryStage.setOnCloseRequest(event -> {
            saveOption.fire();
            pool.shutdown();
        });
    }
}