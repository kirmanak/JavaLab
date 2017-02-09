import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Vector;

/**
 * remove element - удалить элемент под номером element. <br>
 * remove_last - удалить последний элемент. <br>
 * save - сохранить коллекцию в JSON-файл. <br>
 * load - считать коллекцию из JSON-файла. <br>
 * print - напечатать содержимое коллекции и количество элементов. <br>
 * exit - сохранить и выйти. <br>
 * help - вызвать эту справку. <br>
 */

public class TextGenerator {
    private static final File jsonFile = new File(System.getenv("jsonFile"));
    private static Vector<Adults> collection = new Vector<>();

    public static void main(String[] args) {
        load();
        label:
        while (true) {
            System.out.print("Введите команду:");
            String command = new Scanner(System.in).nextLine();
            switch (command) {
                case "exit":
                    save();
                    break label;
                case "print":
                    print();
                    break;
                case "help":
                    help();
                    break;
                case "save":
                    save();
                    break;
                case "load":
                    load();
                    break;
                default:
                    if (command.contains("remove")) {
                        remove(command);
                    } else {
                        System.out.println("Моя твоя не понимай.");
                    }
            }
        }
    }

    /**
     * Печатает содержимое коллекции и количество элементов в ней
     */
    public static void print() {
        System.out.println("В коллекции " + collection.size() + " элемент(ов)(а):");
        collection.forEach(adult -> System.out.println(adult.toString()));
    }

    /**
     * Удаляет элемент на позиции index
     * @param index позиция удаляемого элемента
     */
    private static void remove (int index) {
        if (index < collection.size() && index >= 0) {
            collection.remove(index);
        } else {
            System.out.println("Нет такого элемента.");
        }
    }

    /**
     * Метод, определяющий номер элемента, подлежащего удалению
     * @param command - строка, содержащая слово "remove" и номер элемента либо _last
     */
    public static void remove(String command) {
        try {
            remove(Integer.parseInt(command.substring(7)) - 1);
            System.out.println(Integer.parseInt(command.substring(7)) + "-й элемент удалён.");
        } catch (NumberFormatException | StringIndexOutOfBoundsException err) {
            if (command.equals("remove_last")) {
                remove(collection.size() - 1);
                System.out.println("Последний элемент удалён.");
            } else System.out.println("Моя твоя не понимай.");
        }
    }

    /**
     * Считывает коллекцию из JSON-файла
     */
    public static void load() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(jsonFile));
            collection.clear();
            collection = new Gson().fromJson(br.readLine(), new TypeToken<Vector<Adults>>(){}.getType());
            br.close();
            collection.sort(Comparator.comparing(Adults::toString));
        } catch (IOException | NullPointerException err) {
            System.out.println("Файл ввода не найден. Не буду ничего читать.");
        }
    }

    /**
     * Записывает коллекцию в JSON-файл.
     */
    public static void save() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(jsonFile));
            pw.flush();
            pw.print(new Gson().toJson(collection));
            pw.close();
        } catch (IOException | NullPointerException err) {
            System.out.println("Файл ввода не найден. Не буду ничего писать.");
        }
    }

    /**
     * Печатает справку по командам приложения
     */
    public static void help() {
        System.out.println("Я могу:");
        System.out.println("1. remove element - удалить элемент под номером element.");
        System.out.println("2. remove - удалить последний элемент.");
        System.out.println("3. save - сохранить коллекцию в JSON-файл.");
        System.out.println("4. load - считать коллекцию из JSON-файла.");
        System.out.println("5. print - напечатать коллекцию.");
        System.out.println("6. exit - сохранить и выйти.");
        System.out.println("7. help - вызвать эту справку.");
    }
}
