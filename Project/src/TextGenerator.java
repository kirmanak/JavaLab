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

public class TextGenerator extends Application {
    /** Сама коллекция  */
    static volatile Vector<Humans> collection = new Vector<>();
    private static final Runnable load = () -> System.err.println(Commands.load.doIt());
    private static final Runnable save = () -> System.err.println(Commands.save.doIt());
    static GridPane layout;
    private static Slider slider;
    private static double vBoxHeight;

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(save));
        IO(load);
        launch(args);
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
        for (Humans humans : TextGenerator.collection) {
            TreeItem<String> item =
                    new TreeItem<>(humans.getName());
            item.getChildren().add(new TreeItem<>(humans.toString()));
            tree.getChildren().add(item);
        }
        tree.setExpanded(true);
        TreeView<String> view = new TreeView<>(tree);
        view.setMinWidth(800);
        view.setMaxHeight(vBoxHeight);
        view.setMinHeight(vBoxHeight);
        layout.add(view,0,1);
        slider.setMax(collection.size());
        if (slider.getMax() == 0) {
            slider.setMin(1);
            slider.setMax(5);
        }
    }

    /** Метод, создающий диалоговое окно со справкой по программе */
    private static Dialog helpDialog() {
        Dialog dialog = new Dialog();
        dialog.setTitle("Справка");
        String help = "Назначение кнопок можно понять по их названиям," +
                " но назначение остальных элементов не" +
                " столь очевидно: три текстовых поля ввода используются для команды add," +
                " которая создаёт новый" +
                " элемент для коллекции на основе данных из этих трёх полей. Кроме того," +
                " этой команде нужны ещё" +
                " дата (не ранее текущего дня) и одно из отношений к Малышу. \n" +
                "Команды remove и generate используют слайдер, remove удаляет элемент," +
                " номер которого выбран на слайдере" +
                ", а generate генерирует ровно такое количество новых элементов.";
        dialog.setContentText(help);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.FINISH);
        //следующая строка честно похищена со StackOverflow для разбиения справки на строки
        dialog.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
        return dialog;
    }

    @Override
    public void start(Stage primaryStage) {
        layout = new GridPane();
        Menu menu = new Menu("Справка");
        MenuItem help = new MenuItem("Получить справку");
        help.setOnAction((event) -> helpDialog().showAndWait());
        menu.getItems().add(help);

        slider = new Slider(1, collection.size(), 5);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);

        //кнопок больше на одну, чем команд из-за remove_last,
        //для которого я не стал делать отдельную команду
        Button[] buttons = new Button[Commands.values().length+1];
        buttons[0] = new Button("remove_last");
        buttons[0].setTooltip(new Tooltip("Удалить последний элемент."));
        buttons[0].setOnAction((event -> {
            System.err.println(Commands.remove.doIt(TextGenerator.collection.size()));
            updateList();
        }));
        for (int i = 1; i < buttons.length; i++) {
            switch (Commands.values()[i-1]) {
                case remove: buttons[i] = new Button(Commands.remove.name());
                    buttons[i].setTooltip(new Tooltip(Commands.remove.toString()));
                    buttons[i].setOnAction((event) -> {
                        System.err.println(Commands.remove.doIt((int) slider.getValue()));
                        updateList();
                    });
                    break;

                case add:buttons[i] = new Button(Commands.add.name());
                    buttons[i].setTooltip(new Tooltip(Commands.add.toString()));
                    buttons[i].setOnAction((event) -> {
                        System.err.println(Commands.add.doIt());
                        updateList();
                    });
                    break;

                case load:buttons[i] = new Button(Commands.load.name());
                    buttons[i].setTooltip(new Tooltip(Commands.load.toString()));
                    buttons[i].setOnAction((event) -> {
                        IO(load);
                        updateList();
                    });
                    break;

                case save:buttons[i] = new Button(Commands.save.name());
                    buttons[i].setTooltip(new Tooltip(Commands.save.toString()));
                    buttons[i].setOnAction((event) -> IO(save));
                    break;

                case generate:buttons[i] = new Button(Commands.generate.name());
                    buttons[i].setTooltip(new Tooltip(Commands.generate.toString()));
                    buttons[i].setOnAction((event) -> {
                        System.err.println(Commands.generate.doIt((int) slider.getValue()));
                        updateList();
                    });
                    break;

                default:
                    System.err.println("Ты забыл добавить новую команду.");
            }
        }
        VBox vBox = new VBox();
        vBox.getChildren().addAll(buttons);

        layout.add(vBox,1,1);
        MenuBar bar = new MenuBar(menu);
        layout.add(bar,0,0);

        TextField name = new TextField();
        name.setPromptText("Имя");
        TextField character = new TextField();
        character.setPromptText("Характер");
        TextField location = new TextField();
        location.setPromptText("Местонахождение");

        ChoiceBox<Relative> relations = new ChoiceBox<>();
        relations.getItems().addAll(Relative.values());
        relations.setValue(Relative.sibling);
        DatePicker picker = new DatePicker(LocalDate.now());
        picker.setOnAction((event) -> {
            if (picker.getValue().toEpochDay()<LocalDate.now().toEpochDay())
                picker.setValue(LocalDate.now());
        });
        HBox hBox = new HBox(relations,picker);

        layout.add(name,0,2);
        layout.add(character,0,3);
        layout.add(location,0,4);
        layout.add(hBox,0,5);
        layout.add(slider,1,0);

        layout.add(new Label("Вакантное"), 1, 2);
        layout.add(new Label("место"), 1, 3);
        layout.add(new Label("для"), 1, 4);
        layout.add(new Label("рекламы"), 1, 5);

        primaryStage.setTitle("Лабораторная №6");
        primaryStage.setScene(new Scene(layout));
        primaryStage.show();

        vBoxHeight = vBox.getHeight();
        updateList();
        layout.autosize();
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
    }
}