import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Vector;

/**
 * EntryPoint, читающий консоль, чтобы определить команду, подлежащую выполнению.
 * Работает с файлом, записанным в переменную окружения под названием jsonFile.
 */

public class TextGenerator extends Application {
    /** Сама коллекция  */
    static Vector<Humans> collection = new Vector<>();
    private static GridPane layout;
    private static Slider slider;
    private static double vBoxHeight;

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(Commands.save));
        new Thread(Commands.load).start();
        try {
            synchronized (Commands.load) {
                Commands.load.wait();
            }
        } catch (InterruptedException ignored) {}
        launch(args);
    }

    /** Метод для обновления отображаемой коллекции в случае её изменения*/
    private static void updateList () {
        TreeItem<String> tree = new TreeItem<>("Коллекция: ");
        String list = Commands.print.doIt();
        if (!list.isEmpty()) {
            for (String s : list.split("\n")) {
                TreeItem<String> item =
                        new TreeItem<>(s.substring(s.indexOf(" "), s.indexOf(" ", s.indexOf(" ") + 1)));
                item.getChildren().add(new TreeItem<>(s));
                tree.getChildren().add(item);
            }
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

    private Dialog helpDialog() {
        Dialog dialog = new Dialog();
        dialog.setTitle("Справка");
        String help = "Назначение кнопок можно понять по их названиям, но назначение остальных элементов не" +
                " столь очевидно: три текстовых поля ввода используются для команды add, которая создаёт новый" +
                " элемент для коллекции на основе данных из этих трёх полей. Кроме того, этой команде нужны ещё" +
                " дата (не ранее текущего дня) и одно из отношений к Малышу. \n" +
                "Команды remove и generate используют слайдер, remove удаляет элемент, номер которого выбран на слайдере" +
                ", а generate генерирует ровно такое количество новых элементов.";
        dialog.setContentText(help);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.FINISH);
        //следующая строка честно похищена со stackoverflow
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

        Button[] buttons = new Button[Commands.values().length];
        VBox vBox = new VBox();
        for (int i = 0; i < buttons.length; i++) {
            switch (Commands.values()[i]) {
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
                        Commands.add.action(layout);
                        updateList();
                    });
                    break;

                case load:buttons[i] = new Button(Commands.load.name());
                    buttons[i].setTooltip(new Tooltip(Commands.load.toString()));
                    buttons[i].setOnAction((event) -> {
                        new Thread(Commands.load).start();
                        try {
                            synchronized (Commands.load) {
                                Commands.load.wait();
                            }
                        } catch (InterruptedException ignored) {}
                        updateList();
                    });
                    break;
                case save:buttons[i] = new Button(Commands.save.name());
                    buttons[i].setTooltip(new Tooltip(Commands.save.toString()));
                    buttons[i].setOnAction((event) ->
                            new Thread(Commands.save).start());
                    break;

                    /* Вместо ненужного print будет команда remove_last */
                case print: buttons[i] = new Button("remove_last");
                    buttons[i].setTooltip(new Tooltip("Удалить последний элемент."));
                    buttons[i].setOnAction((event -> {
                        System.err.println(Commands.remove.doIt(TextGenerator.collection.size()));
                        updateList();
                    }));
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
        vBox.getChildren().addAll(buttons);

        layout.add(vBox,1,1);
        MenuBar bar = new MenuBar(menu);
        layout.add(bar,0,0);

        TextField name = new TextField();
        name.setText("Имя");
        name.setPromptText("Имя");
        TextField character = new TextField();
        character.setText("Характер");
        character.setPromptText("Характер");
        TextField location = new TextField();
        location.setText("Местонахождение");
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
        primaryStage.sizeToScene();
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
    }
}