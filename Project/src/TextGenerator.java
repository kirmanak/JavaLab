import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.Vector;

/**
 * EntryPoint, читающий консоль, чтобы определить команду, подлежащую выполнению.
 * Работает с файлом, записанным в переменную окружения под названием jsonFile.
 */

public class TextGenerator extends Application {
    /**
     * Служебная переменная, служащая для чтения командной строки
     */
    static final Scanner scanner = new Scanner(System.in);
    /** Сама коллекция  */
    static Vector<Humans> collection = new Vector<>();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(Commands.save));
        Commands.load.doIt();
        launch(args);
    }

    private static void updateList (GridPane layout, Slider slider) {
        TreeItem<String> tree = new TreeItem<>("Коллекция: ");
        String list = Commands.print.doIt();
        for (String s : list.split("\n")) {
            TreeItem<String> item =
                    new TreeItem<>(s.substring(s.indexOf(" "),s.indexOf(" ", s.indexOf(" ")+1)));
            item.getChildren().add(new TreeItem<>(s));
            tree.getChildren().add(item);
        }
        tree.setExpanded(true);
        layout.getChildren().set(0, new TreeView<>(tree));
        slider.setMax(collection.size());
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane layout = new GridPane();

        TreeView<String> view = new TreeView<>();
        layout.add(view,0,0);

        Slider slider = new Slider(0, collection.size(), 1);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);

        updateList(layout,slider);

        TextField name = new TextField();
        name.setPromptText("Имя");
        TextField character = new TextField();
        character.setPromptText("Характер");
        TextField location = new TextField();
        location.setPromptText("Местонахождение");

        DatePicker picker = new DatePicker(LocalDate.now());

        Menu menu = new Menu("Меню");
        MenuItem[] items = new MenuItem[Commands.values().length+1];
        int i = 0;
        for (; i < items.length-1; i++) {
            switch (Commands.values()[i]) {
                case remove: items[i] = new MenuItem(Commands.remove.toString());
                    items[i].setOnAction((event) -> {
                    System.err.println(Commands.remove.doIt((int) slider.getValue()));
                    updateList(layout,slider);
                });
                    break;
                case add:items[i] = new MenuItem(Commands.add.toString());
                    items[i].setOnAction((event) -> {
                    Commands.add.action(layout);
                    updateList(layout,slider);
                });
                    break;
                case load:items[i] = new MenuItem(Commands.load.toString());
                    items[i].setOnAction((event) -> {
                        System.err.println(Commands.load.doIt());
                        updateList(layout,slider);
                });
                    break;
                case save:items[i] = new MenuItem(Commands.save.toString());
                    items[i].setOnAction((event) ->
                            new Thread(Commands.save).start());
                        //System.err.println(Commands.save.doIt()));
                    break;
                case print:items[i] = new MenuItem(Commands.save.toString());
                    items[i].setOnAction((event) -> updateList(layout,slider));
                    break;
                case generate:items[i] = new MenuItem(Commands.generate.toString());
                    items[i].setOnAction((event) -> {
                    System.err.println(Commands.generate.doIt((int) slider.getValue()));
                    updateList(layout,slider);
                });
                    break;
                default:
                    System.err.println("Ты забыл добавить новую команду.");
            }
        }
        items[i] = new MenuItem("remove_last - удалить последний элемент.");
        items[i].setOnAction((event -> {
            System.err.println(Commands.remove.doIt(TextGenerator.collection.size()));
            updateList(layout,slider);
        }));
        menu.getItems().addAll(items);
        layout.add(new MenuBar(menu),0,1);

        layout.add(name,0,2);
        layout.add(character,0,3);
        layout.add(location,0,4);
        layout.add(picker,0,5);
        layout.add(slider,0,6);
        layout.getColumnConstraints().add(new ColumnConstraints());
        layout.getRowConstraints().add(new RowConstraints());
        primaryStage.setTitle("Лабораторная №6");
        primaryStage.setScene(new Scene(layout));
        primaryStage.show();
    }
}