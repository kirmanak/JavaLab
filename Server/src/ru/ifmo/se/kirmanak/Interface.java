package ru.ifmo.se.kirmanak;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Interface, создающий графический интерфейс и обрабатывающий действия пользователя.
 * Работает с файлом, записанным в переменную окружения под названием jsonFile.
 */

public class Interface extends Application {
    private static final String title = "Лабораторная №7 (сервер)";
    private static TextField name;
    private static TextField character;
    private static TextField location;
    private static ChoiceBox<Relative> relations;
    private static DatePicker picker;
    private static TreeView<String> view;
    private static Slider slider;

    static String getName() {
        return name.getText().trim();
    }

    static String getCharacter() {
        return character.getText().trim();
    }

    static String getLocation() {
        return location.getText().trim();
    }

    static Relative getRelative() {
        return relations.getValue();
    }

    static LocalDate getDate() {
        return picker.getValue();
    }

    static TreeView<String> getView() {
        return view;
    }

    /**
     * Рисует интерфейс
     *
     * @param args аргументы для метода launch(String[] args)
     */
    static void draw(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для обновления размера слайдера в случае её изменения
     */
    static void updateSlider() {
        slider.setMax(EntryPoint.getCollection().size());
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
        //следующая строка честно похищена со StackOverflow для разбиения
        // диалогового окна на строки
        dialog.getDialogPane().getChildren().stream()
                .filter(node -> node instanceof Label)
                .forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
        return dialog;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final VBox rootNode = new VBox();
        final HBox sliderHBox = new HBox();
        final VBox addVBox = new VBox();
        final GridPane pane = new GridPane();
        slider = new Slider(1, EntryPoint.getCollection().size(), 5);
        name = new TextField();
        character = new TextField();
        location = new TextField();
        relations = new ChoiceBox<>();
        view = new TreeView<>(new TreeItem<>("Коллекция:"));
        picker = new DatePicker(LocalDate.now());
        picker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate object) {
                return object.format(DateTimeFormatter.ISO_DATE);
            }

            @Override
            public LocalDate fromString(String string) {
                return LocalDate.parse(string, DateTimeFormatter.ISO_DATE);
            }
        });

        //для remover'а и генератора
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        final Button removeButton = new Button("Удалить");
        final Button generateButton = new Button("Сгенерировать");
        sliderHBox.getChildren().addAll(slider, removeButton, generateButton);

        //для меню
        final Menu fileMenu = new Menu("Файл");
        final MenuItem saveOption = new MenuItem("Сохранить");
        final MenuItem loadOption = new MenuItem("Загрузить");
        final MenuItem exitOption = new MenuItem("Выход");
        fileMenu.getItems().addAll(loadOption, saveOption, exitOption);
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
        final ImageView imageView =
                new ImageView(new Image(
                        "ru/ifmo/se/kirmanak/img.png", true
                ));
        imageView.setFitHeight(384);
        imageView.setFitWidth(313);
        addVBox.getChildren().addAll(new Label("Информация о новом элементе:"),
                name, character, location, box, imageView);
        pane.add(addVBox, 1, 0);

        //описание TreeView
        ObservableList<Humans> collection = EntryPoint.getCollection();
        for (int i = 1, collectionSize = collection.size(); i <= collectionSize; i++) {
            Humans humans = collection.get(i - 1);
            TreeItem<String> item = humans.toTreeItem(i);
            view.getRoot().getChildren().add(item);
        }
        view.getRoot().setExpanded(true);
        pane.add(view, 0, 0);

        rootNode.getChildren().addAll(menuBar, pane, sliderHBox);

        //описание setOnAction-ов
        final ForkJoinPool pool = EntryPoint.getPool();
        Runnable saveRunnable = () ->
                System.err.println(Commands.save.doIt());
        Runnable loadRunnable = () ->
                System.err.println(Commands.load.doIt());
        saveOption.setOnAction((event) ->
                pool.submit(saveRunnable));
        loadOption.setOnAction((event) ->
                pool.submit(loadRunnable));
        exitOption.setOnAction((event) ->
                Platform.exit());
        removeButton.setOnAction((event) ->
                System.err.println(Commands.remove.doIt((int) slider.getValue())));
        generateButton.setOnAction((event) ->
                System.err.println(Commands.generate.doIt((int) slider.getValue())));
        removeLastOption.setOnAction((event) ->
                System.err.println(Commands.remove.doIt(EntryPoint.getCollection().size())));
        helpOption.setOnAction((event) -> dialogWindow("Справка", help).showAndWait());
        picker.setOnAction((event) -> {
            //нельзя установить дату грядущего возвращения ранее,
            //чем сегодня
            if (picker.getValue() == null || picker.getValue().toEpochDay() < LocalDate.now().toEpochDay())
                picker.setValue(LocalDate.now());
        });
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

        //отрисовываем
        loadOption.fire();
        updateSlider();
        primaryStage.setScene(new Scene(rootNode, 571, 571));
        primaryStage.setTitle(title);
        primaryStage.show();
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(false);
        slider.requestFocus();
        slider.setPrefWidth(pane.getWidth()
                - removeButton.getWidth()
                - generateButton.getWidth());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveOption.fire();
            System.err.println("Ожидаю сохранения данных...");
            pool.shutdown();
            try {
                if (!pool.awaitTermination(10, TimeUnit.SECONDS))
                    System.err.println("Слишком долгая запись, аварийное завершение");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }
}